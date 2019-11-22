package rukovoditel.cz;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.Select;

import java.util.UUID;
import java.util.List;

/**
 * Unit test for simple App.
 */
public class AppTest 
{
    private ChromeDriver driver;
    private final static String url_site = "https://digitalnizena.cz/rukovoditel/";
    private final static String login_site = "rukovoditel";
    private final static String password_site = "vse456ru";
    private final static String invalid_password_site = "qwerty";

    @Before
    public void init() {
        System.setProperty("webdriver.chrome.driver", "src/test/resources/drivers/chromedriver.exe");
        driver = new ChromeDriver();
        driver.manage().window().maximize();
    }

    @After
    public void tearDown() {
//        driver.close();
    }

   @Test
    public void Login_valid_test(){
       // given
       driver.get(url_site);
       // when
       WebElement loginInput = driver.findElement(By.name("username"));
       loginInput.sendKeys(login_site);
       WebElement passwordInput = driver.findElement(By.name("password"));
       passwordInput.sendKeys(password_site);
       WebElement loginButton = driver.findElement(By.className("pull-right"));
       loginButton.click();
       // then
       WebElement title_page = driver.findElement(By.className("page-title"));
       String text = title_page.getText();
       Assert.assertEquals("Welcome to the Rukovoditel – your new assistant in business management!", text);
   }

    @Test
    public void Login_invalid_password(){
        // given
        driver.get(url_site);
        // when
        WebElement loginInput = driver.findElement(By.name("username"));
        loginInput.sendKeys(login_site);
        WebElement passwordInput = driver.findElement(By.name("password"));
        passwordInput.sendKeys(invalid_password_site);
        WebElement loginButton = driver.findElement(By.className("pull-right"));
        loginButton.click();
        // then
        WebElement alert_error = driver.findElement(By.className("alert-danger"));
        Assert.assertEquals(true, alert_error.isDisplayed());
    }

    @Test
    public void Logged_user_exit(){
        // given
        driver.get(url_site);
        WebElement loginInput = driver.findElement(By.name("username"));
        loginInput.sendKeys(login_site);
        WebElement passwordInput = driver.findElement(By.name("password"));
        passwordInput.sendKeys(password_site);
        WebElement loginButton = driver.findElement(By.className("pull-right"));
        loginButton.click();
        // when
        driver.get("https://digitalnizena.cz/rukovoditel/index.php?module=users/login&action=logoff");
        WebElement title_page2 = driver.findElement(By.className("login-page-logo"));
        String text2 = title_page2.getText();
        // then
        Assert.assertEquals("My Rukovoditel", text2);
    }

