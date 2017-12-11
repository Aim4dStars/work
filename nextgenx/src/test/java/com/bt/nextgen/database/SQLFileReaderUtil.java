package com.bt.nextgen.database;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

/*
 * ATTENTION: SQL file must not contain column names, etc. including comment signs (#, --, /* etc.)
 *          like e.g. a.'#rows' etc. because every characters after # or -- in a line are filtered
 *          out of the query string the same is true for every characters surrounded by /* and */
/**/
public class SQLFileReaderUtil {
    private static ArrayList<String> listOfQueries = null;

    /*
     * @param   path    Path to the SQL file
     * @return          List of query strings
     */
    public ArrayList<String> createQueries(File file) {
        String queryLine;
        StringBuilder stringBuilder = new StringBuilder();
        listOfQueries = new ArrayList<>();

        try {
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);

            //read the SQL file line by line
            while ((queryLine = br.readLine()) != null) {
                if (queryLine.equals("/")) {
                    queryLine = "~BLOCK_END~";
                }
                if (queryLine.trim().startsWith("CREATE OR REPLACE")) {
                    do {
                        stringBuilder.append(queryLine.trim()).append(" ");
                    } while (((queryLine = br.readLine()) != null) && queryLine.startsWith("/"));
                }

                if (queryLine != null && queryLine.trim().toUpperCase().startsWith("REM") || queryLine.trim().toUpperCase().startsWith("SET")) {
                    queryLine = "";
                }
                // ignore comments beginning with --
                int indexOfCommentSign = queryLine.indexOf("--");
                if (indexOfCommentSign != -1) {
                    if (queryLine.startsWith("--")) {
                        queryLine = "";
                    } else
                        queryLine = queryLine.trim().substring(0, indexOfCommentSign - 1);
                }
                //  the + " " is necessary, because otherwise the content before and after a line break are concatenated
                // like e.g. a.xyz FROM becomes a.xyzFROM otherwise and can not be executed
                stringBuilder.append(queryLine).append(" ");
            }
            br.close();
            String[] blockQueries = stringBuilder.toString().split("~BLOCK_END~");

            //assuming only the first block contains the statements that need to be split with delimiter ;
            String[] splittedQueries = blockQueries[0].split(";");

            // filter out empty statements
            for (String query : splittedQueries) {
                if (!query.trim().equals("") && !query.trim().equals("\t")) {
                    listOfQueries.add(query);
                }
            }
            for (int i = 1; i < blockQueries.length; i++) {
                if (!blockQueries[i].trim().equals("") && !blockQueries[i].trim().equals("\t")) {
                    listOfQueries.add(blockQueries[i]);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return listOfQueries;
    }
}