/*  
 * @(#) MailSender.java Create on 2012-7-31 下午06:05:14   
 *   
 * Copyright 2012 by xl.   
 */

package cn.tools;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;
import cn.system.basic.global.bean.MailInfo;

/**
 * 
 * @author zxd
 * @date 2012-7-31
 */
public class MailHelper {

	Properties props = null;

	public static void main(String[] args) throws Exception {
		/*
		 * MailInfo mailInfo = new MailInfo();
		 * mailInfo.setMailServerHost("192.168.0.180"); //10.10.64.243
		 * mailInfo.setMailServerPort("25"); //
		 * mailInfo.setFromAddress("service@xlmediawatch.com");
		 * mailInfo.setUserName("service"); mailInfo.setPassword("service");
		 * String[] toAddress = {"253392975@qq.com"};
		 * mailInfo.setToAddress(toAddress); mailInfo.setContent("test");
		 * mailInfo.setValidate(true); MailHelper mailHelper = new MailHelper();
		 * mailHelper.sendTextMail(mailInfo);
		 */

		MailInfo mailInfo = new MailInfo();
		// mailInfo.setMailServerHost("10.10.64.243");
		// mailInfo.setMailServerPort("9001");
		mailInfo.setMailServerHost("mail.chinaso.com");
		mailInfo.setMailServerPort("25");
		mailInfo.setFromAddress("hubaoting@chinaso.com");
		mailInfo.setFormName("中国搜索——胡宝婷");
		mailInfo.setUserName("hubaoting");
		mailInfo.setPassword("Qegqw861010");
		String[] toAddress = { "253392975@qq.com" };
		mailInfo.setToAddress(toAddress);
		mailInfo.setSubject("333333333");
		mailInfo.setContent("test1111");
		mailInfo.setAttachFileNames(new String[]{"cc.zip", "bb.zip"});
		mailInfo.setValidate(true);
		MailHelper mailHelper = new MailHelper();
		mailHelper.sendTextMail(mailInfo);
	}

	/**
	 * 发送文本邮件
	 * 
	 * @Title: sendTextMail
	 * @data:2012-8-1上午10:50:59
	 * @author:zxd
	 *
	 * @param mailInfo
	 * @throws MessagingException
	 */
	public void sendTextMail(MailInfo mailInfo) throws Exception {
		Message mailMessage = this.sendMail(mailInfo);
		// 设置发送邮件的内容
		mailMessage.setText(mailInfo.getContent());
		// 发送邮件[自带关闭]
		Transport.send(mailMessage);
	}

	/**
	 * 发送html文件
	 * 
	 * @Title: sendHtmlMail
	 * @data:2012-8-1上午10:51:15
	 * @author:zxd
	 *
	 * @param mailInfo
	 * @throws MessagingException
	 * @throws UnsupportedEncodingException
	 */
	public void sendHtmlMail(MailInfo mailInfo) throws Exception {
		Message mailMessage = this.sendMail(mailInfo);
		// Multipart类是一个容器类，包含MimeBodyPart类型的对象
		Multipart multiPart = new MimeMultipart();
		// 创建一个包含HTML内容的MimeBodyPart
		BodyPart bodyPart = new MimeBodyPart();
		// 创建一个包含HTML内容的MimeBodyPart
		bodyPart.setContent(mailInfo.getContent(), "text/html; charset=utf-8");
		multiPart.addBodyPart(bodyPart);
		// 附件数组
		String[] attachs = mailInfo.getAttachFileNames();
		if (attachs != null && attachs.length > 0) {
			for (int i = 0; i < attachs.length; i++) {
				if (!attachs[i].equals("")) {
					bodyPart = new MimeBodyPart();
					// 获得附件
					DataSource dataSource = new FileDataSource(attachs[i]);
					// 设置附件的数据处理器
					bodyPart.setDataHandler(new DataHandler(dataSource));
					// 设置附件名字
					String attachsName = attachs[i].substring(attachs[i].lastIndexOf("/") + 1);
					bodyPart.setFileName(MimeUtility.encodeText(attachsName));
					multiPart.addBodyPart(bodyPart);
				}
			}
		}
		// 将MimeMultipart对象设置为邮件内容
		mailMessage.setContent(multiPart);
		// 发送邮件
		Transport.send(mailMessage);
	}

