package gui;

import java.awt.Component;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.Properties;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.apache.log4j.BasicConfigurator;

import demo01.ex01.QueueReceiver;
import demo01.ex01.QueueSender;
import data.Person;
import helper.XMLConvert;

import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.swing.JButton;
import javax.swing.JTextPane;
import javax.swing.JTextArea;

public class App extends JFrame {

	private JPanel contentPane;
	private JTextField txtMessage;

	/**
	 * Launch the application.
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception  {




		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					App frame = new App();
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
	public App()  {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);

		JPanel panel = new JPanel();
		panel.setBounds(0, 0, 434, 261);
		contentPane.add(panel);
		panel.setLayout(null);

		txtMessage = new JTextField();
		txtMessage.setBounds(10, 219, 297, 31);
		panel.add(txtMessage);
		txtMessage.setColumns(10);

		JButton btnSend = new JButton("SEND");
		btnSend.setBounds(318, 219, 92, 31);
		panel.add(btnSend);

		JTextArea txtArea = new JTextArea();
		txtArea.setBounds(10, 11, 297, 197);
		panel.add(txtArea);

		QueueSender qSend = new QueueSender();
		QueueReceiver qRecei = new QueueReceiver();

		btnSend.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				String mes = txtMessage.getText();
				try {
					send(mes);
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
	}
	public void send(String mes) throws Exception {

		//config environment for JMS
		BasicConfigurator.configure();
		//config environment for JNDI
		Properties settings = new Properties();
		settings.setProperty(Context.INITIAL_CONTEXT_FACTORY,
				"org.apache.activemq.jndi.ActiveMQInitialContextFactory");
		settings.setProperty(Context.PROVIDER_URL, "tcp://localhost:61616");
		//create context
		Context ctx = new InitialContext(settings);
		//lookup JMS connection factory
		ConnectionFactory factory =
				(ConnectionFactory) ctx.lookup("ConnectionFactory");
		//lookup destination. (If not exist-->ActiveMQ create once)
		Destination destination =
				(Destination) ctx.lookup("dynamicQueues/Order");
		//get connection using credential
		Connection con = factory.createConnection("admin", "admin");
		//connect to MOM
		con.start();
		//create session
		Session session = con.createSession(
				/*transaction*/false,
				/*ACK*/Session.AUTO_ACKNOWLEDGE
				);
		//create producer
		MessageProducer producer = session.createProducer(destination);
		//create text message
		Message msg = session.createTextMessage(mes);
		producer.send(msg);
	
		//shutdown connection
		session.close();
		con.close();
		System.out.println("Da gui..." + mes);
	}
}