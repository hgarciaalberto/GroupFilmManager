package com.ahgitdevelopment.groupfilmmanager.ui.import

import android.content.Context
import android.content.res.AssetManager
import android.util.Log
import org.apache.poi.hssf.usermodel.HSSFSheet
import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.poifs.filesystem.POIFSFileSystem
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.Row
import java.io.InputStream


class ImportMovies(private val context: Context) {

    private val TAG = "ImportMovies"
    private lateinit var mySheet: HSSFSheet

    fun import() {

        try {

            // initialize asset manager
            val assetManager: AssetManager = context.assets

            //  open excel file name as movies.xls
            val myInput: InputStream = assetManager.open("movies.xls")

            // Create a POI File System object
            // Create a POI File System object
            val myFileSystem = POIFSFileSystem(myInput)

            // Create a workbook using the File System
            val myWorkBook = HSSFWorkbook(myFileSystem)

            // Get the first sheet from workbook
            mySheet = myWorkBook.getSheetAt(0)


            // We now need something to iterate through the cells.
            val rowIter: Iterator<Row> = mySheet.rowIterator()
            var rownum = 0

//            textView.append("\n")
            while (rowIter.hasNext()) {
                Log.e(TAG, "row no $rownum")
                val myRow: Row = rowIter.next()
                if (rownum != 0) {
                    var cellIter: Iterator<Cell> = myRow.cellIterator()
                    var colno = 0

                    var id = ""
                    var movieName = ""
                    var info1 = ""
                    var info2 = ""

                    while (cellIter.hasNext()) {
                        val myCell: Cell = cellIter.next()

                        if (colno == 0) {
                            id = myCell.toString()
                        } else if (colno == 1) {
                            movieName = myCell.toString()
                        } else if (colno == 2) {
                            info1 = myCell.toString()
                        } else if (colno == 3) {
                            info2 = myCell.toString()
                        }
                        colno++
                        Log.e(TAG, " Index :" + myCell.columnIndex + " -- " + myCell.toString())
                    }

                    Log.i(TAG, " Id: $id - Name: $movieName - Info1: $info1 - Info2: $info2")
//                    textView.append(sno + " -- " + date + "  -- " + det + "\n")

                }
                rownum++
            }

        } catch (e: Exception) {
            Log.e(TAG, "Importing error", e)
        }
    }
}
