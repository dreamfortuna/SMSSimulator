package rse.smssimulator;

import com.esotericsoftware.kryonet.Connection;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.log4j.Logger;
import rse.smssimulator.message.Callback;
import rse.smssimulator.message.CommandMain;
import rse.smssimulator.message.CommandFire;
import rse.smssimulator.netservice.NetClient;
import rse.smssimulator.message.*;

import java.io.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Timer;
import java.util.TimerTask;

public class MainController extends NetClient {
    //调试用，在IDEA的console中显示调试信息
    private static final Logger logger = Logger.getLogger(MainController.class);
    @FXML
    public Label labelmainMode;
    @FXML
    public Label labelAttackMode;
    @FXML
    public Label labelWoWStatus;
    @FXML
    public Label labelPoint0Count;
    @FXML
    public Label labelPoint1Count;
    @FXML
    public Label labelPoint2Count;
    @FXML
    public Label labelPoint3Count;
    @FXML
    public Label labelPoint4Count;
    @FXML
    public Label labelPoint5Count;
    @FXML
    public Label labelPoint6Count;
    @FXML
    public Label labelPoint7Count;
    @FXML
    public Label labelPoint0Name;
    @FXML
    public Label labelPoint1Name;
    @FXML
    public Label labelPoint2Name;
    @FXML
    public Label labelPoint3Name;
    @FXML
    public Label labelPoint4Name;
    @FXML
    public Label labelPoint5Name;
    @FXML
    public Label labelPoint6Name;
    @FXML
    public Label labelPoint7Name;
    @FXML
    public Label labelPoint0Status;
    @FXML
    public Label labelPoint1Status;
    @FXML
    public Label labelPoint2Status;
    @FXML
    public Label labelPoint3Status;
    @FXML
    public Label labelPoint4Status;
    @FXML
    public Label labelPoint5Status;
    @FXML
    public Label labelPoint6Status;
    @FXML
    public Label labelPoint7Status;
    @FXML
    public Label labelLat;
    @FXML
    public Label labelLon;
    @FXML
    public TextArea textOutput;
    @FXML
    private Label labelFireResult;
    @FXML
    private Label lableGroupName;
    @FXML
    private Label lableMembers;


    //以下变量赋值之后，refresh()任务会自动将其显示到界面中
    private String mainMode = "Normal", attackMode = "N/A";//主控和攻击模式
    private boolean wow = true;//轮载状态
    private float latitude = 0, longtitude = 0;//经纬度
    //以上内容赋值之后，会自动在界面显示
    //设置用于显示的8个挂点，每个挂点含有3个Label控件，分别是名称、状态和数量
    private Boolean setLaLo = false;
    private WeaponPoint[] weaponPoints = new WeaponPoint[8];
    private UnlockProcess unlockProcess = new UnlockProcess();
    private FireProcess fireProcess = new FireProcess();//一个发送控制的类，需要自己编写，目的是将发控逻辑封装起来
    private WeaponData weaponData = new WeaponData();
    ;//武器状态数据，加载武器清单时，需要将内容都进去。但是加载是否正确，需要自己写代码判断
    private String strOutput = "";//用于在输出中显示信息
    private Connection currentConnection;//记录当前的连接，不需要操作
    private boolean commandSuccess = false;//加载或发射是否执行正确，用于在界面显示“成功”、“失败”
    private int resultShowTimes = 0;//控制显示“成功””失败“的时间，当大于0时自动执行
    private Stage stage;//当前窗体
    private boolean newCommand = false;//记录是否有新的控制命令来到
    private FireCheck fireCheck = new FireCheck();


    //连接服务器
    public MainController() {
        connectServer();//链接服务器
    }

    //清楚输出框内容
    @FXML
    public void onClear() {
        strOutput = "";
        textOutput.setText("");
    }

