package ru.argustelecom.box.env;

import ru.argustelecom.system.inf.testframework.it.ui.comp.*;
import ru.argustelecom.system.inf.utils.converters.EntityConverter;

import java.util.UUID;

import static com.google.common.collect.Lists.newArrayList;

public class UITestUtils {

    private UITestUtils() {
    }

    public static String uniqueId(String original) {
        return original + UUID.randomUUID().toString().substring(0, 10);
    }

    public static String uniqueId() {
        return uniqueId("");
    }

    public static Row lastRow(Table table) {
        int lastIndex = table.getRowCount() - 1;
        return table.getRow(lastIndex);
    }

    public static Cell lastRowCell(Table table, int cellIndex) {
        return lastRow(table).getCell(cellIndex);
    }

    public static String convertToString(Object o) {
        return new EntityConverter().convertToString(o);
    }

    public static TreeNode getTreeNode(Tree tree, String... path) {
        return tree.getTreeNode(newArrayList(path));
    }
}
