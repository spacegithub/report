package com.ruisi.vdop.util;

import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import org.apache.struts2.ServletActionContext;

import com.ruisi.ext.engine.ExtConstants;
import com.ruisi.ext.engine.dao.DaoHelper;
import com.ruisi.ext.engine.dao.TestDaoHelperImpl;
import com.ruisi.ext.engine.init.XmlParser;
import com.ruisi.ext.engine.view.context.ExtContext;
import com.ruisi.ext.engine.wrapper.ExtRequest;
import com.ruisi.ext.engine.wrapper.ExtSession;
import com.ruisi.vdop.bean.User;

public final class VDOPUtils {
	
	private static ServletContext myServletContext;
	
	public static ServletContext getMyServletContext() {
		return myServletContext;
	}

	public static void setMyServletContext(ServletContext myServletContext) {
		VDOPUtils.myServletContext = myServletContext;
	}

	/**
	 * 获取一个新的user对象。
	 * @return
	 */
	public static User getNewUser(){
		
		return new User();
	}
	
	public static void main(String[] args){
		String str = "123456";
		System.out.println(getEncodedStr(str));
	}
	
	public static ServletContext getServletContext(){
		return ServletActionContext.getServletContext();
	}
	
	public static User getLoginedUser(){
		return getLoginedUser(false);
	}
	public static User getLoginedUser(boolean is3G){
		HttpSession session=getSession();
		User u=null;
		if(session!=null){
			u=(User)session.getAttribute(is3G ? VdopConstant.USER_KEY_IN_SESSION_3G:VdopConstant.USER_KEY_IN_SESSION);
		}
		return u;
	}
	public static User getLoginedUser(ExtRequest req){
		return getLoginedUser(req, false);
	}
	public static User getLoginedUser(ExtRequest req, boolean is3G){
		ExtSession session=req.getSession();
		User u=null;
		if(session!=null){
			u=(User)session.getAttribute(is3G ? VdopConstant.USER_KEY_IN_SESSION_3G:VdopConstant.USER_KEY_IN_SESSION);
		}
		return u;
	}
	public static User getLoginedUser(HttpServletRequest req){
		return getLoginedUser(req, false);
	}
	public static User getLoginedUser(HttpServletRequest req, boolean is3G){
		HttpSession session=req.getSession();
		User u=null;
		if(session!=null){
			u=(User)session.getAttribute(is3G ? VdopConstant.USER_KEY_IN_SESSION_3G:VdopConstant.USER_KEY_IN_SESSION);
		}
		return u;
	}
	
	/**
	 * 将user存入session中。
	 * @return
	 */
	public static void saveLoginedUser(User user, boolean is3g, HttpServletRequest req, ServletContext ctx){
		HttpSession session=getSession();
		if(session==null)session=getSession(true);
		session.setAttribute(is3g?VdopConstant.USER_KEY_IN_SESSION_3G:VdopConstant.USER_KEY_IN_SESSION,user);
		//添加登录用户数
		Integer userCnt = (Integer)ctx.getAttribute(VdopConstant.USER_CNT);
		if(userCnt == null){
			ctx.setAttribute(VdopConstant.USER_CNT, 1);
		}else{
			ctx.setAttribute(VdopConstant.USER_CNT, userCnt + 1);
		}
		XmlParser.putXmlData();
	}
	/**
	 * 将uservo 从 session中移除。
	 * @return
	 */
	public static void removeLoginUser(ServletContext ctx, HttpSession s, boolean is3G){
		if(s == null){
			return;
		}
		User u=(User)s.getAttribute(is3G ? VdopConstant.USER_KEY_IN_SESSION_3G:VdopConstant.USER_KEY_IN_SESSION);
				
		if(u==null){//此时没有登录或已经超时
			return ;
		}
		u.setLogoutTime(Calendar.getInstance().getTime());//记录登出时间
		//LogoutServ.updateLog(u);//对登录日志和在线更新
		s.removeAttribute(is3G ? VdopConstant.USER_KEY_IN_SESSION_3G:VdopConstant.USER_KEY_IN_SESSION);
		s.removeAttribute(ExtConstants.loginUserKey);
		
		//让session失效
		//减少登录用户数
		Integer userCnt = (Integer)ctx.getAttribute(VdopConstant.USER_CNT);
		if(userCnt != null){
			ctx.setAttribute(VdopConstant.USER_CNT, userCnt - 1);
		}
		XmlParser.removeXmlData();
	}
	public static int getOnlineUser(ServletContext ctx){
		Integer userCnt = (Integer)ctx.getAttribute(VdopConstant.USER_CNT);
		if(userCnt == null){
			return 0;
		}else{
			return userCnt;
		}
	}
	public static boolean getIsEncoding(){
		return true;
	}
	/**
	 * 给str加密md5。
	 * @return
	 */
	public static String getEncodedStr(String str){
		if(str==null)return null;
		return getMD5(str.getBytes());
	}

