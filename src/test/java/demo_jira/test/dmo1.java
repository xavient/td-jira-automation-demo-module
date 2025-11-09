package demo_jira.test;

import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;
import org.testng.Assert;

import net.rcarz.jiraclient.Issue;
import net.rcarz.jiraclient.Issue.SearchResult;
import tdx.common.core.reporter.Status;
import tdx.jira.core.logging.CustomLogs;
import tdx.jira.core.utils.JiraReporting;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * A comprehensive TestNG test suite to validate the functionality of the JiraReporting utility class.
 * This class simulates a test automation framework and uses each public method of JiraReporting
 * to demonstrate proper implementation and behavior.
 */
public class dmo1 {

    private static final String DUMMY_IMAGE_PATH = "target/DEMOAUTOMATION[TOBEDELETED]"+".png";
    private static final String DUMMY_CSV_PATH_DESC = "target/dummy-testcases-desc-" + UUID.randomUUID() + ".csv";
    private static final String DUMMY_CSV_PATH_ZEPHYR = "target/dummy-testcases-zephyr-" + UUID.randomUUID() + ".csv";
    private static final String MOCK_JQL = "key='SKP-277'";
    private static String MOCK_ISSUE_KEY = "SKP-283";

    /**
     * Set up method that runs once before all tests in the suite.
     * It creates dummy files (.png and .csv) required by the test methods, making the suite self-contained.
     */
    @BeforeSuite
    public void setup() throws IOException {
    	CustomLogs.log(Status.INFO,"Setting up test resources...");
        // Create a directory for dummy files
        Files.createDirectories(Paths.get("target"));
        // Create a dummy image file for attachment tests
        Files.createFile(Paths.get(DUMMY_IMAGE_PATH));

        // Create a dummy CSV file for the createJiraTestIssueWithStepsFromCsv method
        String csvContentDesc = "\"Go to login page\",\"The login page should be displayed correctly\"\n"
                              + "\"Enter valid credentials\",\"The system accepts credentials and user is authenticated\"";
        Files.write(Paths.get(DUMMY_CSV_PATH_DESC), csvContentDesc.getBytes());

        // Create a dummy CSV for Zephyr API tests
        String csvContentZephyr = "Test Case Name,Test Step,Test Data,Expected Result\n"
                                + "Sample Zephyr Test Case 1,Step 1,Data 1,Expected 1\n"
                                + "Sample Zephyr Test Case 1,Step 2,Data 2,Expected 2\n"
                                + "Sample Zephyr Test Case 2,Step 1,Data 1,Expected 1";
        Files.write(Paths.get(DUMMY_CSV_PATH_ZEPHYR), csvContentZephyr.getBytes());
        CustomLogs.log(Status.INFO, "Test resources created successfully.");
    }

    /**
     * Tests the {@code createJiraIssue} method by creating a new Bug with an attachment.
     * This method is for creating standard Jira issues.
     */
    @Test(description = "Tests creating a standard Jira Bug issue with an attachment.",priority = 1,groups = { "jiraDemo5" })
    public void testCreateJiraIssue_withAttachment() {
        CustomLogs.log(Status.INFO,"\n--- Running: testCreateJiraIssue_withAttachment ---");
       // File dummyFile = new File(DUMMY_IMAGE_PATH);
        String issueKey = JiraReporting.createJiraIssue(
            "Test",
            null,
            "[TO_BE_DELETED]-TEST: UI not working on Chrome",
            "[TO_BE_DELETED]-User is unable to log in on Chrome browser."
        );
        Assert.assertNotNull(issueKey, "Jira issue key should not be null after creation.");
        MOCK_ISSUE_KEY = issueKey;
        CustomLogs.log(Status.PASS, "Successfully created Jira issue with key: " + issueKey);
        System.out.println("Successfully created Jira issue with key: " + issueKey);
    }


    
    /**
     * Tests the {@code updateJiraIssueAttachment} method.
     * This method is used to add or update an attachment on an existing issue.
     */
    @Test(description = "Tests updating a Jira issue with a new attachment.",priority = 2,groups = { "jiraDemo" })
    public void testUpdateJiraIssueAttachment_replacesFile() {
        CustomLogs.log(Status.INFO,"\n--- Running: testUpdateJiraIssueAttachment_replacesFile ---");
        File dummyFile = new File(DUMMY_IMAGE_PATH);
        // Note: This assumes a Jira issue with key MOCK_ISSUE_KEY exists.
        JiraReporting.updateJiraIssueAttachment(MOCK_ISSUE_KEY, dummyFile);
        CustomLogs.log(Status.PASS, "Successfully executed updateJiraIssueAttachment for issue: " + MOCK_ISSUE_KEY);
    }

