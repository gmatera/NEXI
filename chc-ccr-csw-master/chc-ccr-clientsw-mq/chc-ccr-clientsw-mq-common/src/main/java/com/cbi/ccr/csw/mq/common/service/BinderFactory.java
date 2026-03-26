package com.cbi.ccr.csw.mq.common.service;

import com.cbi.ccr.csw.mq.common.dto.fmsfts.MQ1400SecSendFilereq;
import com.cbi.ccr.csw.mq.common.dto.fmsfts.MQ1401SecSendFilecnf;
import com.cbi.ccr.csw.mq.common.dto.fmsfts.MQ1402SecSendFileind;
import com.cbi.ccr.csw.mq.common.dto.fmsfts.MQ1405SecReceiveind;
import com.cbi.ccr.csw.mq.common.dto.fmsfts.MQ1406SecReleasereq;
import com.cbi.ccr.csw.mq.common.dto.fmsfts.MQ1407SecReleasecnf;
import com.cbi.ccr.csw.mq.common.dto.fmsfts.MQ1409SecReceiveFileInd;
import com.cbi.ccr.csw.mq.common.dto.fmsfts.MQ1410SecReadFilereq;
import com.cbi.ccr.csw.mq.common.dto.fmsfts.MQ1411SecReadFilecnf;
import com.cbi.ccr.csw.mq.common.dto.fmsfts.MQ1412SecReadFileind;
import com.cbi.ccr.csw.mq.common.dto.fmsfts.MQ1413SecPosCreateFileind;
import com.cbi.ccr.csw.mq.common.dto.fmsfts.MQ1419SecNotAckFilereq;
import com.cbi.ccr.csw.mq.common.dto.fmsfts.MQ1420SecNotAckFilecnf;
import com.cbi.ccr.csw.mq.common.dto.mss.MQ1911SecSendMsgreq;
import com.cbi.ccr.csw.mq.common.dto.mss.MQ1921SecSendMsgcnf;
import com.cbi.ccr.csw.mq.common.dto.mss.MQ1933SecReleaseMsgreq;
import com.cbi.ccr.csw.mq.common.dto.mss.MQ1934SecReleaseMsgcnf;
import com.cbi.ccr.csw.mq.common.dto.mss.MQ1941SecSendMsgind;
import com.cbi.ccr.csw.mq.common.dto.mss.MQ1951SecReceiveMsgind;
import com.cbi.ccr.csw.mq.common.dto.mss.MQ1991SecNotAckMsgreq;
import com.cbi.ccr.csw.mq.common.dto.mss.MQ1992SecNotAckMsgcnf;

import chc.framework.util.parsing.input.FlowioFixedPositionBinder;
import chc.framework.util.parsing.input.FlowioOutputMapper;
import chc.framework.util.parsing.input.FlowioPropertyFixed;
import lombok.NonNull;

public class BinderFactory {

	private static final String QUEUE_FILE_NAME = "queueFileName";
	private static final String BA_FILE_SIZE = "baFileSize";
	private static final String CORR_ID = "corrId";
	private static final String SYNC_FLAG = "syncFlag";
	private static final String BA_REM = "baRem";
	private static final String BA_LOC = "baLoc";

	private BinderFactory() {}
	
	public static FlowioFixedPositionBinder<MQ1401SecSendFilecnf> binder1401() {
		FlowioFixedPositionBinder<MQ1401SecSendFilecnf> binder1401 = new FlowioFixedPositionBinder<>(MQ1401SecSendFilecnf.class);
		binder1401.addProperty(new FlowioPropertyFixed("id", 0, 4));
		binder1401.addProperty(new FlowioPropertyFixed(BA_LOC, 4, 16));
		binder1401.addProperty(new FlowioPropertyFixed(BA_REM, 16, 28));
		binder1401.addProperty(new FlowioPropertyFixed("sendType", 28, 29));
		binder1401.addProperty(new FlowioPropertyFixed(SYNC_FLAG, 29, 30));
		binder1401.addProperty(new FlowioPropertyFixed(CORR_ID, 30, 60));
		binder1401.addProperty(new FlowioPropertyFixed("vfn", 60, 92));
		binder1401.addProperty(new FlowioPropertyFixed(BA_FILE_SIZE, 92, 102));
		binder1401.addProperty(new FlowioPropertyFixed(QUEUE_FILE_NAME, 102, 150));
		binder1401.addProperty(new FlowioPropertyFixed("groupId", 150, 174));
		binder1401.addProperty(new FlowioPropertyFixed("lineSeparator", 174, 175));
		binder1401.addProperty(new FlowioPropertyFixed("recType", 175, 176));
		binder1401.addProperty(new FlowioPropertyFixed("maxRecLen", 176, 182));
		binder1401.addProperty(new FlowioPropertyFixed("charType", 182, 183));
		binder1401.addProperty(new FlowioPropertyFixed("compressAlgo", 183, 191));
		binder1401.addProperty(new FlowioPropertyFixed("adfLen", 191, 193));
		binder1401.addProperty(new FlowioPropertyFixed("adf", 193, 273));
		binder1401.addProperty(new FlowioPropertyFixed("udrLen", 273, 275));
		binder1401.addProperty(new FlowioPropertyFixed("udr", 275, 355));
		binder1401.addProperty(new FlowioPropertyFixed("tur", 355, 371));
		binder1401.addProperty(new FlowioPropertyFixed("msgType", 371, 374));
		binder1401.addProperty(new FlowioPropertyFixed("catAppl", 374, 378));
		binder1401.addProperty(new FlowioPropertyFixed("localBaData", 378, 458));
		binder1401.addProperty(new FlowioPropertyFixed("extraDataLen", 458, 462));
		binder1401.addProperty(new FlowioPropertyFixed("extraData", 462, 2510));
		binder1401.addProperty(new FlowioPropertyFixed("sndBaFileDigestAlg", 2510, 2518));
		binder1401.addProperty(new FlowioPropertyFixed("sndBaFileDigestLen", 2518, 2521));
		binder1401.addProperty(new FlowioPropertyFixed("sndBaFileDigest", 2521, 2649));
		binder1401.addProperty(new FlowioPropertyFixed("mabDigestAlg", 2649, 2657));
		binder1401.addProperty(new FlowioPropertyFixed("mabDigestLen", 2657, 2660));
		binder1401.addProperty(new FlowioPropertyFixed("mabDigest", 2660, 2788));
		binder1401.addProperty(new FlowioPropertyFixed("filler1", 2788, 3788));
		binder1401.addProperty(new FlowioPropertyFixed("localAuthInfoAlg", 3788, 3796));
		binder1401.addProperty(new FlowioPropertyFixed("localAuthInfoLen", 3796, 3799));
		binder1401.addProperty(new FlowioPropertyFixed("localAuthInfo", 3799, 3927));
		binder1401.addProperty(new FlowioPropertyFixed("acceptTms", 3927, 3952)); //problema parsing localDatetime
		binder1401.addProperty(new FlowioPropertyFixed("result", 3952, 3955));
		binder1401.addProperty(new FlowioPropertyFixed("rejReason", 3955, 3958));
		binder1401.addProperty(new FlowioPropertyFixed("filler2", 3958, 4458));
		binder1401.addProperty(new FlowioPropertyFixed("actualLocalAuthInfoAlg", 4458, 4466));
		binder1401.addProperty(new FlowioPropertyFixed("actualLocalAuthInfoLen", 4466, 4469));
		binder1401.addProperty(new FlowioPropertyFixed("actualLocalAuthInfo", 4469, 4597));
		return binder1401;
	}
	
