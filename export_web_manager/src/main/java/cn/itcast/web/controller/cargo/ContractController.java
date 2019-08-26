package cn.itcast.web.controller.cargo;

import cn.itcast.domain.cargo.Contract;
import cn.itcast.domain.cargo.ContractExample;
import cn.itcast.vo.ContractProductVo;
import cn.itcast.domain.system.User;
import cn.itcast.service.cargo.ContractProductService;
import cn.itcast.service.cargo.ContractService;
import cn.itcast.web.controller.BaseController;
import cn.itcast.web.utils.DownloadUtil;
import com.alibaba.dubbo.config.annotation.Reference;
import com.github.pagehelper.PageInfo;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Controller
@RequestMapping("/cargo/contract")
public class ContractController extends BaseController {

    @Reference
    private ContractService contractService;
    @Reference
    private ContractProductService contractProductService;

    /**
     * 列表分页查询
     */
    @RequestMapping("/list")
    public String list(@RequestParam(defaultValue = "1") Integer pageNum,
                       @RequestParam(defaultValue = "5") Integer pageSize) {
        ContractExample contractExample = new ContractExample();
        //进行细粒度权限控制
        User user = getLoginUser();
        ContractExample.Criteria contractExampleCriteria = contractExample.createCriteria();
        if (user.getDegree() == 4) {
            //普通员工,只能看自己创建的合同
            contractExampleCriteria.andCreateByEqualTo(user.getId());
        } else if (user.getDegree() == 3) {
            //部门经理，能查看部门的合同
            contractExampleCriteria.andCreateDeptEqualTo(user.getDeptId());
        } else if (user.getDegree() == 2) {
            //能查看本部门和子孙部门的合同
            PageInfo<Contract> pageInfo = contractService.selectByDeptId(user.getDeptId(), pageNum, pageSize);
            request.setAttribute("page", pageInfo);
            return "/cargo/contract/contract-list";
        }
        PageInfo<Contract> pageInfo = contractService.findByPage(contractExample, pageNum, pageSize);
        request.setAttribute("page", pageInfo);
        return "/cargo/contract/contract-list";
    }

    /**
     * 进入添加页面
     */
    @RequestMapping("/toAdd")
    public String toAdd() {
        return "/cargo/contract/contract-add";
    }

    /**
     * 添加/修改
     */
    @RequestMapping("/edit")
    public String edit(Contract contract) {
        //设置公司id和名称
        contract.setCompanyId(getLoginCompanyId());
        contract.setCompanyName(getLoginCompanyName());


        //通过是否有合同id判断是添加/修改合同
        if (StringUtils.isEmpty(contract.getId())) {
            //添加
            //设置创建者
            contract.setCreateBy(getLoginUser().getId());
            //设置创建者部门
            contract.setCreateDept(getLoginUser().getDeptId());
            contractService.save(contract);
        } else {
            contractService.update(contract);
        }
        return "redirect:/cargo/contract/list.do";
    }

