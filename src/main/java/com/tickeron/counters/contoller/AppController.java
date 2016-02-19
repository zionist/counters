package com.tickeron.counters.contoller;

import com.tickeron.counters.DbHelper;
import com.tickeron.counters.dto.Description;
import com.tickeron.counters.dto.LoadTestRun;
import com.tickeron.counters.dto.TestRun;
import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

@Controller
public final class AppController {

    @Autowired
    private DbHelper dbHelper;

    @RequestMapping(value = "/set_description", method = RequestMethod.POST)
    public ResponseEntity<Description> setDescription(@RequestBody Description description)
            throws SQLException {
        dbHelper.connect();
        Statement statement = dbHelper.getStatement();

        System.out.println(description.getDescription());

        //ResultSet resultSet = statement.executeQuery(
        //        "SELECT MIN(StartTime), MAX(EndTime) AS EndTime, RunId FROM LoadTest2010.dbo.LoadTestRun GROUP BY RunId ORDER BY MIN(StartTime) DESC;"
        //);
        dbHelper.disconnect();
        return new ResponseEntity<Description>(description, HttpStatus.OK);

    }

    @RequestMapping(value = "test_runs", method = RequestMethod.GET)
    public String testRuns(Model model)
            throws SQLException {
        dbHelper.connect();
        Statement statement = dbHelper.getStatement();

        ResultSet resultSet = statement.executeQuery(
            "SELECT MIN(StartTime), MAX(EndTime) AS EndTime, RunId FROM LoadTest2010.dbo.LoadTestRun GROUP BY RunId ORDER BY MIN(StartTime) DESC;"
        );
        final List<TestRun> testRuns = new ArrayList<>();
        while (resultSet.next()) {
            TestRun testRun = new TestRun();
            testRun.setStartTime(resultSet.getString(1));
            testRun.setEndTime(resultSet.getString(2));
            testRun.setRunId(resultSet.getString(3));
            testRuns.add(testRun);
            //for (Integer i=1; i <= columnCount; i++){
            //    System.out.println(resultSet.getString(i));
            //}
        }
        dbHelper.disconnect();

        model.addAttribute("testRuns", testRuns);

        return "test_runs";
    }

    @RequestMapping(value = "load_tests", method = RequestMethod.GET)
    public String loadTests(@RequestParam(value = "testRunId", required = true) String testRunId, Model model)
            throws SQLException {

        dbHelper.connect();
        Statement statement = dbHelper.getStatement();
        ResultSet resultSet = statement.executeQuery(
           String.format("SELECT LoadTestRunId, LoadTestName, StartTime, EndTime, RunDuration, WarmupTime, Outcome "
                        + "FROM LoadTest2010.dbo.LoadTestRun WHERE RunId = '%s' ORDER BY LoadTestRunId;",
                        StringEscapeUtils.escapeSql(testRunId))
        );
        final List<LoadTestRun> loadTestRuns = new ArrayList<>();
        while (resultSet.next()) {
            LoadTestRun loadTestRun = new LoadTestRun();
            loadTestRun.setLoadTestRunId(resultSet.getInt(1));
            loadTestRun.setLoadTestName(resultSet.getString(2));
            loadTestRun.setStartTime(resultSet.getString(3));
            loadTestRun.setEndTime(resultSet.getString(4));
            loadTestRun.setRunDuration(resultSet.getInt(5));
            loadTestRun.setWarmupTime(resultSet.getInt(6));
            loadTestRun.setOutcome(resultSet.getString(7));
            loadTestRuns.add(loadTestRun);
        }
        dbHelper.disconnect();
        model.addAttribute("testRunId", testRunId);
        model.addAttribute("loadTestRuns", loadTestRuns);
        return "load_tests";
    }



