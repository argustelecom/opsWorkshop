package ru.argustelecom.box.env.activity;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import ru.argustelecom.box.env.lifecycle.LifecycleHistoryFragment;
import ru.argustelecom.system.inf.testframework.it.ui.comp.Button;
import ru.argustelecom.system.inf.testframework.it.ui.comp.InputText;
import ru.argustelecom.system.inf.testframework.it.ui.comp.Link;
import ru.argustelecom.system.inf.testframework.it.ui.location.FindByFuzzyId;

import java.util.List;

public class ActivityFragment {

    @FindBy(css = "#activity_form li")
    private List<WebElement> tabs;

    @FindByFuzzyId("activity_form-add_comment")
    private Link openCreateCommentDialog;

    @FindByFuzzyId("comment_edit_form-comment_header")
    private InputText header;

    @FindByFuzzyId("comment_edit_form-comment_content")
    private InputText body;

    @FindByFuzzyId("comment_edit_form-submit_button")
    private Button create;

    @FindBy(css = "#activity_form-comment_scroller li")
    private List<WebElement> comments;

    @FindBy(xpath = "//body")
    public LifecycleHistoryFragment lifecycleHistoryBlock;

    public void openCreateCommentDialog() {
        openCreateCommentDialog.click();
    }

    public void setHeader(String value) {
        header.input(value);
    }

    public void setBody(String value) {
        body.input(value);
    }

    public void createComment() {
        create.click();
    }

    public String getCommentBody(String header) {

        WebElement comment = comments.stream()
                .filter(item -> item.findElement(By.cssSelector(".comment-header")).getText().equals(header))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("comments"))
                .findElement(By.cssSelector(".comment-content"));

        return comment.getText();
    }

    public void openComments() {
        tabs.get(0).click();
    }

    public void openAttachments() {
        tabs.get(1).click();
    }

    public void openHistory() {
        tabs.get(2).click();
    }
}