    //初始化界面
    public void init(Stage stage) {
        WeaponPoint p0 = new WeaponPoint(labelPoint0Name, labelPoint0Count, labelPoint0Status);
        WeaponPoint p1 = new WeaponPoint(labelPoint1Name, labelPoint1Count, labelPoint1Status);
        WeaponPoint p2 = new WeaponPoint(labelPoint2Name, labelPoint2Count, labelPoint2Status);
        WeaponPoint p3 = new WeaponPoint(labelPoint3Name, labelPoint3Count, labelPoint3Status);
        WeaponPoint p4 = new WeaponPoint(labelPoint4Name, labelPoint4Count, labelPoint4Status);
        WeaponPoint p5 = new WeaponPoint(labelPoint5Name, labelPoint5Count, labelPoint5Status);
        WeaponPoint p6 = new WeaponPoint(labelPoint6Name, labelPoint6Count, labelPoint6Status);
        WeaponPoint p7 = new WeaponPoint(labelPoint7Name, labelPoint7Count, labelPoint7Status);
        weaponPoints[0] = p0;
        weaponPoints[1] = p1;
        weaponPoints[2] = p2;
        weaponPoints[3] = p3;
        weaponPoints[4] = p4;
        weaponPoints[5] = p5;
        weaponPoints[6] = p6;
        weaponPoints[7] = p7;
        lableGroupName.setText(getGroupName());
        lableMembers.setText(getMembers());
        this.stage = stage;
        startRefreshTask();
    }

    //接受到来自控制器的消息
    @Override
    protected void clientReceived(Connection connection, Object object) throws IOException {
        currentConnection = connection;
        if (object instanceof CommandMain) {
            newCommand = true;
            execCommandMainMode(connection, object);
            if(weaponData!=null){
                weaponData.lockWeapons();
                System.out.println("LOCK");
            }
        } else if (object instanceof CommandFire) {
            newCommand = true;
            exeCommandcFire(connection, object);
        } else if (object instanceof CommandStatus) {
            execCommandStatus(connection, object);
        } else if (object instanceof CommandUnlock) {
            newCommand = true;
            execUnlockCommand(connection, object);
        } else if (object instanceof CommandAttackMode) {
            newCommand = true;
            execCommandAttackMode(connection, object);
            if(weaponData!=null){
                weaponData.lockWeapons();
                System.out.println("LOCK");
            }
        } else if (object instanceof CommandNavigation) {
            newCommand = true;
            execCommandNavigation(connection, object);
            if(weaponData!=null){
                weaponData.lockWeapons();
                System.out.println("LOCK");
            }
        } else if (object instanceof Message) {
            Message message = (Message) object;
            String strMsg = message.getMessage();
            showOutput(strMsg);
        }
        //非解锁和获取状态命令（CommandUnlock/Status）时需要在执行完命令时将解锁处重新锁定
    }

    //执行收到的经纬度信息
    private void execCommandNavigation(Connection connection, Object object) {
        Callback callback = new Callback(true);
        CommandNavigation nav = (CommandNavigation) object;
        if (!(mainMode.equals("Navigation"))) {
            callback.setMessage("纬度设置失败");
            callback.setSuccess(false);
            sendCallback(callback);
            return;
        }
        latitude = nav.getLatitude();
        longtitude = nav.getLongtitude();
        newCommand = true;
        callback.setMessage("纬度设置成功");
        setLaLo = true;
        sendCallback(callback);
    }

    //执行收到的攻击模式命令
    private void execCommandAttackMode(Connection connection, Object object) {
        CommandAttackMode cmd = (CommandAttackMode) object;
        attackMode = cmd.getAttackMode();//设置攻击模式
        newCommand = true;//只有设置newCommand=true，界面才会被刷新一次
    }