    @RequestMapping(value = "load_test_counters", method = RequestMethod.GET)
    public String loadTestCounters(
            @RequestParam(value = "loadTestRunId", required = true) Integer loadTestRunId,
            @RequestParam(value = "loadTestName", required = false) String loadTestName,
            @RequestParam(value = "testRunId", required = false) String testRunId,

            Model model) throws SQLException {

        final String serverSideQuery = String.format(
                "SELECT \n" +
                "( SELECT AVG(cd.CounterValue) \n" +
                "   FROM [iis_counters].[dbo].CounterData cd \n" +
                "   JOIN [iis_counters].[dbo].CounterDetails cdet ON cd.CounterID = cdet.CounterID \n" +
                "   WHERE cdet.ObjectName = 'Memory' AND cdet.CounterName = 'Committed Bytes'  \n" +
                "   AND cd.CounterDTM > r1.StartTime \n" +
                "   AND cd.CounterDTM < r1.EndTime \n" +
                ") as 'iis Memory Commited bytes',\n" +
                "\n" +
                "( SELECT AVG(cd.CounterValue) \n" +
                "   FROM [iis_counters].[dbo].CounterData cd \n" +
                "   JOIN [iis_counters].[dbo].CounterDetails cdet ON cd.CounterID = cdet.CounterID \n" +
                "   WHERE cdet.ObjectName = 'Memory' AND cdet.CounterName = 'Pages/sec'    \n" +
                "   AND cd.CounterDTM > r1.StartTime \n" +
                "   AND cd.CounterDTM < r1.EndTime \n" +
                ") as 'iis Memory Pages/sec',\n" +
                "\n" +
                "( SELECT AVG(cd.CounterValue) \n" +
                "   FROM [iis_counters].[dbo].CounterData cd \n" +
                "   JOIN [iis_counters].[dbo].CounterDetails cdet ON cd.CounterID = cdet.CounterID \n" +
                "   where cdet.ObjectName = 'Processor' and cdet.CounterName = '%% Processor Time' and cdet.InstanceName= '_Total'   \n" +
                "   AND cd.CounterDTM > r1.StartTime \n" +
                "   AND cd.CounterDTM < r1.EndTime \n" +
                ") as 'iis Processor %% Processor time',\n" +
                "\n" +
                "( SELECT AVG(cd.CounterValue) \n" +
                "   FROM [iis_counters].[dbo].CounterData cd \n" +
                "   JOIN [iis_counters].[dbo].CounterDetails cdet ON cd.CounterID = cdet.CounterID \n" +
                "   where cdet.ObjectName = 'PhysicalDisk' and cdet.CounterName = '%% Disk Read Time' and InstanceName = '_Total'  \n" +
                "   AND cd.CounterDTM > r1.StartTime \n" +
                "   AND cd.CounterDTM < r1.EndTime \n" +
                ") as 'iis Processor %%Disk Read Time ',\n" +
                "\n" +
                "( SELECT AVG(cd.CounterValue) \n" +
                "   FROM [iis_counters].[dbo].CounterData cd \n" +
                "   JOIN [iis_counters].[dbo].CounterDetails cdet ON cd.CounterID = cdet.CounterID \n" +
                "   where cdet.ObjectName = 'PhysicalDisk' and cdet.CounterName = '%% Disk Write Time' and InstanceName = '_Total'  \n" +
                "   AND cd.CounterDTM > r1.StartTime \n" +
                "   AND cd.CounterDTM < r1.EndTime \n" +
                ") as 'iis Processor %%Disk Write Time ',\n" +
                "\n" +
                "( SELECT AVG(cd.CounterValue)\n" +
                "   FROM [database_counters].[dbo].CounterData cd \n" +
                "   JOIN [database_counters].[dbo].CounterDetails cdet ON cd.CounterID = cdet.CounterID \n" +
                "   WHERE cdet.ObjectName = 'Memory' AND cdet.CounterName = 'Committed Bytes'  \n" +
                "   AND cd.CounterDTM > r1.StartTime \n" +
                "   AND cd.CounterDTM < r1.EndTime \n" +
                ") as 'database Memory Commited bytes',\n" +
                "\n" +
                "( SELECT AVG(cd.CounterValue)\n" +
                "   FROM [database_counters].[dbo].CounterData cd \n" +
                "   JOIN [database_counters].[dbo].CounterDetails cdet ON cd.CounterID = cdet.CounterID \n" +
                "   WHERE cdet.ObjectName = 'Memory' AND cdet.CounterName = 'Pages/sec'    \n" +
                "   AND cd.CounterDTM > r1.StartTime \n" +
                "   AND cd.CounterDTM < r1.EndTime \n" +
                ") as 'database Memory Pages/sec',\n" +
                "\n" +
                "( SELECT AVG(cd.CounterValue) \n" +
                "   FROM [database_counters].[dbo].CounterData cd \n" +
                "   JOIN [database_counters].[dbo].CounterDetails cdet ON cd.CounterID = cdet.CounterID \n" +
                "   where cdet.ObjectName = 'Processor' and cdet.CounterName = '%% Processor Time' and cdet.InstanceName= '_Total'   \n" +
                "   AND cd.CounterDTM > r1.StartTime \n" +
                "   AND cd.CounterDTM < r1.EndTime \n" +
                ") as 'database Processor %% Processor time',\n" +
                "\n" +
                "( SELECT AVG(cd.CounterValue) \n" +
                "   FROM [database_counters].[dbo].CounterData cd \n" +
                "   JOIN [database_counters].[dbo].CounterDetails cdet ON cd.CounterID = cdet.CounterID \n" +
                "   where cdet.ObjectName = 'PhysicalDisk' and cdet.CounterName = '%% Disk Read Time' and InstanceName = '_Total'  \n" +
                "   AND cd.CounterDTM > r1.StartTime \n" +
                "   AND cd.CounterDTM < r1.EndTime \n" +
                ") as 'database Processor %%Disk Read Time ',\n" +
                "\n" +
                "( SELECT AVG(cd.CounterValue) \n" +
                "   FROM [database_counters].[dbo].CounterData cd \n" +
                "   JOIN [database_counters].[dbo].CounterDetails cdet ON cd.CounterID = cdet.CounterID \n" +
                "   where cdet.ObjectName = 'PhysicalDisk' and cdet.CounterName = '%% Disk Write Time' and InstanceName = '_Total'  \n" +
                "   AND cd.CounterDTM > r1.StartTime \n" +
                "   AND cd.CounterDTM < r1.EndTime \n" +
                ") as 'database Processor %%Disk Write Time '" +
                "FROM LoadTest2010.dbo.LoadTestRun r1  WHERE r1.LoadTestRunId = %d", loadTestRunId);


        final String clientSideQuery =
                String.format(
                        "SELECT r1.StartTime, r1.EndTime,\n" +
                                "(SELECT c1.CumulativeValue FROM LoadTest2010.dbo.[LoadTestPerformanceCounterInstance] c1 WHERE c1.LoadTestRunId = r1.LoadTestRunId AND CounterId = 77 AND c1.InstanceName = '_Total') AS PagesSec,\n" +
                                "(SELECT c1.CumulativeValue FROM LoadTest2010.dbo.[LoadTestPerformanceCounterInstance] c1 WHERE c1.LoadTestRunId = r1.LoadTestRunId AND CounterId = 49 AND c1.InstanceName = '_Total') AS UserLoad,\n" +
                                "(SELECT c1.CumulativeValue FROM LoadTest2010.dbo.[LoadTestPerformanceCounterInstance] c1 WHERE c1.LoadTestRunId = r1.LoadTestRunId AND CounterId = 71 AND c1.InstanceName = '_Total') AS TotalErrors\n" +
                                "FROM  LoadTest2010.dbo.LoadTestRun r1 WHERE LoadTestRunId = %d\n",
                        loadTestRunId);

        dbHelper.connect();
        // get server side counters
        Map<String, String> serverSideCounters = new LinkedHashMap<>();
        Statement statement = dbHelper.getStatement();
        ResultSet resultSet = statement.executeQuery(serverSideQuery);
        Integer columnCount = resultSet.getMetaData().getColumnCount();
        while (resultSet.next()) {
            for (Integer i = 1; i <= columnCount; i++){
                serverSideCounters.put(
                        resultSet.getMetaData().getColumnName(i),
                        resultSet.getString(i)
                        );
            }
        }

        // get client side counters
        Map<String, String> clientSideCounters = new LinkedHashMap<>();
        resultSet = statement.executeQuery(clientSideQuery);
        columnCount = resultSet.getMetaData().getColumnCount();
        while (resultSet.next()) {
            for (Integer i = 1; i <= columnCount; i++){
                clientSideCounters.put(
                        resultSet.getMetaData().getColumnName(i),
                        resultSet.getString(i)
                );
            }
        }

        dbHelper.disconnect();

        model.addAttribute("serverSideCounters", serverSideCounters);
        model.addAttribute("clientSideCounters", clientSideCounters);
        model.addAttribute("loadTestRunId", loadTestRunId);
        model.addAttribute("loadTestName", loadTestName);
        model.addAttribute("testRunId", testRunId);
        return "load_test_counters";

    }

}
