package rse.smssimulator;

import rse.smssimulator.message.Callback;
import rse.smssimulator.message.CommandFire;
import rse.smssimulator.message.WeaponList;

public class FireProcess {
    private String mainMode = "Normal", attackMode = "N/A";
    private boolean wow = false;
    private float latitude = -1000, longtitude = -1000;//经纬度初始值
    private WeaponData weaponData = null;

    public FireProcess() {

    }

    public void loadWeaponData(WeaponData weaponData) {
        this.weaponData = weaponData;
    }

    public String getMainMode() {
        return mainMode;
    }

    public void setMainMode(String mainMode) {
        this.mainMode = mainMode;
    }

    public String getAttackMode() {
        return attackMode;
    }

    public void setAttackMode(String attackMode) {
        this.attackMode = attackMode;
    }

    public boolean isWow() {
        return wow;
    }

    public void setWow(boolean wow) {
        this.wow = wow;
    }

    public WeaponData getWeaponData() {
        return weaponData;
    }

    //执行发射命令，主要的代码写到这里
    public Callback executeFireCommand(CommandFire fireCommand) {
        Callback callback = new Callback(false);
        //System.out.println("555");
        System.out.println(fireCommand.getHangPoints() + fireCommand.getLanchType());
        ///执行发射的代码可以在这里编写
        String[] hangPoint = fireCommand.getHangPoints().split(",");
        if (hangPoint.length == 1) {
            weaponData.getWeapon(Integer.valueOf(hangPoint[0])).setCurrentCount(weaponData.getWeapon(Integer.valueOf(hangPoint[0])).getCurrentCount() - fireCommand.getCount());
            if (weaponData.getWeapon(Integer.valueOf(hangPoint[0])).getCurrentCount()==0){weaponData.getWeapon(Integer.valueOf(hangPoint[0])).setStatus("Failure");}
        } else {
            for (String string : hangPoint) {
                weaponData.getWeapon(Integer.valueOf(string)).setCurrentCount(0);
                if (weaponData.getWeapon(Integer.valueOf(string)).getCurrentCount()==0){weaponData.getWeapon(Integer.valueOf(string)).setStatus("Failure");}
            }
        }

        callback.setMessage("发射成功");
        return callback;
    }
}