    /**
     * 进入修改页面
     */
    @RequestMapping("/toUpdate")
    public String toUpdate(String id) {
        Contract contract = contractService.findById(id);
        request.setAttribute("contract", contract);
        return "/cargo/contract/contract-update";
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public String delete(String id) {
        contractService.delete(id);
        return "redirect:/cargo/contract/list.do";
    }

    /**
     * 查看
     */
    @RequestMapping("/toView")
    public String toView(String id) {
        Contract contract = contractService.findById(id);
        request.setAttribute("contract", contract);
        return "/cargo/contract/contract-view";
    }

    /**
     * 提交
     */
    @RequestMapping("/submit")
    public String submit(String id) {
        Contract contract = contractService.findById(id);
        contract.setState(1);
        contractService.update(contract);
        return "redirect:/cargo/contract/list.do";
    }

    /**
     * 取消
     */
    @RequestMapping("/cancel")
    public String cancel(String id) {
        Contract contract = contractService.findById(id);
        contract.setState(0);
        contractService.update(contract);
        return "redirect:/cargo/contract/list.do";
    }

    /**
     * 进入出货表页面
     * http://localhost:8080/cargo/contract/print.do
     */
    @RequestMapping("/print")
    public String print() {
        return "cargo/print/contract-print";
    }

    /**
     * 导出出货表
     * http://localhost:8080/cargo/contract/printExcel.do?inputDate=
     */
    @RequestMapping("/printExcel")
    public void printExcel(String inputDate) throws IOException {
        //printFreedom(inputDate);
        //printTemplate(inputDate);
        // 第一步：导出第一行
        // a. 创建工作簿
        Workbook workbook = new SXSSFWorkbook();
        // b. 创建工作表
        Sheet sheet = workbook.createSheet("导出出货表");
        // 设置列宽
        sheet.setColumnWidth(0, 256 * 5);
        sheet.setColumnWidth(1, 256 * 15);
        sheet.setColumnWidth(2, 256 * 26);
        sheet.setColumnWidth(3, 256 * 15);
        sheet.setColumnWidth(4, 256 * 29);
        sheet.setColumnWidth(5, 256 * 15);
        sheet.setColumnWidth(6, 256 * 15);
        sheet.setColumnWidth(7, 256 * 15);
        sheet.setColumnWidth(8, 256 * 15);

        // 合并单元格  开始行0  结束行0  开始列1  结束列8
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 1, 8));

        // c. 创建第一行
        Row row = sheet.createRow(0);
        // 设置行高
        row.setHeightInPoints(36);
        // d. 创建第一行第二列
        Cell cell = row.createCell(1);
        // e. 设置单元格内容
        // 2019-06 --> 2019年6月份出货表    2019-11
        String result = inputDate.replaceAll("-0", "-").replaceAll("-", "年") + "月份出货表";
        cell.setCellValue(result);
        // 设置单元格样式
        cell.setCellStyle(this.bigTitle(workbook));

        // 第二步：导出第二行
        String[] titles = {"客户", "订单号", "货号", "数量", "工厂", "工厂交期", "船期", "贸易条款"};
        row = sheet.createRow(1);
        row.setHeightInPoints(26);
        // 创建第二行的每一列
        for (int i = 0; i < titles.length; i++) {
            cell = row.createCell(i + 1);
            // 设置列内容
            cell.setCellValue(titles[i]);
            // 设置列样式
            cell.setCellStyle(this.title(workbook));
        }

        // 第三步：导出数据行, 从第三行开始
        List<ContractProductVo> list =
                contractProductService.findByShipTime(getLoginCompanyId(), inputDate);
        if (list != null && list.size() > 0) {
            int num = 2;
            for (ContractProductVo cp : list) {
                for (int i = 1; i < 100000; i++) {  //------在这里添加循环模拟百万数据
                    row = sheet.createRow(num++);

                    cell = row.createCell(1);
                    cell.setCellValue(cp.getCustomName());

                    cell = row.createCell(2);
                    cell.setCellValue(cp.getContractNo());

                    cell = row.createCell(3);
                    cell.setCellValue(cp.getProductNo());


                    cell = row.createCell(4);
                    if (cp.getCnumber() == null) {
                        cell.setCellValue("");
                    } else {
                        cell.setCellValue(cp.getCnumber());
                    }

                    cell = row.createCell(5);
                    cell.setCellValue(cp.getFactoryName());

                    cell = row.createCell(6);
                    cell.setCellValue(cp.getDeliveryPeriod());

                    cell = row.createCell(7);
                    cell.setCellValue(cp.getShipTime());

                    cell = row.createCell(8);
                    cell.setCellValue(cp.getTradeTerms());
                }
            }
        }