    //执行收到的发射命令
    private void exeCommandcFire(Connection connection, Object object) {
        Callback callback = new Callback(false);
        if (!(mainMode.equals("A/A") || mainMode.equals("A/F"))) {
            callback.setMessage("只有主模式是空空和空地才能发射武器");
            sendCallback(callback);
            return;
        }
        if ((wow) && !(attackMode.equals("AirGun"))) {
            callback.setMessage("必须在空中才能发射武器，只有航炮能够在空中或地面都能发射");
            sendCallback(callback);
            return;
        }
        if (!setLaLo && attackMode.equals("Bomb")) {
            callback.setMessage("当有经纬度数据的时候，才能投放炸弹");
            sendCallback(callback);
            return;
        }
        CommandFire cmd = (CommandFire) object;
        System.out.println(weaponData.getWeapon(Integer.valueOf(cmd.getHangPoints())).getStatus());
        fireCheck.FireOpenCheck(weaponData, cmd, attackMode, attackMode.equals("A/A"));
        callback.setSuccess(fireCheck.Success());
        callback.setMessage(fireCheck.Message());
        if (!fireCheck.Success()) {
            sendCallback(callback);
            return;
        }
        //执行发射命令，具体的发射流程需要在FireProcess中设置
        fireProcess.executeFireCommand(cmd);
        WeaponList wList = weaponData.getWeaponList();
        wList.setAttackMode(attackMode);
        wList.setMainMode(mainMode);
        wList.setWow(wow);
        wList.setOk(weaponData.isOk());
        currentConnection.sendTCP(wList);
    }

    //执行收到的解锁控制命令
    private void execUnlockCommand(Connection connection, Object object) {
        CommandUnlock cmd = (CommandUnlock) object;
        //执行解锁控制命令,具体解锁流程在UnlockProcess中设置
        if(wow==false&&(mainMode.equals("A/A")||mainMode.equals("A/F"))&&attackMode.equals("Missile")){

            Callback callback=unlockProcess.executeUnlockCommand(cmd,weaponData);
            System.out.println(weaponData.getWeapon(Integer.valueOf(Integer.valueOf(cmd.getHangPoints()))).getStatus());
            sendCallback(callback);
            if (callback.isSuccess()){
                WeaponList wList=weaponData.getWeaponList();
                wList.setAttackMode(attackMode);
                wList.setMainMode(mainMode);
                wList.setWow(wow);
                wList.setOk(weaponData.isOk());
                currentConnection.sendTCP(wList);
            }


            newCommand = true;
        }
        else {
            newCommand = true;
            showOutput("只有在空中及A/A或A/F模式、攻击武器为导弹才能解锁。");
        }

    }

    //执行收到的状态请求控制命令
    private void execCommandStatus(Connection connection, Object object) {
        CommandStatus cmd = (CommandStatus) object;
        showOutput(cmd.getCommandString());
        if (cmd.getCommand().equals("getStatus")) {
            if (weaponData != null) {
                WeaponList wList = weaponData.getWeaponList();
                wList.setAttackMode(attackMode);
                wList.setMainMode(mainMode);
                wList.setWow(wow);
                wList.setOk(weaponData.isOk());
                currentConnection.sendTCP(wList);
                showOutput("发送武器清单成功");
            }
        }
    }

    //执行收到的主控命令
    private void execCommandMainMode(Connection connection, Object object) {
        CommandMain cmd = (CommandMain) object;
        String mode = cmd.getMainMode();
        mainMode = mode;//设置当前主模式。定时执行的refresh()会自动刷新界面
        wow = cmd.isWow();//设置当前轮载状态。定时执行的refresh()会自动刷新界面
        fireProcess.setMainMode(mode);//将发射控制的主模式设置为接收到的主模式
        fireProcess.setWow(wow);//将发射控制的轮载状态设置为接收到的轮载状态
        newCommand = true;
        //显示收到的主控命令
        showOutput(cmd.getCommandString());
        //如果主控命令正常执行，则向控制器返回状态
        sendCallback(true, "主状态设置成功");
    }


    //刷新界面，只有有新命令的时候才会刷新
    private void refresh() {
        if (newCommand) {
            refreshMainMode();
            newCommand = false;
            refreshWeaponPoints();
            refreshNavigation();
        }
        refreshResult();
    }

    //刷新发射的结果
    private void refreshResult() {
        if (resultShowTimes > 0 && resultShowTimes < 3) {
            resultShowTimes++;
            String strFireResult = "失败";
            if (commandSuccess) {
                strFireResult = "成功";
            }
            labelFireResult.setText(strFireResult);
        } else {
            labelFireResult.setText("");
        }
    }


