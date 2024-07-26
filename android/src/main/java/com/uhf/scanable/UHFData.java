package com.uhf.scanable;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class UHfData {
  public static List<InventoryTagMap> lsTagList = new ArrayList<InventoryTagMap>();
  public static Map<String, Integer> dtIndexMap = new LinkedHashMap<String, Integer>();
  static DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
  private static int scanned_num;
  private static int isTagxit = 0;
  private static byte target = 0;
  // private static boolean isDeviceOpen = false;
  private static String fh_id;
  private String addr;
  private String num;

  public static String getuhf_id() {
    return fh_id;
  }

  public static void setuhf_id(String fh_id) {
    UHfData.fh_id = fh_id;
  }

  public static int getScanedNum() {
    return scanned_num;
  }

  public static void Inventory_6c(int Session, int tid_flag, int ant) {
    try {
      byte antindex = 0;
      if (ant == 0x80)
        antindex = 1;
      else if (ant == 0x81)
        antindex = 2;
      else if (ant == 0x82)
        antindex = 4;
      else if (ant == 0x83)
        antindex = 8;
      String[] label = UHFGetData.Scan6C(Session, tid_flag, ant);
      if (label == null) {
        scanned_num = 0;
        return;
      }
      scanned_num = label.length;
      for (int i = 0; i < scanned_num; i++) {
        String epc = label[i].substring(0, label[i].length() - 2);
        String rssi = label[i].substring(label[i].length() - 2);
        if (epc == null || epc.equals(""))
          return;
        InventoryTagMap tag = null;
        Integer findIndex = dtIndexMap.get(epc);
        // if (findIndex == null) {
        tag = new InventoryTagMap();
        tag.strEPC = epc;
        tag.strRSSI = rssi;
        tag.nReadCount = 1;
        tag.btAntId = (byte) antindex;
        lsTagList.add(tag);
        // } else {
        // tag = lsTagList.get(findIndex);
        // tag.strRSSI = rssi;
        // tag.btAntId |= (byte) antindex;
        // tag.nReadCount++;
        // }
      }
    } catch (Exception e) {
      e.toString();
    }
  }

  public String getAddr() {
    return addr;
  }

  public void setAddr(String addr) {
    this.addr = addr;
  }

  public String getNum() {
    return num;
  }

  public void setNum(String num) {
    this.num = num;
  }

  // String[] UID = new String[9];
  public static class InventoryTagMap {
    public String strEPC;
    public byte btAntId;
    public String strRSSI;
    public int nReadCount;
  }

  public static class UHFGetData {
    static UHFLib uhf = null;
    private static byte[] Read6Cdata = new byte[256];
    private static byte[] hfTime = { -1 };
    private static int Scan6CNum = -1;
    private static byte[] Scan6CData = new byte[20000];

    public static byte[] getRead6Cdata() {
      return Read6Cdata;
    }

    public static byte[] getUhfVersion() {

      return uhf.Get_TVersionInfo();
    }

    public static byte[] getUhfBand() {

      return uhf.Get_band();
    }

    public static byte[] getUhfMaxFre() {

      return uhf.Get_dmaxfre();
    }

    public static byte[] getUhfMinFre() {

      return uhf.Get_dminfre();
    }

    public static byte[] getUHFdbm() {

      return uhf.Get_powerdBm();
    }

    public static int setPower(byte power) {
      return uhf.SetReader_Power(power);
    }

    public static byte[] getUhfTime() {
      return uhf.Get_ScanTime();
    }

    public static int getScanNum_6c() {
      return Scan6CNum;
    }

    public static byte[] getScanUID_6c() {
      return Scan6CData;
    }

    public static UHFLib getUhf() {
      return uhf;
    }

    public static int openUhf(String serial, int tty_speed, int log_switch, Context mCt) {
      try {
        File device = new File(serial);
        if (!device.canRead() || !device.canWrite()) {
          try {
            Process su;
            su = Runtime.getRuntime().exec("/system/bin/su");
            String cmd = "chmod 666 " + device.getAbsolutePath() + "\n"
                + "exit\n";
            su.getOutputStream().write(cmd.getBytes());
            if ((su.waitFor() != 0) || !device.canRead()
                || !device.canWrite()) {
              throw new SecurityException();
            }
          } catch (Exception e) {
          }
        }

        uhf = new UHFLib(tty_speed, serial, 0, mCt);
        return uhf.open_reader();
      } catch (Exception e) {
        return -1;
      }
    }

    public static int closeUhf() {
      try {
        return uhf.close_reader();
      } catch (Exception e) {
        return -1;
      }
    }

    public static int GetUhfInfo() {
      try {
        return uhf.ReGetInfo();
      } catch (Exception e) {
        return -1;
      }

    }

    public static int SetUhfInfo(byte band, byte maxFre, byte minFre, byte power,
        byte b) {
      int result1 = uhf.SetReader_Region(band, maxFre, minFre);
      int result2 = uhf.SetReader_Power(power);
      if (result1 == 0 && result2 == 0) {
        uhf.ReGetInfo();
        return 0;
      } else
        return -1;

    }

    public static String byteToString(byte[] b) {
      StringBuffer sb = new StringBuffer();
      for (int i = 0; i < b.length; i++) {
        sb.append(Integer.toHexString(b[i] & 0xff));
      }
      return sb.toString();
    }

    public static byte[] stringToByte(String str) {
      byte[] b = new byte[str.length()];
      for (int i = 0; i < str.length(); i++) {
        b[i] = Byte.valueOf(str.substring(i, i + 1));
      }
      return b;
    }

    public static String byteToString(byte[] b, int len) {
      StringBuffer sb = new StringBuffer();
      for (int i = 0; i < len; i++) {
        sb.append(Integer.toHexString(b[i] & 0xff));
      }
      return sb.toString();
    }

    public static String[] Scan6C(int Session, int tid_flag, int ant) {
      try {
        byte tidaddr = 0, tidlen = 0;
        if (Session == 255)
          tid_flag = 0;
        if (tid_flag == 0) {
          tidaddr = 0;
          tidlen = 0;
        } else {
          tidaddr = 0;
          tidlen = 6;
        }
        if ((Session == 0) || (Session == 1))
          target = 0;
        Scan6CNum = 0;
        int result = uhf.EPCC1G2_ScanEPC((byte) 4, (byte) Session, tidaddr, tidlen, target, (byte) ant, (byte) 20);
        if (result == 0) {
          Scan6CNum = uhf.EPCC1G2_Inventory_POUcharTagNum();
          isTagxit = 0;
          Scan6CData = uhf.EPCC1G2_Inventory_pOUcharUIDList();
          String[] label = new String[Scan6CNum];
          StringBuffer bf;
          int j = 0, k;
          String str;
          byte[] epc;
          Log.i("yl", "num = " + Scan6CNum + ">>>>>>" + "len = " + Scan6CData.length);
          for (int i = 0; i < Scan6CNum; i++) {
            bf = new StringBuffer();
            Log.i("yl", "length = " + Scan6CData[j]);
            epc = new byte[(Scan6CData[j] + 1) & 0xff];// ��RSSI�ֽ�
            for (k = 0; k < ((Scan6CData[j] + 1) & 0xff); k++) {
              str = Integer.toHexString(Scan6CData[j + k + 1] & 0xff);
              if (str.length() == 1) {
                bf.append("0");
              }
              bf.append(str);
              epc[k] = Scan6CData[j + k + 1];
            }
            label[i] = bf.toString().toUpperCase();
            j = j + k + 1;
          }
          return label;
        } else if (result == 1)// �޿�
        {
          isTagxit++;
          if ((Session == 2) || (Session == 3)) {
            if (isTagxit > 14) {
              target = (byte) (1 - target);
              isTagxit = 0;
            }
          }
        }
      } catch (Exception e) {
      }
      return null;
    }

    public static int Read6C(byte ENum,
        byte[] EPC,
        byte Mem,
        byte WordPtr,
        byte Num,
        byte[] Password) {
      int result = uhf.ReadEPCC1G2(ENum, EPC, Mem, WordPtr, Num, Password);
      Read6Cdata = uhf.ReadEPCC1G2_Data();
      if (result == 0) {
        return 0;
      }
      return -1;
    }

    public static int Write6c(byte WNum,
        byte ENum,
        byte[] EPC,
        byte Mem,
        byte WordPtr,
        byte[] Writedata,
        byte[] Password) {
      int result = uhf.EPCC1G2_WriteCard(WNum, ENum, EPC, Mem, WordPtr, Writedata, Password);
      return result;
    }

    public static int WriteEPC(byte ENum, byte[] Password, byte[] EPC) {
      int result = uhf.EPCC1G2_WriteEPC(ENum, Password, EPC);
      return result;
    }

    /**
     * Convert byte[] to hex
     * string
     *
     * @param src byte[] data
     * @return hex string
     */
    public static String bytesToHexString(byte[] src) {
      StringBuilder stringBuilder = new StringBuilder();
      if (src == null || src.length <= 0) {
        return null;
      }
      for (int i = 0; i < src.length; i++) {
        int v = src[i] & 0xFF;
        String hv = Integer.toHexString(v);
        if (hv.length() == 1) {
          hv = '0' + hv;
        }
        stringBuilder.append(hv);
      }
      return stringBuilder.toString();
    }

    public static String bytesToHexString(byte[] src, int offset, int length) {
      StringBuilder stringBuilder = new StringBuilder();
      if (src == null || src.length <= 0) {
        return null;
      }
      for (int i = offset; i < length; i++) {
        int v = src[i] & 0xFF;
        String hv = Integer.toHexString(v);
        if (hv.length() == 1) {
          stringBuilder.append(0);
        }
        stringBuilder.append(hv);
      }
      return stringBuilder.toString();
    }

    public static byte[] hexStringToBytes(String hexString) {
      if (hexString == null || hexString.equals("")) {
        return null;
      }
      hexString = hexString.toUpperCase();
      int length = hexString.length() / 2;
      char[] hexChars = hexString.toCharArray();
      byte[] d = new byte[length];
      for (int i = 0; i < length; i++) {
        int pos = i * 2;
        d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
      }
      return d;
    }

    private static byte charToByte(char c) {
      return (byte) "0123456789ABCDEF".indexOf(c);
    }
  }
}
