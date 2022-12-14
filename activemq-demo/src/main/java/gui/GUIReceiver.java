package gui;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Properties;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.apache.log4j.BasicConfigurator;

import data.Person;
import helper.XMLConvert;

public class GUIReceiver extends JFrame implements ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextField textField;
	private JTextArea textArea;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GUIReceiver frame = new GUIReceiver();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public GUIReceiver() {
		setResizable(false);
		setAlwaysOnTop(true);
		getContentPane().setLayout(null);
		setSize(650, 550);
		setTitle("Nhan");
		textArea = new JTextArea();
		textArea.setEditable(false);
		textArea.setBounds(20, 41, 589, 386);
		getContentPane().add(textArea);

		textField = new JTextField();
		textField.setBounds(20, 437, 500, 39);
		getContentPane().add(textField);
		textField.setColumns(10);

		JButton btnNewButton = new JButton("Send");
		btnNewButton.setFont(new Font("Tahoma", Font.PLAIN, 20));
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		btnNewButton.setBounds(524, 437, 85, 39);
		getContentPane().add(btnNewButton);

		btnNewButton.addActionListener(this);
		try {
			receiver();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}

	public void receiver() throws Exception {
		BasicConfigurator.configure();
		// thi???t l???p m??i tr?????ng cho JJNDI
		Properties settings = new Properties();
		settings.setProperty(Context.INITIAL_CONTEXT_FACTORY, "org.apache.activemq.jndi.ActiveMQInitialContextFactory");
		settings.setProperty(Context.PROVIDER_URL, "tcp://localhost:61616");
		// t???o context
		Context ctx = new InitialContext(settings);
		// lookup JMS connection factory
		Object obj = ctx.lookup("ConnectionFactory");
		ConnectionFactory factory = (ConnectionFactory) obj;
		// lookup destination
		Destination destination = (Destination) ctx.lookup("dynamicQueues/GetOrder");
		// t???o connection
		Connection con = factory.createConnection("admin", "admin");
		// n???i ?????n MOM
		con.start();
		// t???o session
		Session session = con.createSession(/* transaction */false, /* ACK */Session.CLIENT_ACKNOWLEDGE);
		// t???o consumer
		MessageConsumer receiver = session.createConsumer(destination);
		// blocked-method for receiving message - sync
		// receiver.receive();
		// Cho receiver l???ng nghe tr??n queue, ch???ng c?? message th?? notify - async
		System.out.println("T?? was listened on queue...");
		receiver.setMessageListener(new MessageListener() {

			// c?? message ?????n queue, ph????ng th???c n??y ???????c th???c thi
			public void onMessage(Message msg) {// msg l?? message nh???n ???????c
				try {
					if (msg instanceof TextMessage) {
						TextMessage tm = (TextMessage) msg;
						String txt = tm.getText();
						System.out.println("Nh???n ???????c " + txt);

						int indexStart = txt.indexOf("<hoten>");
						int indexEnd = txt.indexOf("</hoten>");
						int indexMSStart = txt.indexOf("<mssv>");
						int indexMSEnd = txt.indexOf("</mssv>");
						System.out.println("index " + indexStart);
							System.out.println(txt);
							String textMS = txt.substring(indexMSStart, indexMSEnd);
							String text = txt.substring(indexStart + 100, indexEnd);
							text.replaceAll("<hoten>", "");
							textMS.replaceAll("<mssv>", "");
							if(textArea.getText().indexOf(textMS) == -1) {
								textArea.append("\nMSSV: " + textMS);
							}
							textArea.append("\nContent: " + text);
						
						msg.acknowledge();// g???i t??n hi???u ack
					} else if (msg instanceof ObjectMessage) {
						ObjectMessage om = (ObjectMessage) msg;
						System.out.println(om);
					}
//others message type....
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub

	}
}