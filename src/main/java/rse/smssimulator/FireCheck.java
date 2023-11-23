package rse.smssimulator;

import rse.smssimulator.message.CommandFire;

public class FireCheck {
    private Boolean success = false;
    private String message;
    private int missileAscendingMap = 0;
    private int missileDescendingMap = 0;

    //private Boolean isAA=false;
    public void FireOpenCheck(WeaponData weaponData, CommandFire commandFire, String attackMode, Boolean isAA) {
        success = false;
        message ="";
        if (attackMode.equals("N/A")) {
            message = "请设置攻击武器";
            return;
        }
        String[] hangPoint = commandFire.getHangPoints().split(",");
        int firstPoint =Integer.valueOf(hangPoint[0]);
        if (attackMode.equals("Missile")) {
            if (!(weaponData.getWeapon(firstPoint).getType().contains("PL") || weaponData.getWeapon(firstPoint).getType().contains("KD"))) {
                message = "类型与选择不符";
                return;
            }
        }
        if (attackMode.equals("Bomb")) {
            if (!weaponData.getWeapon(firstPoint).getType().contains("KG")) {
                message = "类型与选择不符";
                return;
            }
        }
        if (attackMode.equals("Rocket")) {
            if (!weaponData.getWeapon(firstPoint).getType().contains("R")) {
                message = "类型与选择不符";
                return;
            }
        }
        if (attackMode.equals("AirGun")) {
            if (!weaponData.getWeapon(firstPoint).getType().contains("mm")) {
                message = "类型与选择不符";
                return;
            }
        }
        if (attackMode.equals("OilTank")) {
            if (!weaponData.getWeapon(firstPoint).getType().contains("L")) {
                message = "类型与选择不符";
                return;
            }
        }
        if (hangPoint.length != 1) {
            if (commandFire.getLanchType().equals("Single") || commandFire.getCount() != hangPoint.length) {
                message = "挂点数与投放方式或投放数量不符合";
                return;
            }
            if (!(attackMode.equals("Missile") || attackMode.equals("Bomb") || attackMode.equals("Rocket"))) {
                message = DisplayInfo.getAttackModeDisplay(attackMode) + "不可以多投";
                return;
            }
            int k = 0;
            for (int i=0;i<hangPoint.length;i++) {
                int l = Integer.valueOf(hangPoint[i]);
                k += l;
                if(weaponData.getWeapon(l).getStatus().equals("Failure")){
                    message="武器存在故障";
                    return;
                }
                if(weaponData.getWeapon(l).getCurrentCount()==0){
                    message="挂点"+l+"数量为0";
                    //weaponData.getWeapon(l).setStatus("Failure");
                    return;
                }
                if (isAA) {
                    if (!weaponData.getWeapon(l).getType().contains("AA")) {
                        message = "空空模式下投放的武器有：AA导弹、航炮";
                        return;
                    }
                } else {
                    if (weaponData.getWeapon(l).getType().contains("AA")) {
                        message = "空地模式下投放的武器有：AF导弹、火箭弹、炸弹、航炮，副油箱也在AF模式下投放";
                        return;
                    }
                }
                if (!weaponData.getWeapon(l).getType().equals(weaponData.getWeapon(Integer.valueOf(hangPoint[(i+1)% hangPoint.length])).getType())) {
                    message = "武器模式不统一";
                    return;
                }
                if (attackMode.equals("Missile") && weaponData.getWeapon(l).getStatus().equals("locked")) {
                    message = "导弹未解锁";
                    return;
                }
            }
            if (k % 6 != 0) {
                message = "不对称";
                return;
            }
        } else {

            if(weaponData.getWeapon(firstPoint).getStatus().equals("Failure")){
                    message="武器存在故障";
                    return;
            }
            if (weaponData.getWeapon(firstPoint).getCurrentCount() < commandFire.getCount()) {
                message = "发射数量应该小于现存数量";
                //weaponData.getWeapon(firstPoint).setStatus("Failure");
                return;
            }
            if (isAA) {
                if ((!weaponData.getWeapon(firstPoint).getType().contains("AA")) && (!attackMode.equals("airGun"))) {
                    message = "空空模式下投放的武器有：AA导弹、航炮";
                    return;
                }
            } else {
                if (weaponData.getWeapon(firstPoint).getType().contains("AA")) {
                    message = "空地模式下投放的武器有：AF导弹、火箭弹、炸弹、航炮，副油箱也在AF模式下投放";
                    return;
                }
            }
            if (attackMode.equals("Missile") && weaponData.getWeapon(firstPoint).getStatus().equals("locked")) {
                message = "导弹未解锁";
                return;

            }
            if (attackMode.equals("Missile")) {
                if (((missileDescendingMap ^ missileAscendingMap) != 0) && (((missileAscendingMap + (1 << firstPoint)) ^ (missileDescendingMap + (1 << (6 - firstPoint)))) != 0)) {
                    message = "导弹只能对称发射，即左翼发射后，下一个只能是右翼";
                    return;
                }
                missileAscendingMap += 1 << firstPoint;
                missileDescendingMap += 1 << (6 - firstPoint);
            }
            if (attackMode.equals("Bomb")){
                if (firstPoint!=3){
                    message="炸弹可以同时都放，但必须是对称的，即不能只投放某一侧机翼的。";
                    return;
                }
            }

        }
        success = true;
    }

    public Boolean Success() {
        return success;
    }

    public String Message() {
        return message;
    }
}
