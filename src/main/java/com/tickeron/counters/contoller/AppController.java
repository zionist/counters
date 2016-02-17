package com.tickeron.counters.contoller;

import com.tickeron.counters.DbHelper;
import com.tickeron.counters.dto.LoadTestRun;
import com.tickeron.counters.dto.TestRun;
import org.apache.commons.lang.StringEscapeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@Controller
public final class AppController {

    @Autowired
    private DbHelper dbHelper;

    @RequestMapping("/test_runs")
    public String testRuns(Model model)
            throws SQLException {
        dbHelper.connect();
        Statement statement = dbHelper.getStatement();

        ResultSet resultSet = statement.executeQuery(
            "SELECT MIN(StartTime), MAX(EndTime) AS EndTime, RunId FROM LoadTest2010.dbo.LoadTestRun GROUP BY RunId ORDER BY MIN(StartTime);"
        );
        Integer columnCount = resultSet.getMetaData().getColumnCount();
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

    @RequestMapping("/load_tests")
    public String testRun(@RequestParam(value = "testRunId", required = true) String testRunId, Model model)
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

}
