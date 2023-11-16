package rse.smssimulator;

import javafx.scene.control.Label;
import javafx.scene.paint.Paint;
import rse.smssimulator.message.Weapon;
//将label控件组织在一起，便于显示
public class WeaponPoint {
    private Label lCount;
    private Label lName;
    private Label lStatus;
    public WeaponPoint(Label lName, Label lCount, Label lStatus){
        this.lCount=lCount;
        this.lName=lName;
        this.lStatus=lStatus;
    }
    public void showWeapon(String name,int count,String status){
        lName.setText(name);
        lCount.setText(String.valueOf(count));
        lStatus.setText(status);
    }
    public void showWeapon(Weapon wp){
        if(wp.isOk()){
            lName.setText(wp.getType());
            lCount.setText(String.valueOf(wp.getCurrentCount()));
            String status=wp.getStatus();
            if(status.equals("Failure")||status.equals("Invalid")){
                lStatus.setTextFill(Paint.valueOf("#FF0000"));
            }else if(status.equals("Unlocked")){
                lStatus.setTextFill(Paint.valueOf("#00FF00"));
            }else{
                lStatus.setTextFill(Paint.valueOf("#0000FF"));
            }
            lStatus.setText(DisplayInfo.getWeaponStatusName(wp.getStatus()));
        }else{
//            lName.setText("故障");
            lCount.setText("error");
            lStatus.setText("故障");
        }

    }
}
