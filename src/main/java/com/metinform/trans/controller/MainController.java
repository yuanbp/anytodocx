package com.metinform.trans.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.poi.word.Word07Writer;
import com.metinform.trans.console.Console;
import com.metinform.trans.support.ConstantSet;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import org.apache.commons.lang3.StringUtils;

import java.awt.Font;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author chieftain
 * @date 2020/3/21 20:31
 */
public class MainController {

    public MainController() {
    }

    @FXML
    private Label scanLb;
    @FXML
    private TextField scanText;
    @FXML
    private Button scanBtn;
    @FXML
    private Label suffixLb;
    @FXML
    private TextField suffixText;
    @FXML
    private Label outLb;
    @FXML
    private TextField outText;
    @FXML
    private Button outBtn;
    @FXML
    private Label outFileNameLb;
    @FXML
    private TextField outFileNameText;
    @FXML
    private TextArea consoleTextArea;
    @FXML
    private Button start;
    @FXML
    private Button openBtn;

    private Console console;

    private static final String FILE_SUFFIX = ".docx";

    @FXML
    private void initialize() {
        console = new Console(consoleTextArea);
        console.appendText("欢迎使用AnyToDocx!");
        scanText.setEditable(false);
        outText.setEditable(false);
        consoleTextArea.setEditable(false);
        scanText.setStyle("-fx-background-color: #E9E9E9;");
        outText.setStyle("-fx-background-color: #E9E9E9;");
        consoleTextArea.setStyle("-fx-background-color: #E9E9E9;");
        consoleTextArea.setStyle("-fx-control-inner-background: #E9E9E9;");
    }

    @FXML
    public void scanBtnAction() {
        String scanDirectory = this.chooseDirectory("请选择扫描目录");
        if (StringUtils.isBlank(scanDirectory)) {
            console.appendText("请选择扫描目录!");
        }
        console.appendText(String.format("已选择扫描目录: %s", scanDirectory));
        scanText.setText(scanDirectory);
    }

    @FXML
    public void outBtnAction() {
        String scanDirectory = this.chooseDirectory("请选择保存目录");
        if (StringUtils.isBlank(scanDirectory)) {
            console.appendText("请选择保存目录!");
        }
        console.appendText(String.format("已选择保存目录: %s", scanDirectory));
        outText.setText(scanDirectory);
    }

    @FXML
    public void startBtnAction() {
        this.process();
    }

    @FXML
    public void openBtnAction() {
        String checkResult = this.beforeStartCheck();
        if (StringUtils.isNotBlank(checkResult)) {
            console.appendText(checkResult);
            return;
        }
        String out = outText.getText();
        String outFileName = outFileNameText.getText();
        String fullOutPath = out.concat(File.separator).concat(outFileName).concat(FILE_SUFFIX);
        File file = new File(fullOutPath);
        if (!file.exists()) {
            console.appendText("没有找到要打开的文件!");
        }
        this.openFile(fullOutPath);
    }

    private void process() {
        String checkResult = this.beforeStartCheck();
        if (StringUtils.isNotBlank(checkResult)) {
            console.appendText(checkResult);
            return;
        }
        console.appendText("正在输出文件......");
        String scanPath = scanText.getText();
        String suffix = suffixText.getText();
        String out = outText.getText();
        String outFileName = outFileNameText.getText();
        try (
                Stream<Path> walk = Files.walk(Paths.get(scanPath));
                Word07Writer writer = new Word07Writer()
        ) {
            String fullOutPath = out.concat(File.separator).concat(outFileName).concat(FILE_SUFFIX);
            List<Path> result = walk.filter(f -> f.toString().endsWith(".".concat(suffix))).collect(Collectors.toList());
            // 宋体小四号
            Font font = new Font("宋体", Font.PLAIN, 12);
            for (Path path : result) {
                this.writeDocx(writer, font, path);
            }
            writer.flush(FileUtil.file(fullOutPath));
            console.appendText("文件已生成: " + fullOutPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void writeDocx(Word07Writer writer, Font font, Path path) throws IOException {
        BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8);
        for (; ; ) {
            String line = reader.readLine();
            if (line == null) {
                break;
            }
            writer.addText(font, line);
        }
    }

    private String beforeStartCheck() {
        StringBuilder stringBuilder = new StringBuilder();
        if (StringUtils.isBlank(scanText.getText())) {
            stringBuilder.append("请选择扫描目录!\r\n");
        }
        if (StringUtils.isBlank(suffixText.getText())) {
            stringBuilder.append("请输入文件后缀!\r\n");
        }
        if (StringUtils.isBlank(outText.getText())) {
            stringBuilder.append("请选择输出目录!\r\n");
        }
        if (StringUtils.isBlank(outFileNameText.getText())) {
            stringBuilder.append("请选输入输出文件名!\r\n");
        }
        String checkResult = stringBuilder.toString();
        if (checkResult.endsWith("\r\n")) {
            checkResult = checkResult.substring(0, checkResult.lastIndexOf("\r\n"));
        }
        return checkResult;
    }

    private String chooseFile(String title) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);
        File file = fileChooser.showOpenDialog(ConstantSet.primaryStage);
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
//        可以设置扩展过滤器来确定在文件选择器中打开哪些文件。
//        fileChooser.getExtensionFilters().addAll(
//                new FileChooser.ExtensionFilter("All Images", "*.*"),
//                new FileChooser.ExtensionFilter("JPG", "*.jpg"),
//                new FileChooser.ExtensionFilter("GIF", "*.gif"),
//                new FileChooser.ExtensionFilter("BMP", "*.bmp"),
//                new FileChooser.ExtensionFilter("PNG", "*.png")
//        );
        if (null != file) {
            return file.getAbsolutePath();
        }
        return "";
    }

    private String chooseDirectory(String title) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle(title);
        File file = directoryChooser.showDialog(ConstantSet.primaryStage);
        directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        if (null != file) {
            return file.getAbsolutePath();
        }
        return "";
    }

    private void openFile(String filePath) {
        try {
            String command = this.concatCommand(filePath);
            Runtime.getRuntime().exec(command);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String concatCommand(String filePath) {
        String os = System.getProperty("os.name");
        if (os.toLowerCase().startsWith("win")) {
            return "start ".concat(filePath);
        }
        return "open ".concat(filePath);
    }
}
