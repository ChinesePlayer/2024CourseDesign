package org.fatmansoft.teach.util;

import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;

import java.util.List;

//PDF相关的工具类
public class PdfUtil {
    //向pdf的某个表格中填充数据
    //该方法假定表格中已有的数据也是用该方法来填充的
    public static void addCells(PdfPTable table, List<PdfPCell> cellList){
        int colNum = table.getNumberOfColumns();
        int count = 0;
        for(PdfPCell cell : cellList){
            table.addCell(cell);
            count++;
        }
        int remain = count % colNum;
        //添加空的单元格来凑齐列数的整数倍
        for(int i = 0; i < remain; i++){
            PdfPCell cell = new PdfPCell();
            table.addCell(cell);
        }
    }
}