	/**
	 * 公共发送邮件
	 * 
	 * @Title: sendMail
	 * @data:2012-8-1上午10:52:27
	 * @author:zxd
	 *
	 * @param mailInfo
	 * @return
	 * @throws MessagingException
	 */
	public Message sendMail(MailInfo mailInfo) throws Exception {
		final String userName = mailInfo.getUserName();
		final String password = mailInfo.getPassword();
		props = mailInfo.getProperties();
		// 根据邮件会话属性和密码验证器构造一个发送邮件的session
		Session mailSession = null;
		mailSession = Session.getInstance(props, new Authenticator() {

			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(userName, password);
			}
		});
		// 根据session创建一个邮件信息
		Message mailMessage = new MimeMessage(mailSession);
		// 创建邮件发送者地址
		Address fromAddress = new InternetAddress(mailInfo.getFromAddress(), MimeUtility.decodeText(mailInfo.getFormName()));
		mailMessage.setFrom(fromAddress);

		// 收件人地址列表
		String[] toAddress = mailInfo.getToAddress();
		// 创建一个InternetAddress数组，长度为toAddress.length
		InternetAddress[] sendTo = new InternetAddress[toAddress.length];
		// 发送多个收件人
		for (int i = 0; i < toAddress.length; i++) {
			// System.out.println("直接发送到" + toAddress[i]);
			sendTo[i] = new InternetAddress(toAddress[i]);
		}
		// 设置收件人地址，Message.RecipientType.TO属性表示接收者的类型为TO
		mailMessage.setRecipients(Message.RecipientType.TO, sendTo);
		// 设置抄送人的地址
		String[] ccs = mailInfo.getCcs();
		if (ccs != null) {
			// 为每个邮件接收者创建一个地址
			Address[] ccAdresses = new InternetAddress[ccs.length];
			for (int i = 0; i < ccs.length; i++) {
				System.out.println("抄送到" + ccs[i]);
				ccAdresses[i] = new InternetAddress(ccs[i]);
			}
			// 将抄送者信息设置到邮件信息中，注意类型为Message.RecipientType.CC
			mailMessage.setRecipients(Message.RecipientType.CC, ccAdresses);
		}
		// 设置密送人的地址
		String[] bcc = mailInfo.getBcc();
		if (bcc != null) {
			// 为每个邮件接收者创建一个地址
			Address[] bccAdresses = new InternetAddress[bcc.length];
			for (int i = 0; i < bcc.length; i++) {
				System.out.println("密送到" + bcc[i]);
				bccAdresses[i] = new InternetAddress(bcc[i]);
			}
			// 将MI送者信息设置到邮件信息中，注意类型为Message.RecipientType.bcc
			mailMessage.setRecipients(Message.RecipientType.BCC, bccAdresses);
		}
		if(null!=mailInfo.getAttachFileNames() && 0<mailInfo.getAttachFileNames().length){
			int count = mailInfo.getAttachFileNames().length;
			for (int i = 0; i < count; i++) {
				String filePath = mailInfo.getAttachFileNames()[i];
				Multipart mp = new MimeMultipart();  
				MimeBodyPart mbp = new MimeBodyPart(); 
				FileDataSource fds = new FileDataSource(filePath); //得到数据源  
				mbp.setDataHandler(new DataHandler(fds)); //得到附件本身并至入BodyPart  
				mbp.setFileName(fds.getName());  		//得到文件名同样至入BodyPart  
				mp.addBodyPart(mbp);
				mailMessage.setContent(mp);
			}
			mailInfo.setAttachFileNames(null);
		}
		
		// 设置邮件消息的主题
		mailMessage.setSubject(mailInfo.getSubject());
		// 设置邮件发送时间
		mailMessage.setSentDate(new Date());
		return mailMessage;
	}
}