    //刷新主模式
    private void refreshMainMode() {
        //将控制命令显示为中文
        String attackModeShow = DisplayInfo.getAttackModeDisplay(attackMode);
        labelAttackMode.setText(attackModeShow);
        //将主控命令模式转为中文
        String strMainModeShow = DisplayInfo.getMainModeDisplay(mainMode);
        labelmainMode.setText(strMainModeShow);
        if (wow) {
            labelWoWStatus.setText("地面");
        } else {
            labelWoWStatus.setText("空中");
        }
    }

    //刷新导航信息
    private void refreshNavigation() {
        labelLat.setText(String.valueOf(latitude));
        labelLon.setText(String.valueOf(longtitude));
    }

    //刷新武器挂点信息
    private void refreshWeaponPoints() {
        if (weaponData != null) {
            if (weaponData.isOk()) {
                for (int i = 0; i < 8; i++) {
                    Weapon wi = weaponData.getWeapon(i);
                    if (wi != null) {
                        weaponPoints[i].showWeapon(wi);
                    }
                }
            }
        }
    }

    //开始一个定时刷新任务，来自网络的数据刷新会出错
    private void startRefreshTask() {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        //更新JavaFX的主线程的代码放在此处
                        refresh();
                    }
                });
            }
        };
        Timer timer = new Timer();
        timer.schedule(task, 1000, 1000);

    }


    //加载武器清单
    @FXML
    public void onLoadWeaponList() {
        if (isWow() && mainMode.equals("Maintain")) {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("加载武器清单");
//            File currentDir = new File("");
//            fileChooser.setInitialDirectory(currentDir);
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("武器清单", "*.wp"));

            File file = fileChooser.showOpenDialog(stage);
            String message = "";
            if (file != null) {
                int failureCount = 8;
                Weapon[] weapons = weaponData.loadWeaponList(file);
                for (int i = 0; i < 8; i++) {
                    Weapon weapon = weapons[i];
                    if (weapon != null) {
                        if (weapons[i].isOk()) {
                            failureCount--;
//                        message = "挂点 " + String.valueOf(i) + "加载失败!!";
//                        showOutput(message);
                        } else {
                            message = "挂点 " + String.valueOf(i) + "加载失败!!";
                            showOutput(message);
                        }
                    } else {
                        message = "挂点 " + String.valueOf(i) + "加载失败!!";
                        showOutput(message);
                    }

                }
                newCommand = true;
                if (failureCount == 0) {
                    weaponData.setOk(true);
                } else {
                    weaponData.setOk(false);
                }
                if (weaponData.isOk()) {
                    if (weaponData.checkWeaponStatus()) {
                        fireProcess.loadWeaponData(weaponData);
//                        setWeaponPoints(weaponData);
                    } else {
                        showOutput("武器挂点不对称");
                        weaponData.setOk(false);//重新设置为加载错误
                    }
                } else {
                    showWarning("武器清单加载失败", "请重新加载武器清单！");
                }
            }
        } else {
            showOutput("只有飞机在地面和维护状态时才能加载武器清单。");
        }
        commandSuccess = weaponData.isOk();
    }

    //发送反馈到服务器
    private void sendCallback(boolean success, String msg) {
        Callback callback = new Callback(success, msg);
        sendCallback(callback);
    }

    //发送反馈到服务器
    private void sendCallback(Callback callback) {
        callback.setLocalHost(getLocalHostName());
        currentConnection.sendTCP(callback);
        showOutput(callback.getMessage());
    }

    //显示警告弹出框
    private void showWarning(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setHeaderText(title);
        alert.setContentText(content);
        alert.initOwner(stage);
        alert.showAndWait();
    }

    //显示输出到界面
    private void showOutput(String msg) {
        LocalTime t = LocalTime.now();
        DateTimeFormatter df = DateTimeFormatter.ofPattern("HH:mm:ss");
        String strTime = "[" + df.format(t) + "]";
        strOutput = strOutput + "\r\n" + strTime + msg;
        if (textOutput != null) {
            textOutput.setText(strOutput);
            textOutput.selectPositionCaret(strOutput.length());
            textOutput.deselect();
        }

    }

    //判断当前的轮载状态
    public boolean isWow() {
        return wow;
    }
}