	public static FlowioFixedPositionBinder<MQ1402SecSendFileind> binder1402() {
		FlowioFixedPositionBinder<MQ1402SecSendFileind> binder = new FlowioFixedPositionBinder<>(MQ1402SecSendFileind.class);

		binder.addProperty(new FlowioPropertyFixed("id", 0, 4));
		binder.addProperty(new FlowioPropertyFixed(BA_LOC, 4, 16));
		binder.addProperty(new FlowioPropertyFixed(BA_REM, 16, 28));
		binder.addProperty(new FlowioPropertyFixed(SYNC_FLAG, 28, 29));
		binder.addProperty(new FlowioPropertyFixed(CORR_ID, 29, 59));
		binder.addProperty(new FlowioPropertyFixed("vfn", 59, 91));
		binder.addProperty(new FlowioPropertyFixed("adfLen", 91, 93));
		binder.addProperty(new FlowioPropertyFixed("adf", 93, 173));
		binder.addProperty(new FlowioPropertyFixed("udrLen", 173, 175));
		binder.addProperty(new FlowioPropertyFixed("udr", 175, 255));
		binder.addProperty(new FlowioPropertyFixed("tur", 255, 271));
		binder.addProperty(new FlowioPropertyFixed("locBaData", 271, 351));
		binder.addProperty(new FlowioPropertyFixed("extraDataLen", 351, 355));
		binder.addProperty(new FlowioPropertyFixed("extraData", 355, 2403));
		binder.addProperty(new FlowioPropertyFixed("result", 2403, 2406));
		binder.addProperty(new FlowioPropertyFixed("rejReason", 2406, 2409));
		binder.addProperty(new FlowioPropertyFixed("negDetailedOper", 2409, 2410));
		binder.addProperty(new FlowioPropertyFixed("transferId", 2410, 2426));
		binder.addProperty(new FlowioPropertyFixed("lineSeparator", 2426, 2427));
		binder.addProperty(new FlowioPropertyFixed("recType", 2427, 2428));
		binder.addProperty(new FlowioPropertyFixed("maxRecLen", 2428, 2434));
		binder.addProperty(new FlowioPropertyFixed(BA_FILE_SIZE, 2434, 2444));
		binder.addProperty(new FlowioPropertyFixed("charType", 2444, 2445));
		binder.addProperty(new FlowioPropertyFixed("compressAlgo", 2445, 2453));
		binder.addProperty(new FlowioPropertyFixed("netFileSize", 2453, 2463));
		binder.addProperty(new FlowioPropertyFixed("acceptTms", 2463, 2488));
		binder.addProperty(new FlowioPropertyFixed("startCreateTms", 2488, 2513));
		binder.addProperty(new FlowioPropertyFixed("endCreateTms", 2513, 2538));
		binder.addProperty(new FlowioPropertyFixed("errorTms", 2538, 2563));
		binder.addProperty(new FlowioPropertyFixed("hostFirstSubTms", 2563, 2588));
		binder.addProperty(new FlowioPropertyFixed("hostSubTms", 2588, 2613));
		binder.addProperty(new FlowioPropertyFixed("hostLastSubTms", 2613, 2638));
		binder.addProperty(new FlowioPropertyFixed("ferFirstSubTms", 2638, 2663));
		binder.addProperty(new FlowioPropertyFixed("fenFirstSubTms", 2663, 2688));
		binder.addProperty(new FlowioPropertyFixed("fenFirstDlvTms", 2688, 2713));
		binder.addProperty(new FlowioPropertyFixed("ferFirstDlvTms", 2713, 2738));
		binder.addProperty(new FlowioPropertyFixed("ferLastSubTms", 2738, 2763));
		binder.addProperty(new FlowioPropertyFixed("fenLastSubTms", 2763, 2788));
		binder.addProperty(new FlowioPropertyFixed("fenLastDlvTms", 2788, 2813));
		binder.addProperty(new FlowioPropertyFixed("ferLastDlvTms", 2813, 2838));
		binder.addProperty(new FlowioPropertyFixed("hostFirstDlvTms", 2838, 2863));
		binder.addProperty(new FlowioPropertyFixed("fermsSubTms", 2863, 2888));
		binder.addProperty(new FlowioPropertyFixed("fenmsSubTms", 2888, 2913));
		binder.addProperty(new FlowioPropertyFixed("fenmsDlvTms", 2913, 2938));
		binder.addProperty(new FlowioPropertyFixed("fermsDlvTms", 2938, 2963));
		binder.addProperty(new FlowioPropertyFixed("firstAckMSTms", 2963, 2988));
		binder.addProperty(new FlowioPropertyFixed("completeTms", 2988, 3013));
		binder.addProperty(new FlowioPropertyFixed("signExist", 3013, 3014));
//		binder.addProperty(new FlowioPropertyFixed("signatureData", 3014, ?));
		binder.addProperty(new FlowioPropertyFixed("sndCertificateLabel", 3014, 3046));
		binder.addProperty(new FlowioPropertyFixed("sndSignAlgo", 3046, 3054));
		binder.addProperty(new FlowioPropertyFixed("sndSignLen", 3054, 3057));
		binder.addProperty(new FlowioPropertyFixed("sndSignTms", 3057, 3082));
		binder.addProperty(new FlowioPropertyFixed("sndSignFemsId", 3082, 3094));
		binder.addProperty(new FlowioPropertyFixed("sndSignServerId", 3094, 3106));
		binder.addProperty(new FlowioPropertyFixed("sndSignCertSubject", 3106, 3362));
		binder.addProperty(new FlowioPropertyFixed("sndSignResult", 3362, 3363));
		binder.addProperty(new FlowioPropertyFixed("checkSndSignResult", 3363, 3364));
		binder.addProperty(new FlowioPropertyFixed("sndSign", 3364, 3620));
		binder.addProperty(new FlowioPropertyFixed("rcvCertificateLabel", 3620, 3652));
		binder.addProperty(new FlowioPropertyFixed("rcvSignAlgo", 3652, 3660));
		binder.addProperty(new FlowioPropertyFixed("rcvSignLen", 3660, 3663));
		binder.addProperty(new FlowioPropertyFixed("rcvSignTms", 3663, 3688));
		binder.addProperty(new FlowioPropertyFixed("rcvSignFemsId", 3688, 3700));
		binder.addProperty(new FlowioPropertyFixed("rcvSignServerId", 3700, 3712));
		binder.addProperty(new FlowioPropertyFixed("rcvSignCertSubject", 3712, 3968));
		binder.addProperty(new FlowioPropertyFixed("rcvSignResult", 3968, 3969));
		binder.addProperty(new FlowioPropertyFixed("checkRcvSignResult", 3969, 3970));
		binder.addProperty(new FlowioPropertyFixed("rcvSign", 3970, 4226));
		binder.addProperty(new FlowioPropertyFixed("fileApplDataDigestAlg", 4226, 4234));
		binder.addProperty(new FlowioPropertyFixed("fileApplDataDigestLen", 4234, 4237));
		binder.addProperty(new FlowioPropertyFixed("fileApplDataDigest", 4237, 4365));
		binder.addProperty(new FlowioPropertyFixed("netFileDigestAlg", 4365, 4373));
		binder.addProperty(new FlowioPropertyFixed("netFileDigestLen", 4373, 4376));
		binder.addProperty(new FlowioPropertyFixed("netFileDigest", 4376, 4504));
		binder.addProperty(new FlowioPropertyFixed("mabDigestAlg", 4504, 4512));
		binder.addProperty(new FlowioPropertyFixed("mabDigestLen", 4512, 4515));
		binder.addProperty(new FlowioPropertyFixed("mabDigest", 4515, 4643));
		binder.addProperty(new FlowioPropertyFixed("filler", 4643, 5143));
		binder.addProperty(new FlowioPropertyFixed("localAuthInfoAlg", 5143, 5151));
		binder.addProperty(new FlowioPropertyFixed("localAuthInfoLen", 5151, 5154));
		binder.addProperty(new FlowioPropertyFixed("localAuthInfo", 5154, 5282));
		//return new FlowioOutputMapper<>(binder);
		return binder;
	}
	
	public static FlowioFixedPositionBinder<MQ1921SecSendMsgcnf> binder1921(Integer mabLeng) {
		FlowioFixedPositionBinder<MQ1921SecSendMsgcnf> binder = new FlowioFixedPositionBinder<>(MQ1921SecSendMsgcnf.class);
		
		binder.addProperty(new FlowioPropertyFixed("id", 0, 4));
		binder.addProperty(new FlowioPropertyFixed("locBaData", 4, 54));
		binder.addProperty(new FlowioPropertyFixed("baLoc", 54, 66));
		binder.addProperty(new FlowioPropertyFixed("baRem", 66, 78));
		binder.addProperty(new FlowioPropertyFixed("priority", 78, 79));
		binder.addProperty(new FlowioPropertyFixed("tur", 79, 95));
		binder.addProperty(new FlowioPropertyFixed("baReqTms", 95, 107));
		binder.addProperty(new FlowioPropertyFixed("msgType", 107, 110));
		binder.addProperty(new FlowioPropertyFixed("catAppl", 110, 114));
		binder.addProperty(new FlowioPropertyFixed("corrId", 114, 144));
		binder.addProperty(new FlowioPropertyFixed("udrLen", 144, 146));
		binder.addProperty(new FlowioPropertyFixed("udr", 146, 226));
		binder.addProperty(new FlowioPropertyFixed("extraDataLen", 226, 230));
		binder.addProperty(new FlowioPropertyFixed("extraData", 230, 2278));
		binder.addProperty(new FlowioPropertyFixed("mabDigestAlg", 2278, 2286));
		binder.addProperty(new FlowioPropertyFixed("mabDigestLen", 2286, 2289));
		binder.addProperty(new FlowioPropertyFixed("mabDigest", 2289, 2417));
		binder.addProperty(new FlowioPropertyFixed("filler1", 2417, 3417));
		binder.addProperty(new FlowioPropertyFixed("reqLocalAuthInfoAlg", 3417, 3425));
		binder.addProperty(new FlowioPropertyFixed("reqLocalAuthInfoLen", 3425, 3428));
		binder.addProperty(new FlowioPropertyFixed("reqLocalAuthInfo", 3428, 3556));
		binder.addProperty(new FlowioPropertyFixed("acceptTms", 3556, 3581));
		binder.addProperty(new FlowioPropertyFixed("result", 3581, 3582));
		binder.addProperty(new FlowioPropertyFixed("rejReason", 3582, 3585));
		binder.addProperty(new FlowioPropertyFixed("aggrI", 3585, 3597));
		binder.addProperty(new FlowioPropertyFixed("aggrII", 3597, 3609));
		binder.addProperty(new FlowioPropertyFixed("msgId", 3609, 3625));
		binder.addProperty(new FlowioPropertyFixed("filler2", 3625, 4125));
		binder.addProperty(new FlowioPropertyFixed("mabLen", 4125, 4135));
		binder.addProperty(new FlowioPropertyFixed("localAuthInfoAlg", 4135, 4143));
		binder.addProperty(new FlowioPropertyFixed("localAuthInfoLen", 4143, 4146));
		if(mabLeng != null && mabLeng != 0) {
			int startColumn = 4146;
			int mab = startColumn + mabLeng;
			int locInfo = mab + 128;
			if(mab != 4146)
				binder.addProperty(new FlowioPropertyFixed("mab", 4146, mab));
			binder.addProperty(new FlowioPropertyFixed("localAuthInfo", mab, locInfo));
		} else {
			binder.addProperty(new FlowioPropertyFixed("localAuthInfo", 4146, 4274));
		}

		
//		binder.addProperty(new FlowioPropertyFixed("mab", 4146, 4146));
//		binder.addProperty(new FlowioPropertyFixed("localAuthInfo", 4146, 4274));
		return binder;
	}

