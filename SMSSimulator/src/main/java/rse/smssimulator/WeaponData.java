package rse.smssimulator;

import rse.smssimulator.message.Weapon;
import rse.smssimulator.message.WeaponList;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Arrays;

public class WeaponData {
    private boolean ok = false;
    private Weapon[] weaponList = new Weapon[8];
    public String[] strMissile = {"PL8","PL9","PL10","PL11","PL12","KD1","KD2","KD3","KD4","KD5","KD6","KD7","KD8"};
    public WeaponData() {

    }

    public Weapon[] loadWeaponList(File file) {
//        boolean[]pointStatus=new boolean[8];
        boolean success = true;
        try {
            FileInputStream inputStream = new FileInputStream(file);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String str = null;
            String message = "";
            int index = -1;
            while ((str = bufferedReader.readLine()) != null && index < 8) {
                index++;
                str = str.trim();
                if (str.length() > 5) {
                    Weapon wp = new Weapon();
                    if (wp.load(str)) {
                        wp.setOk(true);//是否是ok，需要判断，这里只是默认设置
                        weaponList[index] = wp;
                    }
                }
            }
            inputStream.close();
            bufferedReader.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
//        if(success){
//            updateWeapons();
//        }
        return weaponList;
    }

    public Weapon getWeapon(int pos) {
        return weaponList[pos];
    }

    public void setWeapon(int pos, Weapon weapon) {
        weaponList[pos] = weapon;
    }

    public boolean isOk() {
        return ok;
    }

    public void setOk(boolean ok) {
        this.ok = ok;
    }

    //将武器清单组装成WeaponList对象，以便于发送到controller
    public WeaponList getWeaponList() {
        WeaponList wList = new WeaponList();
        for (int i = 0; i < 8; i++) {
            wList.setWeapon(i, weaponList[i]);
        }
        return wList;
    }

    public boolean checkWeaponStatus() {
        if (!checkPoint(0, 6)) {
            return false;
        }
        if (!checkPoint(1, 5)) {
            return false;
        }
        if (!checkPoint(2, 4)) {
            return false;
        }
        return true;
    }

    private boolean checkPoint(int l, int r) {
        if (weaponList[l].isOk() && weaponList[r].isOk() || (!weaponList[l].isOk() && !weaponList[r].isOk())) {
            if (!weaponList[l].getType().equals(weaponList[r].getType())) {
                return false;
            } else {
                return true;
            }
        } else {
            return false;
        }
    }
    public boolean isMissile(Weapon weapon){
        boolean res = Arrays.asList(strMissile).contains(weapon.getType());
        return res;
    }
    public void lockWeapons() {
        for (int i = 0; i <= 6; i++) {
            //System.out.println(true);
            if (weaponList[i]==null)
                continue;
            if ((weaponList[i].getStatus().equals("Unlocked"))&&(isMissile(weaponList[i]))) {
                weaponList[i].setStatus("Locked");
            }
        }
    }
}