        // 第四步：导出下载
        DownloadUtil downloadUtil = new DownloadUtil();
        // 缓冲流
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        // excel文件流---> 缓冲流
        workbook.write(bos);
        // 下载:  (缓冲流---> response输出流)
        downloadUtil.download(bos, response, "出货表.xlsx");
        workbook.close();
    }

    private void printTemplate(String inputDate) throws IOException {
        InputStream in =
                session.getServletContext().getResourceAsStream("/make/xlsprint/tOUTPRODUCT.xlsx");
        Workbook workbook = new XSSFWorkbook(in);
        //获取工作表
        Sheet sheet = workbook.getSheetAt(0);
        //获取第一行
        Row row = sheet.getRow(0);
        Cell cell = row.getCell(1);
        //2019-08
        String result = inputDate.replaceAll("-0", "-")
                .replaceAll("-", "年") + "月份出货表";
        cell.setCellValue(result);
        //获取第三行，主要为了获取样式
        row = sheet.getRow(2);
        CellStyle[] cellStyles = new CellStyle[8];
        for (int i = 0; i < 8; i++) {
            cell = row.getCell(i + 1);
            cellStyles[i] = cell.getCellStyle();
        }
        //导出数据
        List<ContractProductVo> list = contractProductService.findByShipTime(getLoginCompanyId(), inputDate);
        for (int i = 0; i < list.size(); i++) {
            ContractProductVo cp = list.get(i);
            row = sheet.createRow(i + 2);
            cell = row.createCell(1);
            cell.setCellValue(cp.getCustomName());
            cell.setCellStyle(cellStyles[0]);
            cell = row.createCell(2);
            cell.setCellValue(cp.getContractNo());
            cell.setCellStyle(cellStyles[1]);
            cell = row.createCell(3);
            cell.setCellValue(cp.getProductNo());
            cell.setCellStyle(cellStyles[2]);
            cell = row.createCell(4);
            if (cp.getCnumber() == null) {
                cell.setCellValue("");
            } else {
                cell.setCellValue(cp.getCnumber());
            }
            cell.setCellStyle(cellStyles[3]);
            cell = row.createCell(5);
            cell.setCellValue(cp.getFactoryName());
            cell.setCellStyle(cellStyles[4]);
            cell = row.createCell(6);
            cell.setCellValue(cp.getDeliveryPeriod());
            cell.setCellStyle(cellStyles[5]);
            cell = row.createCell(7);
            cell.setCellValue(cp.getShipTime());
            cell.setCellStyle(cellStyles[6]);
            cell = row.createCell(8);
            cell.setCellValue(cp.getTradeTerms());
            cell.setCellStyle(cellStyles[7]);
        }
        //导出下载
        DownloadUtil downloadUtil = new DownloadUtil();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        workbook.write(bos);
        downloadUtil.download(bos, response, "新导出表.xlsx");
        workbook.close();
    }

    /**
     * 自定义打印
     */
    private void printFreedom(String inputDate) throws IOException {
        //创建工作簿
        Workbook workbook = new XSSFWorkbook();
        //创建表
        Sheet sheet = workbook.createSheet("导出出货表");
        // 设置列宽
        sheet.setColumnWidth(0, 256 * 5);
        sheet.setColumnWidth(1, 256 * 15);
        sheet.setColumnWidth(2, 256 * 26);
        sheet.setColumnWidth(3, 256 * 15);
        sheet.setColumnWidth(4, 256 * 29);
        sheet.setColumnWidth(5, 256 * 15);
        sheet.setColumnWidth(6, 256 * 15);
        sheet.setColumnWidth(7, 256 * 15);
        sheet.setColumnWidth(8, 256 * 15);
        //合并单元格
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 1, 8));
        //创建第一行
        Row row = sheet.createRow(0);
        Cell cell = row.createCell(1);
        //已知格式2019-06/2019-11
        String year = inputDate.substring(0, inputDate.indexOf("-"));
        String month = "";
        if (inputDate.contains("-0")) {
            month = inputDate.substring(inputDate.lastIndexOf("0") + 1);
        } else {
            month = inputDate.substring(inputDate.indexOf("-"));
        }
        cell.setCellValue(year + "年" + month + "月份出货表");
        // 设置单元格样式
        cell.setCellStyle(this.bigTitle(workbook));
        //导出第二行
        String[] titles = {"客户", "订单号", "货号", "数量", "工厂", "工厂交期", "船期", "贸易条款"};
        row = sheet.createRow(1);
        row.setHeightInPoints(26);
        for (int i = 0; i < titles.length; i++) {
            cell = row.createCell(i + 1);
            cell.setCellValue(titles[i]);
            cell.setCellStyle(this.title(workbook));
        }
        //查询数据
        String companyId = getLoginCompanyId();
        List<ContractProductVo> list = contractProductService.findByShipTime(companyId, inputDate);
        //导出数据行
        for (int i = 0; i < list.size(); i++) {
            ContractProductVo cp = list.get(i);
            row = sheet.createRow(i + 2);
            cell = row.createCell(1);
            cell.setCellValue(cp.getCustomName());
            cell.setCellStyle(this.text(workbook));
            cell = row.createCell(2);
            cell.setCellValue(cp.getContractNo());
            cell.setCellStyle(this.text(workbook));
            cell = row.createCell(3);
            cell.setCellValue(cp.getProductNo());
            cell.setCellStyle(this.text(workbook));
            cell = row.createCell(4);
            if (cp.getCnumber() == null) {
                cell.setCellValue("");
            } else {
                cell.setCellValue(cp.getCnumber());
            }
            cell.setCellStyle(this.text(workbook));
            cell = row.createCell(5);
            cell.setCellValue(cp.getFactoryName());
            cell.setCellStyle(this.text(workbook));
            cell = row.createCell(6);
            cell.setCellValue(cp.getDeliveryPeriod());
            cell.setCellStyle(this.text(workbook));
            cell = row.createCell(7);
            cell.setCellValue(cp.getShipTime());
            cell.setCellStyle(this.text(workbook));
            cell = row.createCell(8);
            cell.setCellValue(cp.getTradeTerms());
            cell.setCellStyle(this.text(workbook));
        }
        //导出下载
        DownloadUtil downloadUtil = new DownloadUtil();
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        workbook.write(bos);
        downloadUtil.download(bos, response, "出货表.xlsx");
        workbook.close();
    }

    //大标题的样式
    public CellStyle bigTitle(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        Font font = wb.createFont();
        font.setFontName("宋体");
        font.setFontHeightInPoints((short) 16);
        font.setBold(true);//字体加粗
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);                //横向居中
        style.setVerticalAlignment(VerticalAlignment.CENTER);        //纵向居中
        return style;
    }

    //小标题的样式
    public CellStyle title(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        Font font = wb.createFont();
        font.setFontName("黑体");
        font.setFontHeightInPoints((short) 12);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);                //横向居中
        style.setVerticalAlignment(VerticalAlignment.CENTER);        //纵向居中
        style.setBorderTop(BorderStyle.THIN);                        //上细线
        style.setBorderBottom(BorderStyle.THIN);                    //下细线
        style.setBorderLeft(BorderStyle.THIN);                        //左细线
        style.setBorderRight(BorderStyle.THIN);                        //右细线
        return style;
    }

    //文字样式
    public CellStyle text(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        Font font = wb.createFont();
        font.setFontName("Times New Roman");
        font.setFontHeightInPoints((short) 10);

        style.setFont(font);

        style.setAlignment(HorizontalAlignment.LEFT);                //横向居左
        style.setVerticalAlignment(VerticalAlignment.CENTER);        //纵向居中
        style.setBorderTop(BorderStyle.THIN);                        //上细线
        style.setBorderBottom(BorderStyle.THIN);                    //下细线
        style.setBorderLeft(BorderStyle.THIN);                        //左细线
        style.setBorderRight(BorderStyle.THIN);                        //右细线

        return style;
    }
}


