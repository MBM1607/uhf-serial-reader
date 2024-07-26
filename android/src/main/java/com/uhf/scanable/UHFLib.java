package com.uhf.scanable;

import android.content.Context;

public class UHFLib {
  private static final String TAG = null;

  static {
    System.loadLibrary("UHFR2000");
  }

  private byte[] TVersionInfo = { -1, -1 };
  private byte[] ReaderType = { -1 };
  private byte[] TrType = { -1 };
  private byte[] band = { -1 };
  private byte[] dmaxfre = { -1 };
  private byte[] dminfre = { -1 };
  private byte[] powerdBm = { -1 };
  private byte[] scanTime = { -1 };
  private byte[] Ant = { -1 };
  private byte[] BeepEn = { -1 };
  private byte[] outputRep = { -1 };
  private byte[] checkAnt = { -1 };
  private byte[] state = new byte[1];
  private int[] cardNum = new int[1];
  private byte[] pOUcharUIDList = new byte[25600];
  private int[] pListLen = new int[1];
  private int[] pOUcharTagNum = new int[1];
  private byte[] Data = new byte[256];
  private byte[] Errorcode = new byte[1];
  private byte[] read_data = new byte[256];
  private int uhf_speed;
  private byte uhf_addr;
  private Context mContext;
  private String Serial;
  private int logswitch;

  public UHFLib(int tty_speed, String serial, int log_switch, Context mCt) {
    uhf_speed = tty_speed;
    uhf_addr = (byte) 255;
    mContext = mCt;
    Serial = serial;
    logswitch = 1;
  }

  static native int ConnectReader(String serial, int speed, int log_switch);

  static native int DisconnectReader();

  static native int SetInventoryScanTime(byte addr, byte scanTime);

  static native int SetRfPower(byte addr, byte power);

  static native int SetAddress(byte addr, byte newAddr);

  static native int SetRegion(byte addr, byte band, byte maxfre, byte minfre);

  static native int SetBaudRate(byte addr, byte fbaud);

  static native int ConfigAnt(byte addr, byte Ant);

  static native int ConfigDRM(byte addr, byte[] DRM);

  static native int GetReaderInformation(byte addr,
      byte[] TVersionInfo,
      byte[] ReaderType,
      byte[] TrType,
      byte[] band,
      byte[] dmaxfre,
      byte[] dminfre,
      byte[] powerdBm,
      byte[] scanTime,
      byte[] Ant,
      byte[] BeepEn,
      byte[] outputRep,
      byte[] checkAnt);

  static native int Inventory_G2(byte addr,
      byte QValue,
      byte Session,
      byte AdrTID,
      byte LenTID,
      byte Target,
      byte Ant,
      byte Scantime,
      byte[] pOUcharIDList,
      int[] pOUcharTagNum,
      int[] pListLen);

  static native int Inventory_Mask_G2(byte addr,
      byte MatchType,
      int MatchLen,
      int MatchOffset,
      byte[] EPCData,
      byte[] pOUcharIDList,
      int[] pOUcharTagNum);

  static native int ReadData_G2(byte addr,
      byte ENum,
      byte[] EPC,
      byte Mem,
      byte WordPtr,
      byte Num,
      byte[] Password,
      byte[] Data,
      byte[] Errorcode);

  static native int WriteData_G2(byte addr,
      byte WNum,
      byte ENum,
      byte[] EPC,
      byte Mem,
      byte WordPtr,
      byte[] Writedata,
      byte[] Password,
      byte[] Errorcode);

  static native int WriteEPC_G2(byte addr,
      byte ENum,
      byte[] Password,
      byte[] WriteEPC,
      byte[] Errorcode);

  public int array_clear(byte[] array_clear0) {
    int clear_len = array_clear0.length;
    for (int i = 0; i < clear_len; i++) {
      array_clear0[i] = 0;
    }
    return 0;
  }

  public int open_reader() {
    int reply1 = 1;
    reply1 = ConnectReader(Serial, uhf_speed, logswitch);
    return reply1;
  }

  public int ReGetInfo() {
    int result = GetReaderInformation(uhf_addr, TVersionInfo, ReaderType, TrType, band, dmaxfre, dminfre, powerdBm,
        scanTime, Ant, BeepEn, outputRep, checkAnt);
    if (result == 0)
      return 0;
    return -1;
  }

  public int close_reader() {

    DisconnectReader();
    return 0;
  }

