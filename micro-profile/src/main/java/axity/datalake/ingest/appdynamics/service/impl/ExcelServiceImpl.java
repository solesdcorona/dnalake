package axity.datalake.ingest.appdynamics.service.impl;

import axity.datalake.ingest.appdynamics.service.ExcelService;
import axity.datalake.ingest.appdynamics.service.to.ColumnValueTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;

import java.io.*;
import java.util.List;

public class ExcelServiceImpl implements ExcelService {

    private static final Logger logger = LogManager.getLogger(ExcelServiceImpl.class);


    @Override
    public void writeMetrics(List<ColumnValueTO> columns) {
        try {
            //InputStream inp = new FileInputStream("workbook.xlsx");
            ClassLoader classLoader = getClass().getClassLoader();
            //InputStream inputStream = classLoader.getResourceAsStream("workbook.xlsx");
            InputStream inputStream = new FileInputStream( new File("C:\\ROGER\\architecture\\appDynamics\\workbook.xlsx"));
            Workbook wb = WorkbookFactory.create(inputStream);
            Sheet sheet = wb.getSheetAt(0);
            int lastRow = sheet.getLastRowNum();
            logger.info("excel row {}" + lastRow);
            Row row = sheet.getRow(lastRow+1);
            if (row == null) {
                row = sheet.createRow(lastRow+1);
            }
            Cell cellFirst = row.getCell(0);
            if (cellFirst == null) {
                cellFirst = row.createCell(0);
            }
            cellFirst.setCellType(CellType.STRING);
            cellFirst.setCellValue("Michoacan");

            for (ColumnValueTO columnValueTO : columns) {
                String input=columnValueTO.getIndexColumn().toLowerCase();
                logger.info(" letter {}", input);
                int position=0;
                int alphabet=26;
                int positionBefore=0;
                int currentposition=0;
                for (int i = 0; i < input.length(); ++i) {
                     position = position +input.charAt(i) - 'a' + 1;

                    if(i==1){
                        positionBefore=positionBefore+alphabet;
                        positionBefore=positionBefore+position;
                        currentposition=positionBefore;
                    }else{
                        currentposition=position+1;
                    }
                    positionBefore=position;
                }
                logger.info(" position {} value {}", currentposition,columnValueTO.getValueColumn());

                Cell cell = row.getCell(currentposition-2);
                if (cell == null) {
                    cell = row.createCell(currentposition-2);
                }
                cell.setCellType(CellType.STRING);
                cell.setCellValue(columnValueTO.getValueColumn());
            }


            String input = "vvac".toLowerCase(); //note the to lower case in order to treat a and A the same way
            /*for (int i = 0; i < input.length(); ++i) {
                logger.info(" position {}", input.charAt(i));
                logger.info(" position {} {}", input.charAt(i), ('a' + 1));
                int position = input.charAt(i) - 'a' + 1;
                logger.info(" position {}", position);
            }
            */
            // Write the output to a file
            OutputStream fileOut = new FileOutputStream("C:\\ROGER\\architecture\\appDynamics\\workbook.xlsx");
            wb.write(fileOut);
            wb.close();
            inputStream.close();
            fileOut.close();
        } catch (InvalidFormatException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
