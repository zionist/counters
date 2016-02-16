package com.tickeron.counters.contoller;

import com.tickeron.counters.DbHelper;
import com.tickeron.counters.dto.TestRun;
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
public class GreetingController {

    @Autowired
    DbHelper dbHelper;

    @RequestMapping("/test_runs")
    public String greeting(@RequestParam(value="name", required=false, defaultValue="World") String name, Model model) throws SQLException {
        model.addAttribute("name", name);

        dbHelper.connect();
        Statement statement = dbHelper.getStatement();

        ResultSet resultSet = statement.executeQuery("SELECT MIN(StartTime), RunId FROM LoadTest2010.dbo.LoadTestRun GROUP BY RunId ORDER BY MIN(StartTime);");
        Integer columnCount = resultSet.getMetaData().getColumnCount();
        final List<TestRun> testRuns = new ArrayList<>();
        while (resultSet.next()) {
            TestRun testRun = new TestRun();
            testRun.setRunId(resultSet.getString(1));
            testRun.setStartTime(resultSet.getString(2));
            testRuns.add(testRun);
            //for (Integer i=1; i <= columnCount; i++){
            //    System.out.println(resultSet.getString(i));
            //}
        }
        dbHelper.disconnect();



        model.addAttribute("testRuns", testRuns);

        return "greeting";
    }

}