	public static FlowioFixedPositionBinder<MQ1941SecSendMsgind> binder1941() {
		FlowioFixedPositionBinder<MQ1941SecSendMsgind> binder = new FlowioFixedPositionBinder<>(MQ1941SecSendMsgind.class);

		binder.addProperty(new FlowioPropertyFixed("id", 0, 4));
		binder.addProperty(new FlowioPropertyFixed(BA_LOC, 4, 16));
		binder.addProperty(new FlowioPropertyFixed(BA_REM, 16, 28));
		binder.addProperty(new FlowioPropertyFixed("priority", 28, 29));
		binder.addProperty(new FlowioPropertyFixed("tur", 29, 45));
		binder.addProperty(new FlowioPropertyFixed("msgType", 45, 48));
		binder.addProperty(new FlowioPropertyFixed("catAppl", 48, 52));
		binder.addProperty(new FlowioPropertyFixed(CORR_ID, 52, 82));
		binder.addProperty(new FlowioPropertyFixed("udrLen", 82, 84));
		binder.addProperty(new FlowioPropertyFixed("udr", 84, 164));
		binder.addProperty(new FlowioPropertyFixed("extraDataLen", 164, 168));
		binder.addProperty(new FlowioPropertyFixed("extraData", 168, 2216));
		binder.addProperty(new FlowioPropertyFixed("filler1", 2216, 2716));
		binder.addProperty(new FlowioPropertyFixed("result", 2716, 2717));
		binder.addProperty(new FlowioPropertyFixed("rejReason", 2717, 2720));
		binder.addProperty(new FlowioPropertyFixed("aggrIid", 2720, 2732));
		binder.addProperty(new FlowioPropertyFixed("aggrIIid", 2732, 2744));
		binder.addProperty(new FlowioPropertyFixed("msgId", 2744, 2760));
		binder.addProperty(new FlowioPropertyFixed("acceptTms", 2760, 2785));
		binder.addProperty(new FlowioPropertyFixed("hostFirstsubTms", 2785, 2810));
		binder.addProperty(new FlowioPropertyFixed("hostSubTms", 2810, 2835));
		binder.addProperty(new FlowioPropertyFixed("ferSubTms", 2835, 2860));
		binder.addProperty(new FlowioPropertyFixed("fenSubTms", 2860, 2885));
		binder.addProperty(new FlowioPropertyFixed("fenDlvTms", 2885, 2910));
		binder.addProperty(new FlowioPropertyFixed("ferDlvTms", 2910, 2935));
		binder.addProperty(new FlowioPropertyFixed("hostFirstDlvTms", 2935, 2960));
		binder.addProperty(new FlowioPropertyFixed("completeTms", 2960, 2985));
		binder.addProperty(new FlowioPropertyFixed("signExist", 2985, 2986));
		// Signature Data
		binder.addProperty(new FlowioPropertyFixed("sndCertificateLabel", 2986, 3018));
		binder.addProperty(new FlowioPropertyFixed("sndSignAlgo", 3018, 3026));
		binder.addProperty(new FlowioPropertyFixed("sndSignLen", 3026, 3029));
		binder.addProperty(new FlowioPropertyFixed("sndSignTms", 3029, 3054));
		binder.addProperty(new FlowioPropertyFixed("sndSignFemsId", 3054, 3066));
		binder.addProperty(new FlowioPropertyFixed("sndSignServerId", 3066, 3078));
		binder.addProperty(new FlowioPropertyFixed("sndSignCertSubject", 3078, 3334));
		binder.addProperty(new FlowioPropertyFixed("sndSignResult", 3334, 3335));
		binder.addProperty(new FlowioPropertyFixed("checkSndSignResult", 3335, 3336));
		binder.addProperty(new FlowioPropertyFixed("sndSign", 3336, 3592));
		binder.addProperty(new FlowioPropertyFixed("rcvCertificateLabel", 3592, 3624));
		binder.addProperty(new FlowioPropertyFixed("rcvSignAlgo", 3624, 3632));
		binder.addProperty(new FlowioPropertyFixed("rcvSignLen", 3632, 3635));
		binder.addProperty(new FlowioPropertyFixed("rcvSignTms", 3635, 3660));
		binder.addProperty(new FlowioPropertyFixed("rcvSignFemsId", 3660, 3672));
		binder.addProperty(new FlowioPropertyFixed("rcvSignServerId", 3672, 3684));
		binder.addProperty(new FlowioPropertyFixed("rcvSignCertSubject", 3684, 3940));
		binder.addProperty(new FlowioPropertyFixed("rcvSignResult", 3940, 3941));
		binder.addProperty(new FlowioPropertyFixed("checkRcvSignResult", 3941, 3942));
		binder.addProperty(new FlowioPropertyFixed("rcvSign", 3942, 4198));
		binder.addProperty(new FlowioPropertyFixed("mabDigestAlg", 4198, 4206));
		binder.addProperty(new FlowioPropertyFixed("mabDigestLen", 4206, 4209));
		binder.addProperty(new FlowioPropertyFixed("mabDigest", 4209, 4337));
		binder.addProperty(new FlowioPropertyFixed("filler2", 4337, 4837));
		binder.addProperty(new FlowioPropertyFixed("localAuthInfoAlg", 4837, 4845));
		binder.addProperty(new FlowioPropertyFixed("localAuthInfoLen", 4845, 4848));
		binder.addProperty(new FlowioPropertyFixed("localAuthInfo", 4848, 4976));
		return binder;
	}

	public static FlowioFixedPositionBinder<MQ1412SecReadFileind> binder1412() {
		FlowioFixedPositionBinder<MQ1412SecReadFileind> binder = new FlowioFixedPositionBinder<>(MQ1412SecReadFileind.class);

		binder.addProperty(new FlowioPropertyFixed("id", 0, 4));
		binder.addProperty(new FlowioPropertyFixed(BA_LOC, 4, 16));
		binder.addProperty(new FlowioPropertyFixed(BA_REM, 16, 28));
		binder.addProperty(new FlowioPropertyFixed(SYNC_FLAG, 28, 29));
		binder.addProperty(new FlowioPropertyFixed(CORR_ID, 29, 59));
		binder.addProperty(new FlowioPropertyFixed("vfn", 59, 91));
		binder.addProperty(new FlowioPropertyFixed("aggrI", 91, 103));
		binder.addProperty(new FlowioPropertyFixed("aggrII", 103, 115));
		binder.addProperty(new FlowioPropertyFixed("adfLen", 115, 117));
		binder.addProperty(new FlowioPropertyFixed("adf", 117, 197));
		binder.addProperty(new FlowioPropertyFixed("result", 197, 200));
		binder.addProperty(new FlowioPropertyFixed("rejReason", 200, 203));
		binder.addProperty(new FlowioPropertyFixed(QUEUE_FILE_NAME, 203, 251));
		binder.addProperty(new FlowioPropertyFixed("groupId", 251, 275));
		binder.addProperty(new FlowioPropertyFixed("lineSeparatorRcv", 275, 276));
		binder.addProperty(new FlowioPropertyFixed("recType", 276, 277));
		binder.addProperty(new FlowioPropertyFixed("maxRecLen", 277, 283));
		binder.addProperty(new FlowioPropertyFixed("fillerOne", 283, 293));
		binder.addProperty(new FlowioPropertyFixed(BA_FILE_SIZE, 293, 303));
		binder.addProperty(new FlowioPropertyFixed("sndCharType", 303, 304));
		binder.addProperty(new FlowioPropertyFixed("rcvCharType", 304, 305));
		binder.addProperty(new FlowioPropertyFixed("compressAlgo", 305, 313));
		binder.addProperty(new FlowioPropertyFixed("netFileSize", 313, 323));
		binder.addProperty(new FlowioPropertyFixed("transferId", 323, 339));
		binder.addProperty(new FlowioPropertyFixed("hostFirstSubTms", 339, 364));
		binder.addProperty(new FlowioPropertyFixed("ferFirstBSubTms", 364, 389));
		binder.addProperty(new FlowioPropertyFixed("fenFirstBSubTms", 389, 414));
		binder.addProperty(new FlowioPropertyFixed("fenFirstBDlvTms", 414, 439));
		binder.addProperty(new FlowioPropertyFixed("ferFirstBDlvTms", 439, 464));
		binder.addProperty(new FlowioPropertyFixed("ferLastBSubTms", 464, 489));
		binder.addProperty(new FlowioPropertyFixed("fenLastBSubTms", 489, 514));
		binder.addProperty(new FlowioPropertyFixed("fenLastBDlvTms", 514, 539));
		binder.addProperty(new FlowioPropertyFixed("ferLastBDlvTms", 539, 564));
		binder.addProperty(new FlowioPropertyFixed("hostFirstDlvTms", 564, 589));
		binder.addProperty(new FlowioPropertyFixed("ftsEndReceiveTms", 589, 614));
		binder.addProperty(new FlowioPropertyFixed("acceptTms", 614, 639));
		binder.addProperty(new FlowioPropertyFixed("startCreateTms", 639, 664));
		binder.addProperty(new FlowioPropertyFixed("endCreateTms", 664, 689));
		binder.addProperty(new FlowioPropertyFixed("fillerTwo", 689, 739));
		binder.addProperty(new FlowioPropertyFixed("localBaData", 739, 819));
		binder.addProperty(new FlowioPropertyFixed("extraDataLen", 819, 823));
		binder.addProperty(new FlowioPropertyFixed("extraData", 823, 2871));
		binder.addProperty(new FlowioPropertyFixed("signExist", 2871, 2872));
		// binder.addProperty(new FlowioPropertyFixed("signatureData", 2872, 28));
		binder.addProperty(new FlowioPropertyFixed("sndCertificateLabel", 2872, 2904));
		binder.addProperty(new FlowioPropertyFixed("sndSignAlgo", 2904, 2912));
		binder.addProperty(new FlowioPropertyFixed("sndSignLen", 2912, 2915));
		binder.addProperty(new FlowioPropertyFixed("sndSignTms", 2915, 2940));
		binder.addProperty(new FlowioPropertyFixed("sndSignFemsId", 2940, 2952));
		binder.addProperty(new FlowioPropertyFixed("sndSignServerId", 2952, 2964));
		binder.addProperty(new FlowioPropertyFixed("sndSignCertSubject", 2964, 3220));
		binder.addProperty(new FlowioPropertyFixed("sndSignResult", 3220, 3221));
		binder.addProperty(new FlowioPropertyFixed("checkSndSignResult", 3221, 3222));
		binder.addProperty(new FlowioPropertyFixed("sndSign", 3222, 3478));
		binder.addProperty(new FlowioPropertyFixed("fillerThree", 3478, 4084));
		binder.addProperty(new FlowioPropertyFixed("fileApplDataDigestAlg", 4084, 4092));
		binder.addProperty(new FlowioPropertyFixed("fileApplDataDigestLen", 4092, 4095));
		binder.addProperty(new FlowioPropertyFixed("fileApplDataDigest", 4095, 4223));
		binder.addProperty(new FlowioPropertyFixed("rcvBaFileDigestAlg", 4223, 4231));
		binder.addProperty(new FlowioPropertyFixed("rcvBaFileDigestLen", 4231, 4234));
		binder.addProperty(new FlowioPropertyFixed("rcvBaFileDigest", 4234, 4362));
		binder.addProperty(new FlowioPropertyFixed("netFileDigestAlg", 4362, 4370));
		binder.addProperty(new FlowioPropertyFixed("netFileDigestLen", 4370, 4373));
		binder.addProperty(new FlowioPropertyFixed("netFileDigest", 4373, 4501));
		binder.addProperty(new FlowioPropertyFixed("fillerFour", 4501, 5501));
		binder.addProperty(new FlowioPropertyFixed("localAuthInfoAlg", 5501, 5509));
		binder.addProperty(new FlowioPropertyFixed("localAuthInfoLen", 5509, 5512));
		binder.addProperty(new FlowioPropertyFixed("localAuthInfo", 5512, 5640));
		return binder;
	}

