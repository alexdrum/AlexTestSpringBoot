package com.alextest.crawler.controller;

import com.alextest.Apis;
import com.alextest.crawler.CrawlerConst;
import com.alextest.crawler.CrawlerUtils;
import com.alextest.crawler.entity.CompanyEntity;
import com.alextest.crawler.exception.SimpleException;
import com.alextest.crawler.service.ProxyPool;
import com.alextest.crawler.vo.ProxyVo;
import com.alextest.util.DateUtils;
import com.alextest.util.TestUtils;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.util.List;

import static com.alextest.crawler.CrawlerConst.*;

/**
 * Created by alexdrum on 2017/7/24.
 */
@RestController
@SpringBootApplication
public class CompanyInfoCrawlerController implements Apis {

    @Autowired
    ProxyPool proxyPool;

    /**
     * controller测试方法
     */
    @RequestMapping(value = CONTROLLER_TEST, method = RequestMethod.GET)
    public void controllerTest(){
        proxyPool.getProxyVoMap();
    }

    /**
     * 通过种子信息向天眼通抓取企业信息
     */
    @RequestMapping(value = GET_COMPANY_INFO, method = RequestMethod.GET)
    public void saveFromTianYanCha() throws IOException {
        boolean isDev = false;

        // 从excel文件中获取种子关键字集合
        List<String> keyList = Lists.newArrayList();
        if (isDev) {
            keyList.add("自如");
        }
        String filePath = CrawlerConst.WIN_ROOT;
        String os = System.getProperty("os.name");
        if (!os.toLowerCase().startsWith("win")) {
            filePath = CrawlerConst.LINUX_ROOT;
        }
        String fullPathFileName = filePath + SEED_FILE;
        InputStream stream = new FileInputStream(fullPathFileName);

        // 抓取数据
        try {

            long nowTime = DateUtils.formatNow2Long();
            XSSFSheet sheet;
            XSSFRow row;
            XSSFWorkbook wb = null;

            if (!isDev) {
                try {
                    wb = new XSSFWorkbook(stream);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                sheet = wb.getSheetAt(0);
                // 得到总行数
                int rowNum = sheet.getLastRowNum();
                row = sheet.getRow(0);
                // 正文内容应该从第二行开始,第一行为表头的标题
                for (int i = 1; i <= rowNum; i++) {
                    row = sheet.getRow(i);
                    XSSFCell xssfCell = row.getCell(0);
                    try {
                        String keyWord = xssfCell.getRichStringCellValue().getString();
                        //把刚获取的列存入list
                        keyList.add(keyWord);
                        TestUtils.log("已读取第" + i + "条数据：" + keyWord);
                    } catch (IllegalStateException e) {
                        continue;
                    }
                }
            }

            TestUtils.log("已经初始化关键字，共：" + keyList.size() + "个。");

            // 新建导出数据文件
            String path = CrawlerConst.WIN_ROOT;
            String fileName = "企业名录导入数据";
            String fileType = "xlsx";
            List<CompanyEntity> list = Lists.newArrayList();

            TestUtils.log("开始抓取企业数据：");
            int CrawlerCounter = 1;
            // 通过关键字从目标网站上抓取数据
            for (String keyWord : keyList) {
                try {

                    // 抓取网页
                    String targetURL = TIAN_YAN_CHA_PREFIX + keyWord + TIAN_YAN_CHA_SUFFIX;
                    Document doc;
                    try {
                        // 获取一个动态代理IP
                        ProxyVo proxyVo = proxyPool.getProxy();
                        // 获取网页
                        doc = CrawlerUtils.getDocumentFromURLWithProxy(targetURL, proxyVo);
                    } catch (IOException ioException) {
                        TestUtils.log("获取网页失败！");
                        ioException.printStackTrace();
                        break;
                    }
                    TestUtils.log("已成功获取当前第" + CrawlerCounter + "个关键词：" + keyWord + " 的搜索结果页面；");
                    TestUtils.log(doc.toString());

                    // 验证网页内容
                    Elements elements = doc.getElementsByClass("search_result_single search-2017 pb20 pt20 pl30 pr30");
                    if (CollectionUtils.isEmpty(elements)) {
                        TestUtils.log("啥也没爬着！");
                    }

                    // 解析公司信息
                    int resultCounter = 1;
                    for (Element element : elements) {
                        TestUtils.log("已成功获取当前第" + CrawlerCounter + "个关键词：" + keyWord + " 的第" + resultCounter + "个搜索结果详细信息；");

                        // 获取公司名称
                        String companyName = "";
                        Elements nameDiv = new Elements();
                        try {
                            nameDiv = element.getElementsByClass("mr20 search_left_icon");
                            companyName = nameDiv.get(0).child(0).attr("alt");
                        } catch (Exception e) {
                            TestUtils.log("没有公司名称信息！");
                        }

                        // 获取图片文件并保存到图片平台
                        String picURL = "";
                        try {
                            picURL = nameDiv.get(0).child(0).attr("src");
                        } catch (Exception e) {
                            TestUtils.log("没有公司Logo信息！");
                        }

                        // 获取省份
                        String province = "";
                        try {
                            Elements provinceDiv = element.getElementsByClass("search_right_item");
                            String provinceDivString = provinceDiv.get(0).child(0).child(1).toString();
                            province = provinceDivString.substring(provinceDivString.indexOf("</i>") + 4, provinceDivString.indexOf(" \n <div class=\"notInIE8 position-abs\""));
                        } catch (Exception e) {
                            TestUtils.log("没有所在省份信息！");
                        }

                        // 获得法人姓名、注册资金、注册日期、品牌
                        Elements infoDiv = element.getElementsByClass("search_row_new");

                        // 注册法人
                        String createName = "";
                        try {
                            createName = infoDiv.get(0).child(0).child(0).text();
                        } catch (Exception e) {
                            TestUtils.log("没有注册法人信息！");
                        }

                        // 注册资金
                        String registeredCapitalString = infoDiv.get(0).child(1).child(0).text();
                        Double registeredCapital = 0D;
                        try {
                            registeredCapital = Double.valueOf(registeredCapitalString.substring(0, registeredCapitalString.indexOf("万人民币")).trim());
                            registeredCapital = registeredCapital * 10000L;
                        } catch (Exception e) {
                            TestUtils.log("没有注册资金信息！");
                        }

                        // 成立日期
                        Integer foundDate = 0;
                        try {
                            String foundDateString = infoDiv.get(0).child(2).child(0).text();
                            foundDate = Integer.valueOf(foundDateString.replace("-", ""));
                        } catch (Exception e) {
                            TestUtils.log("没有建立日期信息！");
                        }

                        // 品牌信息
                        String brand = "";
                        try {
                            brand = infoDiv.get(0).child(3).child(0).child(2).text();
                        } catch (Exception exception) {
                            TestUtils.log("没有品牌信息");
                        }

                        CompanyEntity companyEntity = new CompanyEntity();
                        companyEntity.setName(companyName);
                        TestUtils.log("第" + CrawlerCounter + "个关键词：" + keyWord + " 的第" + resultCounter + "个结果的公司名称：" + companyName);
                        companyEntity.setCreateName(createName);
                        TestUtils.log("第" + CrawlerCounter + "个关键词：" + keyWord + " 的第" + resultCounter + "个结果的法人姓名：" + createName);
                        companyEntity.setProvince(province);
                        TestUtils.log("第" + CrawlerCounter + "个关键词：" + keyWord + " 的第" + resultCounter + "个结果的所在省份：" + province);
                        companyEntity.setRegisteredCapital(registeredCapital);
                        TestUtils.log("第" + CrawlerCounter + "个关键词：" + keyWord + " 的第" + resultCounter + "个结果的注册资金：" + registeredCapital);
                        companyEntity.setFoundDate(foundDate);
                        TestUtils.log("第" + CrawlerCounter + "个关键词：" + keyWord + " 的第" + resultCounter + "个结果的成立日期：" + foundDate);
                        companyEntity.setBrand(brand);
                        TestUtils.log("第" + CrawlerCounter + "个关键词：" + keyWord + " 的第" + resultCounter + "个结果的品牌信息：" + brand);
                        companyEntity.setLogo(picURL);
                        TestUtils.log("第" + CrawlerCounter + "个关键词：" + keyWord + " 的第" + resultCounter + "个结果的图片地址：" + picURL);
                        companyEntity.setCreateTime(nowTime);
                        list.add(companyEntity);
                        resultCounter++;
                    }
                } catch (Exception e){
                    break;
                }
                CrawlerCounter++;
                // 休息5秒钟
                Thread.sleep(5000);
            }

            // 写结果文件
            String title[] = {"公司名称", "企业法人", "所在省份", "注册资金(元)", "建立日期", "品牌名称", "图片地址", "抓取时间"};
            writer(path, fileName, fileType, list, title);

            wb.close();
        } catch (Exception e) {
            TestUtils.log(e);
        } finally {
            stream.close();
        }
    }

    @SuppressWarnings("resource")
    public static void writer(String path, String fileName, String fileType, List<CompanyEntity> list, String titleRow[]) throws IOException, SimpleException {

        //创建工作文档对象
        Workbook wb = new XSSFWorkbook();
        String excelPath = path + File.separator + fileName + "." + fileType;
        File file = new File(excelPath);
        Sheet sheet = null;

        //创建sheet对象
        if (!file.exists()) {
            sheet = wb.createSheet("sheet1");
            OutputStream outputStream = new FileOutputStream(excelPath);
            wb.write(outputStream);
            outputStream.flush();
            outputStream.close();
        }

        //创建sheet对象
        if (sheet == null) {
            sheet = (Sheet) wb.createSheet("sheet1");
        }

        //添加表头
        Row row = sheet.createRow(0);
        Cell cell = row.createCell(0);
        row.setHeight((short) 540);
        cell.setCellValue("企业信息");    //创建第一行

        CellStyle style = wb.createCellStyle(); // 样式对象
        // 设置单元格的背景颜色为淡蓝色
        style.setFillForegroundColor(HSSFColor.PALE_BLUE.index);

        style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);// 垂直
        style.setAlignment(CellStyle.ALIGN_CENTER);// 水平
        style.setWrapText(true);// 指定当单元格内容显示不下时自动换行

        cell.setCellStyle(style); // 样式，居中

        Font font = wb.createFont();
        font.setBoldweight(Font.BOLDWEIGHT_BOLD);
        font.setFontName("宋体");
        font.setFontHeight((short) 280);
        style.setFont(font);
        // 单元格合并
        // 四个参数分别是：起始行，起始列，结束行，结束列
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 7));
        sheet.autoSizeColumn(5200);

        row = sheet.createRow(1);    //创建第二行
        for (int i = 0; i < titleRow.length; i++) {
            cell = row.createCell(i);
            cell.setCellValue(titleRow[i]);
            cell.setCellStyle(style); // 样式，居中
            sheet.setColumnWidth(i, 20 * 256);
        }
        row.setHeight((short) 540);

        //循环写入行数据
        for (int i = 0; i < list.size(); i++) {
            row = (Row) sheet.createRow(i + 2);
            row.setHeight((short) 500);
            row.createCell(0).setCellValue((list.get(i)).getName());
            row.createCell(1).setCellValue((list.get(i)).getCreateName());
            row.createCell(2).setCellValue((list.get(i)).getProvince());
            row.createCell(3).setCellValue((list.get(i)).getRegisteredCapital());
            row.createCell(4).setCellValue((list.get(i)).getFoundDate());
            row.createCell(5).setCellValue((list.get(i)).getBrand());
            row.createCell(6).setCellValue((list.get(i)).getLogo());
            row.createCell(7).setCellValue((list.get(i)).getCreateTime());
        }

        //创建文件流
        OutputStream stream = new FileOutputStream(excelPath);
        try {
            //写入数据
            wb.write(stream);
        } catch (Exception e) {
            TestUtils.log("写入文件失败");
        } finally {
            //关闭文件流
            stream.close();
        }
        wb.close();
    }
}