	/**
	 * 获取字符串MD5
	 * @param source
	 * @return
	 */
	public static String getMD5(byte[] source) {  
	    String s = null;  
	    char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',  
	            'a', 'b', 'c', 'd', 'e', 'f' };// 用来将字节转换成16进制表示的字符  
	    try {  
	        java.security.MessageDigest md = java.security.MessageDigest  
	                .getInstance("MD5");  
	        md.update(source);  
	        byte tmp[] = md.digest();// MD5 的计算结果是一个 128 位的长整数，  
	        // 用字节表示就是 16 个字节  
	        char str[] = new char[16 * 2];// 每个字节用 16 进制表示的话，使用两个字符， 所以表示成 16  
	        // 进制需要 32 个字符  
	        int k = 0;// 表示转换结果中对应的字符位置  
	        for (int i = 0; i < 16; i++) {// 从第一个字节开始，对 MD5 的每一个字节// 转换成 16  
	            // 进制字符的转换  
	            byte byte0 = tmp[i];// 取第 i 个字节  
	            str[k++] = hexDigits[byte0 >>> 4 & 0xf];// 取字节中高 4 位的数字转换,// >>>  
	            // 为逻辑右移，将符号位一起右移  
	            str[k++] = hexDigits[byte0 & 0xf];// 取字节中低 4 位的数字转换  

	        }  
	        s = new String(str);// 换后的结果转换为字符串  

	    } catch (NoSuchAlgorithmException e) {  
	        // TODO Auto-generated catch block  
	        e.printStackTrace();  
	    }  
	    return s;  
	}  
	
	/**
	 * 获取session。
	 * @return
	 */
	public static HttpSession getSession(boolean create){
		
		HttpServletRequest request = ServletActionContext.getRequest();  
		if(create){
			return request.getSession(true);
		}else{
			return request.getSession();
		}
		
	}
	/**
	 * 获取session。
	 * @return
	 */
	public static HttpSession getSession(){
		
		HttpServletRequest request = ServletActionContext.getRequest();
		return request.getSession();
		
		
	}
	/**
	 * 获取请求request。
	 * @return
	 */
	public static HttpServletRequest getRequest(){
		return ServletActionContext.getRequest(); 
	}
	
	public static HttpServletResponse getResponse(){
		return ServletActionContext.getResponse();
	}
	
	/**
	 * 取daoHelper
	 * @return
	 */
	public static DaoHelper getDaoHelper(){
		try{
			ServletContext ctx = ServletActionContext.getServletContext();
			return (DaoHelper)SpringUtil.getApplicationContext(ctx).getBean("daoHelper");
		}catch(Exception ex){
			return getDaoHelper(VDOPUtils.myServletContext);
		}
		
	}
	
	public static DaoHelper getDaoHelper(ServletContext ctx){
		return (DaoHelper)SpringUtil.getApplicationContext(ctx).getBean("daoHelper");
	}
	
	/**
	 * 获取ext-config中配置的变量。
	 * @return
	 */
	public static String getConstant(String name){
		return ExtContext.getInstance().getConstant(name);
	}
	
	public static String getUUIDStr(){
		return UUID.randomUUID().toString().replace("-","");
	}
	
	public static int getSEQ(ServletContext ctx){
		DaoHelper dao = VDOPUtils.getDaoHelper(ctx);
		return dao.queryForInt("select seq_sys_id.nextval from dual");
	}
	
	public static int getSEQ(){
		ServletContext ctx = ServletActionContext.getServletContext();
		return getSEQ(ctx);
	}
	
	public static Connection getConnection() throws SQLException{
		ServletContext ctx = VDOPUtils.getServletContext();
		DataSource ds = (DataSource)SpringUtil.getApplicationContext(ctx).getBean("dataSource");
		return ds.getConnection();
	}
	
	public static String dealStringParam(String vals){
		String[] vls = vals.split(",");
		StringBuffer sb = new StringBuffer();
		for(int i=0; i<vls.length; i++){
			String v = vls[i];
			sb.append("'" + v + "'");
			if(i != vls.length - 1){
				sb.append(",");
			}
		}
		return sb.toString();
	}
	
	public static Map<String, String> getAllParams(HttpServletRequest req){
		Map<String, String> dt = new HashMap<String, String>();
		Enumeration enu = req.getParameterNames();
		while(enu.hasMoreElements()){
			String key = (String)enu.nextElement();
			dt.put(key, req.getParameter(key));
		}
		return dt;
	}
}