    @Test
    public void Project_without_name(){
        // given
        driver.get(url_site);
        WebElement loginInput = driver.findElement(By.name("username"));
        loginInput.sendKeys(login_site);
        WebElement passwordInput = driver.findElement(By.name("password"));
        passwordInput.sendKeys(password_site);
        WebElement loginButton = driver.findElement(By.className("pull-right"));
        loginButton.click();
        // when
        driver.get("https://digitalnizena.cz/rukovoditel/index.php?module=items/items&path=21");
        WebElement addProject = driver.findElement(By.className("btn-primary"));
        addProject.click();
        WebDriverWait wait = new WebDriverWait(driver, 5);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("btn-primary-modal-action")));
        WebElement save = driver.findElement(By.className("btn-primary-modal-action"));
        save.click();
        WebElement label_error = driver.findElement(By.id("fields_158-error"));
        // then
        Assert.assertEquals(true, label_error.isDisplayed());
    }

    @Test
    public void New_project_test() {
        // given
        driver.get(url_site);
        WebElement loginInput = driver.findElement(By.name("username"));
        loginInput.sendKeys(login_site);
        WebElement passwordInput = driver.findElement(By.name("password"));
        passwordInput.sendKeys(password_site);
        WebElement loginButton = driver.findElement(By.className("pull-right"));
        loginButton.click();
        // when
        driver.get("https://digitalnizena.cz/rukovoditel/index.php?module=items/items&path=21");
        WebElement addProject = driver.findElement(By.className("btn-primary"));
        addProject.click();

        WebDriverWait wait = new WebDriverWait(driver, 5);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.name("fields[157]")));

        Select select_status = new Select(driver.findElement(By.name("fields[157]")));
        select_status.selectByVisibleText("New");

        Select select_priority = new Select(driver.findElement(By.name("fields[156]")));
        select_priority.selectByVisibleText("High");

        WebElement date_set = driver.findElement(By.className("date-set"));
        date_set.click();
        WebElement active_day = driver.findElement(By.cssSelector(".active.day"));
        active_day.click();

        WebElement press_name = driver.findElement(By.name("fields[158]"));
        String uuid = UUID.randomUUID().toString();
        press_name.sendKeys("Liskov" + uuid);

        WebElement save = driver.findElement(By.className("btn-primary-modal-action"));
        save.click();
        // then
        driver.get("https://digitalnizena.cz/rukovoditel/index.php?module=items/items&path=21");
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("item_heading_link")));
        List<WebElement> elements = driver.findElements(By.className("item_heading_link"));
        int tmp = 0;
        int cnt_row = 0;
        for (WebElement el : elements) {
//            System.out.println(el.getText());
            cnt_row = cnt_row + 1;
            if (el.getText().contains(uuid)) {
                tmp = 1;
//                System.out.println("Yes");
                break;
            }
        }
        if (tmp == 1){
            WebElement delete_project = driver.findElement(By.cssSelector("tbody > tr:nth-child(" + Integer.toString(cnt_row) + ") > td.field-152-td > a:nth-child(1)"));
            delete_project.click();

            wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("btn-primary-modal-action")));
            WebElement confirm_delete = driver.findElement(By.className("btn-primary-modal-action"));
            confirm_delete.click();

            Assert.assertEquals(1, tmp);
        }
        else{
            Assert.fail();
        }
    }

    @Test
    public void Create_one_task(){

        final String descr_text = "console.log('hello_world')";
        final String const_status = "New";
        final String const_priority = "Medium";
        final String const_type = "Task";

        // given
        driver.get(url_site);
        WebElement loginInput = driver.findElement(By.name("username"));
        loginInput.sendKeys(login_site);
        WebElement passwordInput = driver.findElement(By.name("password"));
        passwordInput.sendKeys(password_site);
        WebElement loginButton = driver.findElement(By.className("pull-right"));
        loginButton.click();
        driver.get("https://digitalnizena.cz/rukovoditel/index.php?module=items/items&path=21");
        // when
        WebDriverWait wait = new WebDriverWait(driver, 5);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("item_heading_link")));
        List<WebElement> elements = driver.findElements(By.className("item_heading_link"));
        for (WebElement el : elements) {
            if (el.getText().equals("liskovProject")) {
                el.click();
                break;
            }
        }

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("btn-primary")));
        WebElement add_task = driver.findElement(By.className("btn-primary"));
        add_task.click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("btn-primary-modal-action")));
        Select select_type_task = new Select(driver.findElement(By.name("fields[167]")));
        select_type_task.selectByVisibleText(const_type);

        WebElement task_name = driver.findElement(By.name("fields[168]"));
        String uuid_task = UUID.randomUUID().toString();
        task_name.sendKeys("Task" + uuid_task);

        Select select_status_task = new Select(driver.findElement(By.name("fields[169]")));
        select_status_task.selectByVisibleText(const_status);

        Select select_priority_task = new Select(driver.findElement(By.name("fields[170]")));
        select_priority_task.selectByVisibleText(const_priority);

        WebElement desc_insert = driver.findElement(By.className("cke_button__codesnippet_icon"));
        desc_insert.click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("cke_80_textarea")));
        WebElement insert_text = driver.findElement(By.id("cke_80_textarea"));

        Select choose_lang = new Select(driver.findElement(By.id("cke_77_select")));
        choose_lang.selectByVisibleText("JavaScript");
        insert_text.sendKeys(descr_text);

        WebElement save_code = driver.findElement(By.id("cke_86_label"));
        save_code.click();

        WebElement save_task = driver.findElement(By.className("btn-primary-modal-action"));
        save_task.click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("tbody > tr > td.field-163-td > a:nth-child(3)")));
        WebElement open_new_page = driver.findElement(By.cssSelector("tbody > tr > td.field-163-td > a:nth-child(3)"));
        open_new_page.click();

        // then
        WebElement verify_descr = driver.findElement(By.className("language-javascript"));
        Assert.assertEquals(descr_text, verify_descr.getText());

        WebElement verify_type = driver.findElement(By.cssSelector("tr.form-group-167 > td > div"));
        Assert.assertEquals(const_type, verify_type.getText());

        WebElement verify_status = driver.findElement(By.cssSelector("tr.form-group-169 > td > div"));
        Assert.assertEquals(const_status, verify_status.getText());

        WebElement verify_priority = driver.findElement(By.cssSelector("tr.form-group-170 > td > div"));
        Assert.assertEquals(const_priority, verify_priority.getText());

        WebElement verify_name = driver.findElement(By.className("caption"));
        Assert.assertEquals("Task" + uuid_task, verify_name.getText());

        driver.get("https://digitalnizena.cz/rukovoditel/index.php?module=items/items&path=21-383/22");

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("tbody > tr > td.field-163-td > a:nth-child(1)")));
        WebElement delete_task = driver.findElement(By.cssSelector("tbody > tr > td.field-163-td > a:nth-child(1)"));
        delete_task.click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("btn-primary-modal-action")));
        WebElement confirm_delete_task = driver.findElement(By.className("btn-primary-modal-action"));
        confirm_delete_task.click();
    }

    @Test
    public void Create_seven_tasks(){
        // given
        driver.get(url_site);
        WebElement loginInput = driver.findElement(By.name("username"));
        loginInput.sendKeys(login_site);
        WebElement passwordInput = driver.findElement(By.name("password"));
        passwordInput.sendKeys(password_site);
        WebElement loginButton = driver.findElement(By.className("pull-right"));
        loginButton.click();

        driver.get("https://digitalnizena.cz/rukovoditel/index.php?module=items/items&path=21");

        WebDriverWait wait = new WebDriverWait(driver, 5);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("item_heading_link")));
        List<WebElement> elements = driver.findElements(By.className("item_heading_link"));
        for (WebElement el : elements) {
            if (el.getText().equals("liskovProject")) {
                el.click();
                break;
            }
        }
        // when
        for (int i = 0; i < 7; i++) {
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("btn-primary")));
            WebElement add_task = driver.findElement(By.className("btn-primary"));
            add_task.click();

            wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("btn-primary-modal-action")));
            Select select_type_task = new Select(driver.findElement(By.name("fields[167]")));
            select_type_task.selectByVisibleText("Task");

            WebElement task_name = driver.findElement(By.name("fields[168]"));
            String uuid_task = UUID.randomUUID().toString();
            task_name.sendKeys("Task" + uuid_task);

            Select select_status_task = new Select(driver.findElement(By.name("fields[169]")));

            switch(i) {
                case 0: // New
                    select_status_task.selectByVisibleText("New");
                    break;
                case 1: // Open
                    select_status_task.selectByVisibleText("Open");
                    break;
                case 2: // Waiting
                    select_status_task.selectByVisibleText("Waiting");
                    break;
                case 3: //Done
                    select_status_task.selectByVisibleText("Done");
                    break;
                case 4: //Closed
                    select_status_task.selectByVisibleText("Closed");
                    break;
                case 5: //Paid
                    select_status_task.selectByVisibleText("Paid");
                    break;
                case 6: //Canceled
                    select_status_task.selectByVisibleText("Canceled");
                    break;
            }
            WebElement save_task = driver.findElement(By.className("btn-primary-modal-action"));
            save_task.click();
        }

        driver.get("https://digitalnizena.cz/rukovoditel/index.php?module=reports/users_filters&action=use&id=default&redirect_to=listing&reports_id=453&path=21-383/22");

        WebElement check_status = driver.findElement(By.className("filters-preview-condition-include"));
        Assert.assertEquals("New, Open, Waiting", check_status.getText());

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("field-169-td")));
        List<WebElement> list_status = driver.findElements(By.cssSelector(".field-169-td > div"));
        for (WebElement el : list_status) {
            if(el.getText().equals("New") || el.getText().equals("Open") || el.getText().equals("Waiting")){
                //
            }
            else {
                Assert.fail();
            }
        }

        check_status.click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("ul.chosen-choices > li.search-choice:nth-child(2) > a.search-choice-close")));
        WebElement delete_filter = driver.findElement(By.cssSelector("ul.chosen-choices > li.search-choice:nth-child(2) > a.search-choice-close"));
        delete_filter.click();

        WebElement save_new_filter = driver.findElement(By.className("btn-primary-modal-action"));
        save_new_filter.click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("field-169-td")));
        List<WebElement> new_list_status = driver.findElements(By.cssSelector(".field-169-td > div"));
        for (WebElement el : new_list_status) {
            if(el.getText().equals("New") || el.getText().equals("Waiting")){
                //
            }
            else {
                Assert.fail();
            }
        }

        driver.get("https://digitalnizena.cz/rukovoditel/index.php?module=reports/filters&action=delete&id=all&redirect_to=listing&path=21-383/22&reports_id=453");

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("field-169-td")));
        List<WebElement> new_list_status_all = driver.findElements(By.cssSelector(".field-169-td > div"));

        if(new_list_status_all.size() != 7){
            System.out.println("Not 7 elements in the status");
            Assert.fail();
        }
        else {
            for (WebElement el : new_list_status_all) {
                if (el.getText().equals("New") || el.getText().equals("Open") || el.getText().equals("Waiting") ||
                        el.getText().equals("Done") || el.getText().equals("Closed") || el.getText().equals("Paid") || el.getText().equals("Canceled")) {
                    //
                } else {
                    Assert.fail();
                }
            }
        }
        // then
        WebElement select_all = driver.findElement(By.id("select_all_items"));
        select_all.click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("table.table-bordered > tbody > tr:nth-child(7) > td:nth-child(1) > div > span.checked")));

        driver.get("https://digitalnizena.cz/rukovoditel/index.php?module=items/delete_selected&path=21-383/22&reports_id=453");

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("btn-primary-modal-action")));
        WebElement confirm_delete_all_tasks = driver.findElement(By.className("btn-primary-modal-action"));
        confirm_delete_all_tasks.click();

        wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("table.table-bordered > tbody > tr > td")));
        WebElement check_deleted_elem = driver.findElement(By.cssSelector("table.table-bordered > tbody > tr > td"));
        Assert.assertEquals("No Records Found", check_deleted_elem.getText());
    }
}
