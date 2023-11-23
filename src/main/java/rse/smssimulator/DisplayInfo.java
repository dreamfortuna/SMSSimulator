package rse.smssimulator;

import java.util.HashMap;
import java.util.Map;

public class DisplayInfo {
    private static Map<String,String> mainModeMap=new HashMap<String,String>();
    private static Map<String,String> attackModeMap=new HashMap<String,String>();
    private static Map<String,String>statusMap=new HashMap<String,String>();
    //将控制命令转化为文字显示，此处为初始化控制命令与中文的对应关系
    static{
        mainModeMap.put("Normal","正常");
        mainModeMap.put("Maintain","维护");
        mainModeMap.put("BIT","自检测");
        mainModeMap.put("Navigation","导航");
        mainModeMap.put("A/A","空空");
        mainModeMap.put("A/F","空地");

        attackModeMap.put("Missile","导弹");
        attackModeMap.put("Bomb","炸弹");
        attackModeMap.put("Rocket","火箭弹");
        attackModeMap.put("AirGun","航炮");
        attackModeMap.put("OilTank","副油箱");
        attackModeMap.put("Missile","导弹");
        attackModeMap.put("N/A","N/A");

        statusMap.put("Invalid","无效");
        statusMap.put("Unlocked","解锁");
        statusMap.put("Locked","锁定");
        statusMap.put("Normal","正常");
        statusMap.put("Failure","故障");
    }
    //将主控制命令转化为中文显示
    public static String getMainModeDisplay(String mode){
        return mainModeMap.get(mode);
    }
    //将攻击命令转化为中文显示
    public static String getAttackModeDisplay(String mode){
        return attackModeMap.get(mode);
    }
    //得到武器状态的中文名称
    public static String getWeaponStatusName(String status){
        String mode=statusMap.get(status);
        if(mode==null){
            mode="无效";
        }
        return mode;
    }
}