	public static FlowioFixedPositionBinder<MQ1413SecPosCreateFileind> binder1413() {
		FlowioFixedPositionBinder<MQ1413SecPosCreateFileind> binder = new FlowioFixedPositionBinder<>(MQ1413SecPosCreateFileind.class);

		binder.addProperty(new FlowioPropertyFixed("id", 0, 4));
		binder.addProperty(new FlowioPropertyFixed(BA_LOC, 4, 16));
		binder.addProperty(new FlowioPropertyFixed(BA_REM, 16, 28));
		binder.addProperty(new FlowioPropertyFixed(SYNC_FLAG, 28, 29));
		binder.addProperty(new FlowioPropertyFixed("vfn", 29, 61));
		binder.addProperty(new FlowioPropertyFixed("fillerOne", 61, 85));
		binder.addProperty(new FlowioPropertyFixed("acceptTms", 85, 110));
		binder.addProperty(new FlowioPropertyFixed(CORR_ID, 110, 140));
		binder.addProperty(new FlowioPropertyFixed("fillerTwo", 140, 160));
		binder.addProperty(new FlowioPropertyFixed("startCreateTms", 160, 185));
		binder.addProperty(new FlowioPropertyFixed("endCreateTms", 185, 210));
		binder.addProperty(new FlowioPropertyFixed(QUEUE_FILE_NAME, 210, 258));
		binder.addProperty(new FlowioPropertyFixed("groupId", 258, 282));
		binder.addProperty(new FlowioPropertyFixed("lineSeparator", 282, 283));
		binder.addProperty(new FlowioPropertyFixed("recType", 283, 284));
		binder.addProperty(new FlowioPropertyFixed("maxRecLen", 284, 290));
		binder.addProperty(new FlowioPropertyFixed("fillerThree", 290, 300));

		binder.addProperty(new FlowioPropertyFixed(BA_FILE_SIZE, 300, 310));
		binder.addProperty(new FlowioPropertyFixed("charType", 310, 311));
		binder.addProperty(new FlowioPropertyFixed("compressAlgo", 311, 319));
		binder.addProperty(new FlowioPropertyFixed("netFileSize", 319, 329));
		binder.addProperty(new FlowioPropertyFixed("localBaData", 329, 409));
		binder.addProperty(new FlowioPropertyFixed("fillerFour", 409, 916));

		binder.addProperty(new FlowioPropertyFixed("localAuthInfoAlg", 916, 924));
		binder.addProperty(new FlowioPropertyFixed("localAuthInfoLen", 924, 927));
		binder.addProperty(new FlowioPropertyFixed("localAuthInfo", 927, 1055));
		//return new FlowioOutputMapper<>(binder);
		return binder;
	}

	public static FlowioFixedPositionBinder<MQ1411SecReadFilecnf> binder1411() {
		FlowioFixedPositionBinder<MQ1411SecReadFilecnf> binder = new FlowioFixedPositionBinder<>(MQ1411SecReadFilecnf.class);

		binder.addProperty(new FlowioPropertyFixed("id", 0, 4));
		binder.addProperty(new FlowioPropertyFixed("reqBaLoc", 4, 16));
		binder.addProperty(new FlowioPropertyFixed("reqBaRem", 16, 28));
		binder.addProperty(new FlowioPropertyFixed("reqReadType", 28, 29));
		binder.addProperty(new FlowioPropertyFixed("reqSyncFlag", 29, 30));
		binder.addProperty(new FlowioPropertyFixed("reqCorrId", 30, 60));
		binder.addProperty(new FlowioPropertyFixed("reqVfn", 60, 92));
		binder.addProperty(new FlowioPropertyFixed("reqQueueFileName", 92, 140));
		binder.addProperty(new FlowioPropertyFixed("reqLineSeparatorRcv", 140, 141));
		binder.addProperty(new FlowioPropertyFixed("reqRcvCharType", 141, 142));
		binder.addProperty(new FlowioPropertyFixed("reqLocalBaData", 142, 222));
		binder.addProperty(new FlowioPropertyFixed("reqRcvBaFileDigestAlg", 222, 230));
		binder.addProperty(new FlowioPropertyFixed("reqRcvBaFileDigestLen", 230, 233));
		binder.addProperty(new FlowioPropertyFixed("reqFiller", 233, 433));
		binder.addProperty(new FlowioPropertyFixed("reqLocalAuthInfoAlg", 433, 441));
		binder.addProperty(new FlowioPropertyFixed("reqLocalAuthInfoLen", 441, 444));
		binder.addProperty(new FlowioPropertyFixed("reqLocalAuthInfo", 444, 572));
		binder.addProperty(new FlowioPropertyFixed("acceptTms", 572, 597));
		binder.addProperty(new FlowioPropertyFixed("result", 597, 600));
		binder.addProperty(new FlowioPropertyFixed("rejReason", 600, 603));
		binder.addProperty(new FlowioPropertyFixed("filler", 603, 803));
		binder.addProperty(new FlowioPropertyFixed("localAuthInfoAlg", 803, 811));
		binder.addProperty(new FlowioPropertyFixed("localAuthInfoLen", 811, 814));
		binder.addProperty(new FlowioPropertyFixed("localAuthInfo", 814, 942));
		return binder;
	}

