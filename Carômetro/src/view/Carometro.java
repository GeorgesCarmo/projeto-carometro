package view;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Image;
import java.awt.SystemColor;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileInputStream;
import java.sql.Connection;
import java.text.DateFormat;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import model.DAO;
import utils.Validador;

public class Carometro extends JFrame {
	
	//instanciando objetos JDBC
	DAO dao = new DAO();
	private Connection con;
	
	// instanciando objeto para o fluxo de bytes
	private FileInputStream fis;
	
	// variável global para armazenar tamanho da imagem(bytes)
	private int tamanho;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JLabel lblStatus;
	private JLabel lblData;
	private JLabel lblNewLabel;
	private JTextField textRa;
	private JLabel lblNewLabel_1;
	private JTextField textNome;
	private JLabel lblFoto;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Carometro frame = new Carometro();
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
	public Carometro() {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowActivated(WindowEvent e) {
				status();
				setarData();
			}
		});
		setTitle("Carêmetro");
		setResizable(false);
		setIconImage(Toolkit.getDefaultToolkit().getImage(Carometro.class.getResource("/img/logotipo-do-instagram.png")));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 649, 362);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JPanel panel = new JPanel();
		panel.setBackground(SystemColor.textHighlight);
		panel.setBounds(0, 269, 633, 54);
		contentPane.add(panel);
		panel.setLayout(null);
		
		lblStatus = new JLabel("");
		lblStatus.setIcon(new ImageIcon(Carometro.class.getResource("/img/dboff.png")));
		lblStatus.setBounds(591, 11, 32, 32);
		panel.add(lblStatus);
		
		lblData = new JLabel("");
		lblData.setForeground(SystemColor.text);
		lblData.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblData.setBounds(33, 11, 397, 32);
		panel.add(lblData);
		
		lblNewLabel = new JLabel("RA");
		lblNewLabel.setBounds(10, 23, 46, 14);
		contentPane.add(lblNewLabel);
		
		textRa = new JTextField();
		textRa.addKeyListener(new KeyAdapter() {
			@Override
			public void keyTyped(KeyEvent e) {
				String caracteres = "0123456789"; // caracteres que serão aceitos
				if(!caracteres.contains(e.getKeyChar() + "")) { //invertendo a logica com o operador not !e
					e.consume();
				}
			}
		});
		textRa.setBounds(37, 20, 86, 20);
		contentPane.add(textRa);
		textRa.setColumns(10);
		// uso do PlainDocument para limitar os campos
		textRa.setDocument(new Validador(6));
		
		
		lblNewLabel_1 = new JLabel("Nome");
		lblNewLabel_1.setBounds(10, 69, 46, 14);
		contentPane.add(lblNewLabel_1);
		
		textNome = new JTextField();
		textNome.setBounds(66, 66, 240, 20);
		contentPane.add(textNome);
		textNome.setColumns(10);
		// uso do PlainDocument para limitar os campos
				textNome.setDocument(new Validador(30));
				
				lblFoto = new JLabel("");
				lblFoto.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
				lblFoto.setIcon(new ImageIcon(Carometro.class.getResource("/img/camera.png")));
				lblFoto.setBounds(367, 11, 256, 256);
				contentPane.add(lblFoto);
				
				JButton btnCarregar = new JButton("Carregar foto");
				btnCarregar.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						carregarFoto();
					}
				});
				btnCarregar.setForeground(SystemColor.textHighlight);
				btnCarregar.setBounds(191, 114, 134, 23);
				contentPane.add(btnCarregar);
		
	}// fim do construtor
	
	private void status() {
		try {
			con = dao.conectar();
			if(con == null) {
				//System.out.println("Erro de conexao");
				lblStatus.setIcon(new ImageIcon(Carometro.class.getResource("/img/dboff.png")));
			}else {
				//System.out.println("Banco de dados conectado");
				lblStatus.setIcon(new ImageIcon(Carometro.class.getResource("/img/dbon.png")));
			}
			con.close();
		}catch(Exception e) {
			System.out.println(e);
		}
	}
	
	private void setarData() {
		Date data = new Date(); // obter a data do sistema
		DateFormat formatador = DateFormat.getDateInstance(DateFormat.FULL); // formatação da data
		lblData.setText(formatador.format(data)); // modificando o texto da label para a data formatada
	}
	
	private void carregarFoto() {
		JFileChooser jfc = new JFileChooser();
		jfc.setDialogTitle("Selecionar arquivo");
		jfc.setFileFilter(new FileNameExtensionFilter("Arquivo de imagens (*.PNG,*.JPG,*.JPEG)","png","jpg","jpeg"));
		int resultado = jfc.showOpenDialog(this); // executar o explorador de arquivos
		if(resultado == JFileChooser.APPROVE_OPTION) {
			try {
				fis = new FileInputStream(jfc.getSelectedFile());
				tamanho = (int) jfc.getSelectedFile().length();
				Image foto = ImageIO.read(jfc.getSelectedFile()).getScaledInstance(lblFoto.getWidth(), lblFoto.getHeight(), Image.SCALE_SMOOTH);
				lblFoto.setIcon(new ImageIcon(foto));
			}catch(Exception e) {
				System.out.println(e);
			}
		}
	}
}