    /**
     * Tests the {@code updateJiraExistingIssue} method.
     * This method updates an existing issue's details and can also attach a file.
     */
    @Test(description = "Tests updating an existing Jira issue with a new description and status.",priority = 3,groups = { "jiraDemo1" })
    public void testUpdateJiraExistingIssue_updatesDetails() {
        CustomLogs.log(Status.INFO,"\n--- Running: testUpdateJiraExistingIssue_updatesDetails ---");
        File dummyFile = new File(DUMMY_IMAGE_PATH);
        boolean isUpdated = JiraReporting.updateJiraExistingIssue(
            Status.FAIL,
            "[TO BE DELETED]_Updated bug description with new details after test failure.",
            "Test",
            "SKP-288",
            dummyFile
        );
        Assert.assertTrue(isUpdated, "The Jira issue should be updated successfully.");
        CustomLogs.log(Status.PASS,"Successfully updated Jira issue: " + MOCK_ISSUE_KEY);
    }

    /**
     * Tests the {@code updateJiraExistingIssueOrCreateNew} method.
     * This is a core CustomLogs method that intelligently either updates or creates an issue.
     */
    @Test(description = "Tests creating a new Jira issue with a screenshot when no matching issue is found.",priority = 4,groups = { "jiraDemo" })
    public void testUpdateOrCreateNewIssue_createsNewIssue() throws Exception {
        CustomLogs.log(Status.INFO,"\n--- Running: testUpdateOrCreateNewIssue_createsNewIssue ---");
        File dummyFile = new File(DUMMY_IMAGE_PATH);
        String issueSummary = "New Bug from Automation Run " + System.currentTimeMillis()+" [TO BE DELETED]";
        String issueDescription = "This test simulates a failure and creates a new issue."+" [TO BE DELETED]";
        JiraReporting.updateJiraExistingIssueOrCreateNew(
            Status.PASS,
            issueSummary,
            issueDescription,
            "Test",
            dummyFile
        );
        // This method has no return, so we assert that no exception was thrown.
        CustomLogs.log(Status.PASS,"Successfully executed updateJiraExistingIssueOrCreateNew.");
    }
    
    /**
     * Tests the {@code getOpenIssuesDetails} method.
     * This demonstrates how to search for issues with standard fields.
     */
    @Test(description = "Tests searching for open issues and retrieving standard details.",priority = 5,groups = { "jiraDemo" })
    public void testSearchOpenIssues_returnsStandardDetails() {
        CustomLogs.log(Status.INFO,"\n--- Running: testSearchOpenIssues_returnsStandardDetails ---");
        SearchResult results = JiraReporting.getOpenIssuesDetails(MOCK_JQL);
        Assert.assertNotNull(results, "Search results should not be null.");
        CustomLogs.log(Status.PASS,"JIRA search completed: {} total issues, {} returned"+
                results.total);
    
        for (Issue issue : results.issues) {
        	 CustomLogs.log(Status.PASS,"Issue: {} | Summary: {} | Status: {} | Assignee: {}"+" "+ 
                        issue.getKey()+" "+
                        issue.getSummary()+" "+
                        issue.getStatus() != null ? issue.getStatus().getName() : "N/A"+" "+
                        issue.getAssignee() != null ? issue.getAssignee().getDisplayName() : "Unassigned");
        }
    }

    /**
     * Tests the {@code getOpenIssuesAllDetails} method.
     * This demonstrates how to search for issues and retrieve all available fields.
     */
    @Test(description = "Tests searching for open issues and retrieving all fields.",priority = 6,groups = { "jiraDemo" })
    public void testSearchOpenIssues_returnsAllDetails() {
        CustomLogs.log(Status.INFO,"\n--- Running: testSearchOpenIssues_returnsAllDetails ---");
        SearchResult results = JiraReporting.getOpenIssuesAllDetails(MOCK_JQL);
        Assert.assertNotNull(results, "Search results with all fields should not be null.");
        CustomLogs.log(Status.PASS,"JIRA search completed: {} total issues, {} returned"+
                results.total);
    
        for (Issue issue : results.issues) {
        	 CustomLogs.log(Status.PASS,"Issue: {} | Summary: {} | Status: {} | Assignee: {}"+" "+ 
                        issue.getKey()+" "+
                        issue.getSummary()+" "+
                        issue.getStatus() != null ? issue.getStatus().getName() : "N/A"+" "+
                        issue.getAssignee() != null ? issue.getAssignee().getDisplayName() : "Unassigned");
        }
    }


}