	public static FlowioFixedPositionBinder<MQ1405SecReceiveind> binder1405(Integer mabLength) {
		FlowioFixedPositionBinder<MQ1405SecReceiveind> binder = new FlowioFixedPositionBinder<>(MQ1405SecReceiveind.class);

		binder.addProperty(new FlowioPropertyFixed("id", 0, 4));
		binder.addProperty(new FlowioPropertyFixed(BA_LOC, 4, 16));
		binder.addProperty(new FlowioPropertyFixed(BA_REM, 16, 28));
		binder.addProperty(new FlowioPropertyFixed(CORR_ID, 28, 58));

		binder.addProperty(new FlowioPropertyFixed("vfn", 58, 90));
		binder.addProperty(new FlowioPropertyFixed("adfLen", 90, 92));
		binder.addProperty(new FlowioPropertyFixed("adf", 92, 172));
		binder.addProperty(new FlowioPropertyFixed(QUEUE_FILE_NAME, 172, 220));
		binder.addProperty(new FlowioPropertyFixed("groupId", 220, 244));
		binder.addProperty(new FlowioPropertyFixed("lineSeparatorRcv", 244, 245));
		binder.addProperty(new FlowioPropertyFixed("recType", 245, 246));
		binder.addProperty(new FlowioPropertyFixed("maxRecLen", 246, 252));
		binder.addProperty(new FlowioPropertyFixed("fillerOne", 252, 262));
		binder.addProperty(new FlowioPropertyFixed(BA_FILE_SIZE, 262, 272));
		binder.addProperty(new FlowioPropertyFixed("sndCharType", 272, 273));
		binder.addProperty(new FlowioPropertyFixed("rcvCharType", 273, 274));
		binder.addProperty(new FlowioPropertyFixed("compressAlgo", 274, 282));
		binder.addProperty(new FlowioPropertyFixed("netFileSize", 282, 292));
		binder.addProperty(new FlowioPropertyFixed("udrLen", 292, 294));
		binder.addProperty(new FlowioPropertyFixed("udr", 294, 374));
		binder.addProperty(new FlowioPropertyFixed("tur", 374, 390));
		binder.addProperty(new FlowioPropertyFixed("msgType", 390, 393));
		binder.addProperty(new FlowioPropertyFixed("catAppl", 393, 397));
		binder.addProperty(new FlowioPropertyFixed("transferId", 397, 413));
		binder.addProperty(new FlowioPropertyFixed("hostFirstSubTms", 413, 438));
		binder.addProperty(new FlowioPropertyFixed("ferFirstBSubTms", 438, 463));
		binder.addProperty(new FlowioPropertyFixed("fenFirstBSubTms", 463, 488));
		binder.addProperty(new FlowioPropertyFixed("fenFirstBDlvTms", 488, 513));
		binder.addProperty(new FlowioPropertyFixed("ferFirstBDlvTms", 513, 538));
		binder.addProperty(new FlowioPropertyFixed("ferLastBSubTms", 538, 563));
		binder.addProperty(new FlowioPropertyFixed("fenLastBSubTms", 563, 588));
		binder.addProperty(new FlowioPropertyFixed("fenLastBDlvTms", 588, 613));
		binder.addProperty(new FlowioPropertyFixed("ferLastBDlvTms", 613, 638));
		binder.addProperty(new FlowioPropertyFixed("hostFirstDlvTms", 638, 663));
		binder.addProperty(new FlowioPropertyFixed("ftsEndReceiveTms", 663, 688));
		binder.addProperty(new FlowioPropertyFixed("fermsSubTms", 688, 713));
		binder.addProperty(new FlowioPropertyFixed("fenmsSubTms", 713, 738));
		binder.addProperty(new FlowioPropertyFixed("fenmsDlvTms", 738, 763));
		binder.addProperty(new FlowioPropertyFixed("fermsDlvTms", 763, 788));
		binder.addProperty(new FlowioPropertyFixed("acceptTms", 788, 813));
		binder.addProperty(new FlowioPropertyFixed("startReadTms", 813, 838));
		binder.addProperty(new FlowioPropertyFixed("endReadTms", 838, 863));
		binder.addProperty(new FlowioPropertyFixed("fillerTwo", 863, 913));
		binder.addProperty(new FlowioPropertyFixed("localBaData", 913, 993));
		binder.addProperty(new FlowioPropertyFixed("extraDataLen", 993, 997));
		binder.addProperty(new FlowioPropertyFixed("extraData", 997, 3045));
		binder.addProperty(new FlowioPropertyFixed("signExist", 3045, 3046));
		// binder.addProperty(new FlowioPropertyFixed("signatureData", 3036, 3036));
		binder.addProperty(new FlowioPropertyFixed("sndCertificateLabel", 3046, 3078));
		binder.addProperty(new FlowioPropertyFixed("sndSignAlgo", 3078, 3086));
		binder.addProperty(new FlowioPropertyFixed("sndSignLen", 3086, 3089));
		binder.addProperty(new FlowioPropertyFixed("sndSignTms", 3089, 3114));
		binder.addProperty(new FlowioPropertyFixed("sndSignFemsId", 3114, 3126));
		binder.addProperty(new FlowioPropertyFixed("sndSignServerId", 3126, 3138));
		binder.addProperty(new FlowioPropertyFixed("sndSignCertSubject", 3138, 3394));
		binder.addProperty(new FlowioPropertyFixed("sndSignResult", 3394, 3395));
		binder.addProperty(new FlowioPropertyFixed("checkSndSignResult", 3395, 3396));
		binder.addProperty(new FlowioPropertyFixed("sndSign", 3396, 3652));
		binder.addProperty(new FlowioPropertyFixed("fillerThree", 3652, 4258));
		binder.addProperty(new FlowioPropertyFixed("fileApplDataDigestAlg", 4258, 4266));
		binder.addProperty(new FlowioPropertyFixed("fileApplDataDigestLen", 4266, 4269));
		binder.addProperty(new FlowioPropertyFixed("fileApplDataDigest", 4269, 4397));
		binder.addProperty(new FlowioPropertyFixed("rcvBaFileDigestAlg", 4397, 4405));
		binder.addProperty(new FlowioPropertyFixed("rcvBaFileDigestLen", 4405, 4408));
		binder.addProperty(new FlowioPropertyFixed("rcvBaFileDigest", 4408, 4536));
		binder.addProperty(new FlowioPropertyFixed("netFileDigestAlg", 4536, 4544));
		binder.addProperty(new FlowioPropertyFixed("netFileDigestLen", 4544, 4547));
		binder.addProperty(new FlowioPropertyFixed("netFileDigest", 4547, 4675));
		binder.addProperty(new FlowioPropertyFixed("mabDigestAlg", 4675, 4683));
		binder.addProperty(new FlowioPropertyFixed("mabDigestLen", 4683, 4686));
		binder.addProperty(new FlowioPropertyFixed("mabDigest", 4686, 4814));
		binder.addProperty(new FlowioPropertyFixed("fillerFour", 4814, 5814));
		binder.addProperty(new FlowioPropertyFixed("mabLen", 5814, 5824));
		//TO MERGE IN V20	
		if(mabLength != null) {
			int startColumn = 5824;
			int mab = startColumn + mabLength;
			int locInfoAlg = mab + 8;
			int locInfoLen = locInfoAlg + 3;
			int locInfo = locInfoLen + 128;
			binder.addProperty(new FlowioPropertyFixed("mab", 5824, mab));
			binder.addProperty(new FlowioPropertyFixed("localAuthInfoAlg", mab, locInfoAlg));
			binder.addProperty(new FlowioPropertyFixed("localAuthInfoLen", locInfoAlg, locInfoLen));
			binder.addProperty(new FlowioPropertyFixed("localAuthInfo", locInfoLen, locInfo));
		}
		
	
//		binder.addProperty(new FlowioPropertyFixed("localAuthInfoAlg", 6324, 6332));
//		binder.addProperty(new FlowioPropertyFixed("localAuthInfoLen", 6332, 6335));
//		binder.addProperty(new FlowioPropertyFixed("localAuthInfo", 6335, 6463));
		return binder;
	}

	public static FlowioFixedPositionBinder<MQ1410SecReadFilereq> binder1410() {
		FlowioFixedPositionBinder<MQ1410SecReadFilereq> binder = new FlowioFixedPositionBinder<>(MQ1410SecReadFilereq.class);

		binder.addProperty(new FlowioPropertyFixed("id", 0, 4));
		binder.addProperty(new FlowioPropertyFixed(BA_LOC, 4, 16));
		binder.addProperty(new FlowioPropertyFixed(BA_REM, 16, 28));
		binder.addProperty(new FlowioPropertyFixed("readType", 28, 29));
		binder.addProperty(new FlowioPropertyFixed(SYNC_FLAG, 29, 30));
		binder.addProperty(new FlowioPropertyFixed(CORR_ID, 30, 60));
		binder.addProperty(new FlowioPropertyFixed("vfn", 60, 92));
		binder.addProperty(new FlowioPropertyFixed(QUEUE_FILE_NAME, 92, 140));
		binder.addProperty(new FlowioPropertyFixed("lineSeparator", 140, 141));
		binder.addProperty(new FlowioPropertyFixed("rcvCharType", 141, 142));
		binder.addProperty(new FlowioPropertyFixed("localBaData", 142, 222));
		binder.addProperty(new FlowioPropertyFixed("rcvBaFileDigestAlg", 222, 230));
		binder.addProperty(new FlowioPropertyFixed("rcvBaFileDigestLen", 230, 233));
		binder.addProperty(new FlowioPropertyFixed("filler", 233, 433));
		binder.addProperty(new FlowioPropertyFixed("localAuthInfoAlg", 433, 441));
		binder.addProperty(new FlowioPropertyFixed("localAuthInfoLen", 441, 444));
		binder.addProperty(new FlowioPropertyFixed("localAuthInfo", 444, 572));
		return binder;
	}

