package rse.smssimulator;

import rse.smssimulator.message.Callback;
import rse.smssimulator.message.CommandUnlock;

import java.util.Arrays;

public class UnlockProcess {
    private String mainMode_1="A/A",mainMode_2="A/F",attackMode="Missile";
    //private WeaponData weaponData =new WeaponData();
    public String[] strMissile = {"PL8","PL9","PL10","PL11","PL12","KD1","KD2","KD3","KD4","KD5","KD6","KD7","KD8"};


    public Callback executeUnlockCommand(CommandUnlock Command,WeaponData weaponData) {
        Callback callback=new Callback(false);
        String[] hangPoint = Command.getHangPoints().split(",");
        boolean res = false;
        for(int i=0;i<=hangPoint.length-1;i++) {
            String hangPoints = hangPoint[i];
            if (Arrays.asList(strMissile).contains(weaponData.getWeapon(Integer.valueOf(hangPoints)).getType())) {
                if (weaponData.getWeapon(Integer.valueOf(hangPoints)).getStatus().equals("Invalid")||weaponData.getWeapon(Integer.valueOf(hangPoints)).getStatus().equals("Failure")){
                    callback.setMessage("Failure or Invalid");
                    return callback;
                }
                weaponData.getWeapon(Integer.valueOf(hangPoints)).setStatus("Unlocked");
                res = true;
            } else {
                res = false;
            }
        }
        if(res){
            callback.setSuccess(true);
            callback.setMessage("解锁成功");
        }
        else {
            callback.setMessage("解锁失败");
        }
        return callback;
    }


}