  public int SetReader_Newaddress(byte newaddr) {
    if (SetAddress(uhf_addr, newaddr) == 0) {
      uhf_addr = newaddr;
      return 0;
    }
    return -1;

  }

  public int SetReader_ScanTime(byte scantime) {

    if (SetInventoryScanTime(uhf_addr, scantime) == 0) {
      scanTime[0] = scantime;
      return 0;
    }
    return -1;
  }

  public int SetReader_Power(byte power) {
    if (SetRfPower(uhf_addr, power) == 0) {
      powerdBm[0] = power;
      return 0;

    }
    return -1;
  }

  public int SetReader_Region(byte band, byte maxfre, byte minfre) {
    if (SetRegion(uhf_addr, band, maxfre, minfre) == 0) {
      return 0;
    }
    return -1;
  }

  public int SetReader_BaudRate(int fbaud) {
    byte fbaud1;
    if (fbaud == 9600)
      fbaud1 = 0x00;
    else if (fbaud == 19200)
      fbaud1 = 0x01;
    else if (fbaud == 38400)
      fbaud1 = 0x02;
    else if (fbaud == 57600)
      fbaud1 = 0x05;
    else if (fbaud == 115200)
      fbaud1 = 0x06;
    else
      return -1;
    if (SetBaudRate(uhf_addr, fbaud1) == 0) {
      uhf_speed = fbaud;
      return 0;
    }
    return -1;
  }

  public byte[] Get_TVersionInfo() {
    return TVersionInfo;

  }

  public byte[] Get_ReaderType() {
    return ReaderType;
  }

  public byte[] Get_TrType() {
    return TrType;
  }

  public byte[] Get_band() {
    return band;
  }

  public byte[] Get_dmaxfre() {
    return dmaxfre;
  }

  public byte[] Get_dminfre() {
    return dminfre;
  }

  public byte[] Get_powerdBm() {
    return powerdBm;
  }

  public byte[] Get_ScanTime() {
    return scanTime;
  }

  public byte[] Get_Ant() {
    return Ant;
  }

  public byte[] Get_BeepEn() {
    return BeepEn;
  }

  public byte[] Get_OutputRep() {
    return outputRep;
  }

  public byte[] Get_CheckAnt() {
    return checkAnt;
  }

  public int EPCC1G2_ScanEPC(byte QValue,
      byte Session,
      byte tidaddr,
      byte tidlen,
      byte target,
      byte Ant,
      byte Scantime) {
    int reply = -1;
    array_clear(pOUcharUIDList);
    pOUcharTagNum[0] = 0;
    reply = Inventory_G2(uhf_addr, QValue, Session, tidaddr, tidlen, target, Ant, Scantime,
        pOUcharUIDList, pOUcharTagNum, pListLen);
    return reply;

  }

  public byte[] EPCC1G2_Inventory_pOUcharUIDList() {
    return pOUcharUIDList;
  }

  public int EPCC1G2_Inventory_POUcharTagNum() {
    return pOUcharTagNum[0];
  }

  public int EPCC1G2_Inventory_POUcharReadlen() {
    return pListLen[0];
  }

  public int ReadEPCC1G2(
      byte ENum,
      byte[] EPC,
      byte Mem,
      byte WordPtr,
      byte Num,
      byte[] Password) {
    array_clear(Data);
    if (ReadData_G2(uhf_addr, ENum, EPC, Mem, WordPtr, Num, Password, Data, Errorcode) == 0)
      return 0;
    return -1;
  }

  public byte[] ReadEPCC1G2_Data() {
    return Data;
  }

  public byte ReadEPCC1G2_Errorcode() {
    return Errorcode[0];
  }

  public byte EPCC1G2_WriteCard(
      byte WNum,
      byte ENum,
      byte[] EPC,
      byte Mem,
      byte WordPtr,
      byte[] Writedata,
      byte[] Password) {
    int reply = -1;
    reply = WriteData_G2(uhf_addr, WNum, ENum, EPC, Mem, WordPtr, Writedata, Password, Errorcode);
    if (reply == 0)
      return Errorcode[0];
    return -1;
  }

  public byte EPCC1G2_WriteEPC(
      byte ENum,
      byte[] Password,
      byte[] WriteEPC) {
    int reply = -1;
    reply = WriteEPC_G2(uhf_addr, ENum, Password, WriteEPC, Errorcode);
    if (reply == 0)
      return Errorcode[0];
    return -1;
  }

  public byte[] get_readdata() {
    return read_data;
  }

}