	public static FlowioFixedPositionBinder<MQ1400SecSendFilereq> binder1400(int mabLength) {

		FlowioFixedPositionBinder<MQ1400SecSendFilereq> binder = new FlowioFixedPositionBinder<>(MQ1400SecSendFilereq.class);
		binder.addProperty(new FlowioPropertyFixed("id", 0, 4));
		binder.addProperty(new FlowioPropertyFixed(BA_LOC, 4, 16));
		binder.addProperty(new FlowioPropertyFixed(BA_REM, 16, 28));
		binder.addProperty(new FlowioPropertyFixed("sendType", 28, 29));
		binder.addProperty(new FlowioPropertyFixed(SYNC_FLAG, 29, 30));
		binder.addProperty(new FlowioPropertyFixed(CORR_ID, 30, 60));
		binder.addProperty(new FlowioPropertyFixed("vfn", 60, 92));
		binder.addProperty(new FlowioPropertyFixed(BA_FILE_SIZE, 92, 102));
		binder.addProperty(new FlowioPropertyFixed(QUEUE_FILE_NAME, 102, 150));
		binder.addProperty(new FlowioPropertyFixed("groupId", 150, 174));
		binder.addProperty(new FlowioPropertyFixed("lineSeparator", 174, 175));
		binder.addProperty(new FlowioPropertyFixed("recType", 175, 176));
		binder.addProperty(new FlowioPropertyFixed("maxRecLen", 176, 182));
		binder.addProperty(new FlowioPropertyFixed("charType", 182, 183));
		binder.addProperty(new FlowioPropertyFixed("compressAlgo", 183, 191));
		binder.addProperty(new FlowioPropertyFixed("adfLen", 191, 193));
		binder.addProperty(new FlowioPropertyFixed("adf", 193, 273));
		binder.addProperty(new FlowioPropertyFixed("udrLen", 273, 275));
		binder.addProperty(new FlowioPropertyFixed("udr", 275, 355));
		binder.addProperty(new FlowioPropertyFixed("tur", 355, 371));
		binder.addProperty(new FlowioPropertyFixed("msgType", 371, 374));
		binder.addProperty(new FlowioPropertyFixed("catAppl", 374, 378));
		binder.addProperty(new FlowioPropertyFixed("localBaData", 378, 458));
		binder.addProperty(new FlowioPropertyFixed("extraDataLen", 458, 462));
		binder.addProperty(new FlowioPropertyFixed("extraData", 462, 2510));
		binder.addProperty(new FlowioPropertyFixed("sndBaFileDigestAlg", 2510, 2518));
		binder.addProperty(new FlowioPropertyFixed("sndBaFileDigestLen", 2518, 2521));
		binder.addProperty(new FlowioPropertyFixed("sndBaFileDigest", 2521, 2649));
		binder.addProperty(new FlowioPropertyFixed("mabDigestAlg", 2649, 2657));
		binder.addProperty(new FlowioPropertyFixed("mabDigestLen", 2657, 2660));
		binder.addProperty(new FlowioPropertyFixed("mabDigest", 2660, 2788));
		binder.addProperty(new FlowioPropertyFixed("filler", 2788, 3788));
		binder.addProperty(new FlowioPropertyFixed("mabLen", 3788, 3798));
		
		int startColumn = 3798;
		int mab = startColumn + mabLength;
		int locInfoAlg = mab + 8;
		int locInfoLen = locInfoAlg + 3;
		int locInfo = locInfoLen + 128;
		if(mab != 3798)
			binder.addProperty(new FlowioPropertyFixed("mab", 3798, mab));
		
		binder.addProperty(new FlowioPropertyFixed("localAuthInfoAlg", mab, locInfoAlg));
		binder.addProperty(new FlowioPropertyFixed("localAuthInfoLen", locInfoAlg, locInfoLen));
		binder.addProperty(new FlowioPropertyFixed("localAuthInfo", locInfoLen, locInfo));
		
//		else {
//			
//			binder.addProperty(new FlowioPropertyFixed("localAuthInfoAlg", 3798, 3806));
//			binder.addProperty(new FlowioPropertyFixed("localAuthInfoLen", 3806, 3809));
//			binder.addProperty(new FlowioPropertyFixed("localAuthInfo", 3809, 3937));
//		}
		return binder;
	}

	public static FlowioFixedPositionBinder<MQ1406SecReleasereq> binder1406() {
		FlowioFixedPositionBinder<MQ1406SecReleasereq> binder = new FlowioFixedPositionBinder<>(MQ1406SecReleasereq.class);

		binder.addProperty(new FlowioPropertyFixed("id", 0, 4));
		binder.addProperty(new FlowioPropertyFixed(BA_LOC, 4, 16));
		binder.addProperty(new FlowioPropertyFixed(BA_REM, 16, 28));
		binder.addProperty(new FlowioPropertyFixed(SYNC_FLAG, 28, 29));
		binder.addProperty(new FlowioPropertyFixed("udrLen", 29, 31));
		binder.addProperty(new FlowioPropertyFixed("udr", 31, 111));
		binder.addProperty(new FlowioPropertyFixed("vfn", 111, 143));
		binder.addProperty(new FlowioPropertyFixed("baProcessTms", 143, 155));
		binder.addProperty(new FlowioPropertyFixed("filler", 155, 255));
		binder.addProperty(new FlowioPropertyFixed("localAuthInfoAlg", 255, 263));
		binder.addProperty(new FlowioPropertyFixed("localAuthInfoLen", 263, 266));
		binder.addProperty(new FlowioPropertyFixed("localAuthInfo", 266, 394));
		return binder;
	}

	public static FlowioFixedPositionBinder<MQ1407SecReleasecnf> binder1407() {
		FlowioFixedPositionBinder<MQ1407SecReleasecnf> binder = new FlowioFixedPositionBinder<>(MQ1407SecReleasecnf.class);

		binder.addProperty(new FlowioPropertyFixed("id", 0, 4));
		binder.addProperty(new FlowioPropertyFixed("reqBaLoc", 4, 16));
		binder.addProperty(new FlowioPropertyFixed("reqBaRem", 16, 28));
		binder.addProperty(new FlowioPropertyFixed("reqSyncFlag", 28, 29));
		binder.addProperty(new FlowioPropertyFixed("reqUdrLen", 29, 31));
		binder.addProperty(new FlowioPropertyFixed("reqUdr", 31, 111));
		binder.addProperty(new FlowioPropertyFixed("reqVfn", 111, 143));
		binder.addProperty(new FlowioPropertyFixed("reqBaProcessTms", 143, 155));
		binder.addProperty(new FlowioPropertyFixed("reqFiller", 155, 255));
		binder.addProperty(new FlowioPropertyFixed("reqLocalAuthInfoAlg", 255, 263));
		binder.addProperty(new FlowioPropertyFixed("reqLocalAuthInfoLen", 263, 266));
		binder.addProperty(new FlowioPropertyFixed("reqLocalAuthInfo", 266, 394));
		binder.addProperty(new FlowioPropertyFixed("result", 394, 397));
		binder.addProperty(new FlowioPropertyFixed("rejReason", 397, 400));
		binder.addProperty(new FlowioPropertyFixed("siStdProcessTms", 400, 425));
		binder.addProperty(new FlowioPropertyFixed("filler", 425, 525));
		binder.addProperty(new FlowioPropertyFixed("localAuthInfoAlg", 525, 533));
		binder.addProperty(new FlowioPropertyFixed("localAuthInfoLen", 533, 536));
		binder.addProperty(new FlowioPropertyFixed("localAuthInfo", 536, 664));
		return binder;
	}

	public static FlowioFixedPositionBinder<MQ1419SecNotAckFilereq> binder1419() {
		FlowioFixedPositionBinder<MQ1419SecNotAckFilereq> binder = new FlowioFixedPositionBinder<>(MQ1419SecNotAckFilereq.class);

		binder.addProperty(new FlowioPropertyFixed("id", 0, 4)); 
		binder.addProperty(new FlowioPropertyFixed(BA_LOC, 4, 16));
		binder.addProperty(new FlowioPropertyFixed(BA_REM, 16, 28)); 
		binder.addProperty(new FlowioPropertyFixed(SYNC_FLAG, 28, 29)); 
		binder.addProperty(new FlowioPropertyFixed("udrLen", 29, 31));
		binder.addProperty(new FlowioPropertyFixed("udr", 31, 111)); 
		binder.addProperty(new FlowioPropertyFixed("vfn", 111, 143)); 
		binder.addProperty(new FlowioPropertyFixed("baProcessTms", 143, 155)); 
		binder.addProperty(new FlowioPropertyFixed("filler", 155, 255)); 
		binder.addProperty(new FlowioPropertyFixed("localAuthInfoAlg", 255, 263));
		binder.addProperty(new FlowioPropertyFixed("localAuthInfoLen", 263, 266));
		binder.addProperty(new FlowioPropertyFixed("localAuthInfo", 266, 394)); 
		
		return binder;
	}
	
	public static FlowioFixedPositionBinder<MQ1420SecNotAckFilecnf> binder1420() {
		FlowioFixedPositionBinder<MQ1420SecNotAckFilecnf> binder = new FlowioFixedPositionBinder<>(MQ1420SecNotAckFilecnf.class);

		binder.addProperty(new FlowioPropertyFixed("id", 0, 4));
		binder.addProperty(new FlowioPropertyFixed("reqBaLoc", 4, 16));
		binder.addProperty(new FlowioPropertyFixed("reqBaRem", 16, 28));
		binder.addProperty(new FlowioPropertyFixed("reqSyncFlag", 28, 29));
		binder.addProperty(new FlowioPropertyFixed("reqUdrLen", 29, 31));
		binder.addProperty(new FlowioPropertyFixed("reqUdr", 31, 111));
		binder.addProperty(new FlowioPropertyFixed("reqVfn", 111, 143));
		binder.addProperty(new FlowioPropertyFixed("reqBaProcessTms", 143, 155));
		binder.addProperty(new FlowioPropertyFixed("reqFiller", 155, 255));
		binder.addProperty(new FlowioPropertyFixed("reqLocalAuthInfoAlg", 255, 263));
		binder.addProperty(new FlowioPropertyFixed("reqLocalAuthInfoLen", 263, 266));
		binder.addProperty(new FlowioPropertyFixed("reqLocalAuthInfo", 266, 394));
		binder.addProperty(new FlowioPropertyFixed("result", 394, 397));
		binder.addProperty(new FlowioPropertyFixed("rejReason", 397, 400));
		binder.addProperty(new FlowioPropertyFixed("siStdProcessTms", 400, 425));
		binder.addProperty(new FlowioPropertyFixed("filler", 425, 525));

		binder.addProperty(new FlowioPropertyFixed("localAuthInfoAlg", 525, 533));
		binder.addProperty(new FlowioPropertyFixed("localAuthInfoLen", 533, 536));
		binder.addProperty(new FlowioPropertyFixed("localAuthInfo", 536, 664));
		
		return binder;
	}
	
	public static FlowioFixedPositionBinder<MQ1911SecSendMsgreq> binder1911(Integer mabLength) {

		FlowioFixedPositionBinder<MQ1911SecSendMsgreq> binder = new FlowioFixedPositionBinder<>(MQ1911SecSendMsgreq.class);
		binder.addProperty(new FlowioPropertyFixed("id", 0, 4));
		binder.addProperty(new FlowioPropertyFixed("filler1", 4, 52));
		binder.addProperty(new FlowioPropertyFixed("locBaData", 52, 102));
		binder.addProperty(new FlowioPropertyFixed(BA_LOC, 102, 114));
		binder.addProperty(new FlowioPropertyFixed(BA_REM, 114, 126));
		binder.addProperty(new FlowioPropertyFixed("priority", 126, 127));
		binder.addProperty(new FlowioPropertyFixed("tur", 127, 143));
		binder.addProperty(new FlowioPropertyFixed("baReqTms", 143, 155));
		binder.addProperty(new FlowioPropertyFixed("msgType", 155, 158));
		binder.addProperty(new FlowioPropertyFixed("catAppl", 158, 162));
		binder.addProperty(new FlowioPropertyFixed(CORR_ID, 162, 192));
		binder.addProperty(new FlowioPropertyFixed("udrLen", 192, 194));
		binder.addProperty(new FlowioPropertyFixed("udr", 194, 274));
		binder.addProperty(new FlowioPropertyFixed("extraDataLen", 274, 278));
		binder.addProperty(new FlowioPropertyFixed("extraData", 278, 2326));
		binder.addProperty(new FlowioPropertyFixed("mabDigestAlg", 2326, 2334));
		binder.addProperty(new FlowioPropertyFixed("mabDigestLen", 2334, 2337));
		binder.addProperty(new FlowioPropertyFixed("mabDigest", 2337, 2465));
		binder.addProperty(new FlowioPropertyFixed("filler2", 2465, 3465));
		binder.addProperty(new FlowioPropertyFixed("mabLen", 3465, 3475));
		binder.addProperty(new FlowioPropertyFixed("localAuthInfoAlg", 3475, 3483));
		binder.addProperty(new FlowioPropertyFixed("localAuthInfoLen", 3483, 3486));
		
		if(mabLength != null) {
			int startColumn = 3486;
			int mab = startColumn + mabLength;
			int lau = mab + 128;
			
			binder.addProperty(new FlowioPropertyFixed("mab", startColumn, mab));
			binder.addProperty(new FlowioPropertyFixed("localAuthInfo", mab, lau));
		} 
		
//		binder.addProperty(new FlowioPropertyFixed("mab", 3486, 4510));
//		binder.addProperty(new FlowioPropertyFixed("localAuthInfo", 4510, 4638));
		return binder;
	}
	
	public static FlowioFixedPositionBinder<MQ1933SecReleaseMsgreq> binder1933() {
		FlowioFixedPositionBinder<MQ1933SecReleaseMsgreq> binder = new FlowioFixedPositionBinder<>(MQ1933SecReleaseMsgreq.class);

		binder.addProperty(new FlowioPropertyFixed("id", 0, 4));
		binder.addProperty(new FlowioPropertyFixed(BA_LOC, 4, 16));
		binder.addProperty(new FlowioPropertyFixed(BA_REM, 16, 28));
		binder.addProperty(new FlowioPropertyFixed("priority", 28, 29));
		binder.addProperty(new FlowioPropertyFixed("msgIdR", 29, 45));
		binder.addProperty(new FlowioPropertyFixed("udrLen", 45, 47));
		binder.addProperty(new FlowioPropertyFixed("udr", 47, 127));
		binder.addProperty(new FlowioPropertyFixed("baProcessTms", 127, 139));
		binder.addProperty(new FlowioPropertyFixed("filler", 139, 239));
		binder.addProperty(new FlowioPropertyFixed("localAuthInfoAlg", 239, 247));
		binder.addProperty(new FlowioPropertyFixed("localAuthInfoLen", 247, 250));
		binder.addProperty(new FlowioPropertyFixed("localAuthInfo", 250, 378));
		return binder;
	}

	public static FlowioFixedPositionBinder<MQ1934SecReleaseMsgcnf> binder1934() {
		FlowioFixedPositionBinder<MQ1934SecReleaseMsgcnf> binder = new FlowioFixedPositionBinder<>(MQ1934SecReleaseMsgcnf.class);

		binder.addProperty(new FlowioPropertyFixed("id", 0, 4));
		binder.addProperty(new FlowioPropertyFixed("reqBaLoc", 4, 16)); 
		binder.addProperty(new FlowioPropertyFixed("reqBaRem", 16, 28)); 
		binder.addProperty(new FlowioPropertyFixed("reqPriority", 28, 29));
		binder.addProperty(new FlowioPropertyFixed("reqMsgIdR", 29, 45));
		binder.addProperty(new FlowioPropertyFixed("reqUdrLen", 45, 47));
		binder.addProperty(new FlowioPropertyFixed("reqUdr", 47, 127));                                                            
		binder.addProperty(new FlowioPropertyFixed("reqBaProcessTms", 127, 139));  
		binder.addProperty(new FlowioPropertyFixed("fillerOne", 139, 239));  
		binder.addProperty(new FlowioPropertyFixed("reqLocalAuthInfoAlg", 239, 247));
		binder.addProperty(new FlowioPropertyFixed("reqLocalAuthInfoLen", 247, 250));
		binder.addProperty(new FlowioPropertyFixed("reqLocalAuthInfo", 250, 378));
		binder.addProperty(new FlowioPropertyFixed("result", 378, 379));
		binder.addProperty(new FlowioPropertyFixed("rejReason", 379, 382));
		binder.addProperty(new FlowioPropertyFixed("siStdProcessTms", 382, 407));
		binder.addProperty(new FlowioPropertyFixed("fillerTwo", 407, 507));
		binder.addProperty(new FlowioPropertyFixed("localAuthInfoAlg", 507, 515));
		binder.addProperty(new FlowioPropertyFixed("localAuthInfoLen", 515, 518));
		binder.addProperty(new FlowioPropertyFixed("localAuthInfo", 518, 646));
		return binder;
	}
	
	public static FlowioFixedPositionBinder<MQ1991SecNotAckMsgreq> binder1991() {
		FlowioFixedPositionBinder<MQ1991SecNotAckMsgreq> binder = new FlowioFixedPositionBinder<>(MQ1991SecNotAckMsgreq.class);

		binder.addProperty(new FlowioPropertyFixed("id", 0, 4));
		binder.addProperty(new FlowioPropertyFixed(BA_LOC, 4, 16));
		binder.addProperty(new FlowioPropertyFixed(BA_REM, 16, 28));
		binder.addProperty(new FlowioPropertyFixed("priority", 28, 29));
		binder.addProperty(new FlowioPropertyFixed("msgId", 29, 45));
		binder.addProperty(new FlowioPropertyFixed("udrLen", 45, 47));
		binder.addProperty(new FlowioPropertyFixed("udr", 47, 127));
		binder.addProperty(new FlowioPropertyFixed("baProcessTms", 127, 139));
		binder.addProperty(new FlowioPropertyFixed("filler", 139, 239));
		binder.addProperty(new FlowioPropertyFixed("localAuthInfoAlg", 239, 247));
		binder.addProperty(new FlowioPropertyFixed("localAuthInfoLen", 247, 250));
		binder.addProperty(new FlowioPropertyFixed("localAuthInfo", 250, 378));
		return binder;
	}
	
	public static FlowioFixedPositionBinder<MQ1992SecNotAckMsgcnf> binder1992() {
		FlowioFixedPositionBinder<MQ1992SecNotAckMsgcnf> binder = new FlowioFixedPositionBinder<>(MQ1992SecNotAckMsgcnf.class);

		binder.addProperty(new FlowioPropertyFixed("id", 0, 4));
		binder.addProperty(new FlowioPropertyFixed("reqBaLoc", 4, 16)); 
		binder.addProperty(new FlowioPropertyFixed("reqBaRem", 16, 28)); 
		binder.addProperty(new FlowioPropertyFixed("reqPriority", 28, 29));
		binder.addProperty(new FlowioPropertyFixed("reqMsgId", 29, 45));
		binder.addProperty(new FlowioPropertyFixed("reqUdrLen", 45, 47));
		binder.addProperty(new FlowioPropertyFixed("reqUdr", 47, 127));                                                            
		binder.addProperty(new FlowioPropertyFixed("reqBaProcessTms", 127, 139));  
		binder.addProperty(new FlowioPropertyFixed("reqFiller", 139, 239));  
		binder.addProperty(new FlowioPropertyFixed("reqLocalAuthInfoAlg", 239, 247));
		binder.addProperty(new FlowioPropertyFixed("reqLocalAuthInfoLen", 247, 250));
		binder.addProperty(new FlowioPropertyFixed("reqLocalAuthInfo", 250, 378));
		binder.addProperty(new FlowioPropertyFixed("result", 378, 379));
		binder.addProperty(new FlowioPropertyFixed("rejReason", 379, 382));
		binder.addProperty(new FlowioPropertyFixed("siStdProcessTms", 382, 407));
		binder.addProperty(new FlowioPropertyFixed("filler", 407, 507));
		binder.addProperty(new FlowioPropertyFixed("localAuthInfoAlg", 507, 515));
		binder.addProperty(new FlowioPropertyFixed("localAuthInfoLen", 515, 518));
		binder.addProperty(new FlowioPropertyFixed("localAuthInfo", 518, 646));
		return binder;
	}
	
	public static FlowioFixedPositionBinder<MQ1951SecReceiveMsgind> binder1951(Integer mabLength) {
		FlowioFixedPositionBinder<MQ1951SecReceiveMsgind> binder = new FlowioFixedPositionBinder<>(MQ1951SecReceiveMsgind.class);

		binder.addProperty(new FlowioPropertyFixed("id", 0, 4));
		binder.addProperty(new FlowioPropertyFixed("baLoc", 4, 16)); 
		binder.addProperty(new FlowioPropertyFixed("baRem", 16, 28)); 
		binder.addProperty(new FlowioPropertyFixed("priority", 28, 29));
		binder.addProperty(new FlowioPropertyFixed("msgId", 29, 45));
		binder.addProperty(new FlowioPropertyFixed("msgIdR", 45, 61));
		binder.addProperty(new FlowioPropertyFixed("udrLen", 61, 63));
		binder.addProperty(new FlowioPropertyFixed("udr", 63, 143));                                                            
		binder.addProperty(new FlowioPropertyFixed("tur", 143, 159));  
		binder.addProperty(new FlowioPropertyFixed("msgType", 159, 162));
		binder.addProperty(new FlowioPropertyFixed("catAppl", 162, 166));
		binder.addProperty(new FlowioPropertyFixed("aggrI", 166, 178));
		binder.addProperty(new FlowioPropertyFixed("aggrII", 178, 190));
		binder.addProperty(new FlowioPropertyFixed("hostFirstSubTms", 190, 215));
		binder.addProperty(new FlowioPropertyFixed("ferSubTms", 215, 240));
		binder.addProperty(new FlowioPropertyFixed("fenSubTms", 240, 265));
		binder.addProperty(new FlowioPropertyFixed("fenDlvTms", 265, 290));
		binder.addProperty(new FlowioPropertyFixed("ferDlvTms", 290, 315));
		binder.addProperty(new FlowioPropertyFixed("hostFirstDelTms", 315, 340));
		binder.addProperty(new FlowioPropertyFixed("firstBADlvTms", 340, 365));
		binder.addProperty(new FlowioPropertyFixed("extraDataLen", 365, 369));
		binder.addProperty(new FlowioPropertyFixed("extraData", 369, 2417));
		binder.addProperty(new FlowioPropertyFixed("signExist", 2417, 2418));
	//	binder.addProperty(new FlowioPropertyFixed("signatureData", 4, 	54));
		binder.addProperty(new FlowioPropertyFixed("sndCertificateLabel", 2418, 2450));
		binder.addProperty(new FlowioPropertyFixed("sndSignAlgo", 2450, 2458));
		binder.addProperty(new FlowioPropertyFixed("sndSignLen", 2458, 2461));
		binder.addProperty(new FlowioPropertyFixed("sndSignTms", 2461, 2486));
		binder.addProperty(new FlowioPropertyFixed("sndSignFemsId", 2486, 2498));
		binder.addProperty(new FlowioPropertyFixed("sndSignServerId", 2498, 2510));
		binder.addProperty(new FlowioPropertyFixed("sndSignCertSubject", 2510, 2766));
		binder.addProperty(new FlowioPropertyFixed("sndSignResult", 2766, 2767));
		binder.addProperty(new FlowioPropertyFixed("checkSndSignResult", 2767, 2768));
		binder.addProperty(new FlowioPropertyFixed("sndSign", 2768, 3024));
		binder.addProperty(new FlowioPropertyFixed("fillerOne", 3024, 3630));
		binder.addProperty(new FlowioPropertyFixed("mabDigestAlg", 3630, 3638));
		binder.addProperty(new FlowioPropertyFixed("mabDigestLen", 3638, 3641));
		binder.addProperty(new FlowioPropertyFixed("mabDigest", 3641, 3769));
		binder.addProperty(new FlowioPropertyFixed("fillerTwo", 3769, 4769));
		binder.addProperty(new FlowioPropertyFixed("mabLen", 4769, 4779));
		binder.addProperty(new FlowioPropertyFixed("localAuthInfoAlg", 4779, 4787));
		binder.addProperty(new FlowioPropertyFixed("localAuthInfoLen", 4787, 4790));
		
		if(mabLength != null) {
			int mab = 4790 + mabLength;
			int lau = mab + 128;
			
			binder.addProperty(new FlowioPropertyFixed("mab", 4790, mab));
			binder.addProperty(new FlowioPropertyFixed("localAuthInfo", mab, lau));
		}
		return binder;
	}
	
	
	public static FlowioFixedPositionBinder<MQ1409SecReceiveFileInd> binder1409() {
		FlowioFixedPositionBinder<MQ1409SecReceiveFileInd> binder1409 = new FlowioFixedPositionBinder<>(MQ1409SecReceiveFileInd.class);

		binder1409.addProperty(new FlowioPropertyFixed("id", 0, 4));
		binder1409.addProperty(new FlowioPropertyFixed("baLoc", 4, 16));
		binder1409.addProperty(new FlowioPropertyFixed("baRem", 16, 28));
		binder1409.addProperty(new FlowioPropertyFixed("syncFlag", 28, 29));
		binder1409.addProperty(new FlowioPropertyFixed("vfn", 29, 61));
		binder1409.addProperty(new FlowioPropertyFixed("adfLen", 61, 63));
		binder1409.addProperty(new FlowioPropertyFixed("adf", 63, 143));
		binder1409.addProperty(new FlowioPropertyFixed("aggrI", 143, 155));
		binder1409.addProperty(new FlowioPropertyFixed("aggrII", 155, 167));
		binder1409.addProperty(new FlowioPropertyFixed("fillerOne", 167, 215));
		binder1409.addProperty(new FlowioPropertyFixed("recType", 215, 216));
		binder1409.addProperty(new FlowioPropertyFixed("maxRecLen", 216, 222));
		binder1409.addProperty(new FlowioPropertyFixed("fillerTwo", 222, 232));
		binder1409.addProperty(new FlowioPropertyFixed("charType", 232, 233));
		binder1409.addProperty(new FlowioPropertyFixed("compressAlgo", 233, 241));
		binder1409.addProperty(new FlowioPropertyFixed("netFileSize", 241, 251));
		binder1409.addProperty(new FlowioPropertyFixed("transferId", 251, 267));
		binder1409.addProperty(new FlowioPropertyFixed("hostFirstSubTms", 267, 292));
		binder1409.addProperty(new FlowioPropertyFixed("ferFirstBSubTms", 292, 317));
		binder1409.addProperty(new FlowioPropertyFixed("fenFirstBSubTms", 317, 342));
		binder1409.addProperty(new FlowioPropertyFixed("fenFirstBDlvTms", 342, 367));
		binder1409.addProperty(new FlowioPropertyFixed("ferFirstBDlvTms", 367, 392));
		binder1409.addProperty(new FlowioPropertyFixed("ferLastBSubTms", 392, 417));
		binder1409.addProperty(new FlowioPropertyFixed("fenLastBSubTms", 417, 442));
		binder1409.addProperty(new FlowioPropertyFixed("fenLastBDlvTms", 442, 467));
		binder1409.addProperty(new FlowioPropertyFixed("ferLastBDeliveryTms", 467, 492));
		binder1409.addProperty(new FlowioPropertyFixed("hostFirstDlvTms", 492, 517));
		binder1409.addProperty(new FlowioPropertyFixed("ftsEndReceiveTms", 517, 542));
		binder1409.addProperty(new FlowioPropertyFixed("fermsSubTms", 542, 567));
		binder1409.addProperty(new FlowioPropertyFixed("fenmsSubTms", 567, 592));
		binder1409.addProperty(new FlowioPropertyFixed("fenmsDlvTms", 592, 617));
		binder1409.addProperty(new FlowioPropertyFixed("fermsDlvTms", 617, 642));
		binder1409.addProperty(new FlowioPropertyFixed("extraDataLen", 642, 646));
		binder1409.addProperty(new FlowioPropertyFixed("extraData", 646, 2694));
		binder1409.addProperty(new FlowioPropertyFixed("fillerThree", 2694, 2894));
		binder1409.addProperty(new FlowioPropertyFixed("localAuthInfoAlg", 2894, 2902));
		binder1409.addProperty(new FlowioPropertyFixed("localAuthInfoLen", 2902, 2905));
		binder1409.addProperty(new FlowioPropertyFixed("localAuthInfo", 2905, 3033));
		
		return binder1409;
	}
}
