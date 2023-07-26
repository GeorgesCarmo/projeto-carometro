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
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import model.DAO;
import utils.Validador;

public class Carometro extends JFrame {

	// instanciando objetos JDBC
	DAO dao = new DAO();
	private Connection con;
	private PreparedStatement pst;
	private ResultSet rs;

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
	private JButton btnAdicionar;
	private JButton btnReset;
	private JButton btnBuscar;
	private JList listNomes;
	private JScrollPane scrollPaneLista;

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
		setIconImage(
				Toolkit.getDefaultToolkit().getImage(Carometro.class.getResource("/img/logotipo-do-instagram.png")));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 649, 362);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));

		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		scrollPaneLista = new JScrollPane();
		scrollPaneLista.setBorder(null);
		scrollPaneLista.setVisible(false);
		scrollPaneLista.setBounds(66, 86, 240, 92);
		contentPane.add(scrollPaneLista);
		
		listNomes = new JList();
		listNomes.setBorder(null);
		scrollPaneLista.setViewportView(listNomes);

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
				if (!caracteres.contains(e.getKeyChar() + "")) { // invertendo a logica com o operador not !e
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
		textNome.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		textNome.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				listarNomes();
			}
		});
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
		btnCarregar.setBounds(172, 113, 134, 23);
		contentPane.add(btnCarregar);

		btnAdicionar = new JButton("");
		btnAdicionar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				adicionar();
			}
		});
		btnAdicionar.setToolTipText("Adicionar");
		btnAdicionar.setIcon(new ImageIcon(Carometro.class.getResource("/img/create.png")));
		btnAdicionar.setBounds(10, 184, 64, 64);
		contentPane.add(btnAdicionar);

		btnReset = new JButton("");
		btnReset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				reset();
			}
		});
		btnReset.setToolTipText("Limpar campos");
		btnReset.setIcon(new ImageIcon(Carometro.class.getResource("/img/eraser.png")));
		btnReset.setBounds(107, 184, 64, 64);
		contentPane.add(btnReset);

		btnBuscar = new JButton("Buscar");
		btnBuscar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				buscarRa();
			}
		});
		btnBuscar.setForeground(SystemColor.textHighlight);
		btnBuscar.setBounds(205, 19, 86, 23);
		contentPane.add(btnBuscar);

	}// fim do construtor

	private void status() {
		try {
			con = dao.conectar();
			if (con == null) {
				// System.out.println("Erro de conexao");
				lblStatus.setIcon(new ImageIcon(Carometro.class.getResource("/img/dboff.png")));
			} else {
				// System.out.println("Banco de dados conectado");
				lblStatus.setIcon(new ImageIcon(Carometro.class.getResource("/img/dbon.png")));
			}
			con.close();
		} catch (Exception e) {
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
		jfc.setFileFilter(new FileNameExtensionFilter("Arquivo de imagens (*.PNG,*.JPG,*.JPEG)", "png", "jpg", "jpeg"));
		int resultado = jfc.showOpenDialog(this); // executar o explorador de arquivos
		if (resultado == JFileChooser.APPROVE_OPTION) {
			try {
				fis = new FileInputStream(jfc.getSelectedFile());
				tamanho = (int) jfc.getSelectedFile().length();
				Image foto = ImageIO.read(jfc.getSelectedFile()).getScaledInstance(lblFoto.getWidth(),
						lblFoto.getHeight(), Image.SCALE_SMOOTH);
				lblFoto.setIcon(new ImageIcon(foto));
			} catch (Exception e) {
				System.out.println(e);
			}
		}
	}

	private void adicionar() {
		if (textNome.getText().isEmpty()) { // se o campo nome estiver vazio
			JOptionPane.showMessageDialog(null, "Preencha o nome"); //
			textNome.requestFocus(); // coloca o cursor no campo nome
		} else {
			String insert = "insert into alunos(nome,foto) values(?,?)"; // inserir o registo do aluno no banco de dados
			try {
				con = dao.conectar(); // abrindo a conexao com banco de dados
				pst = con.prepareStatement(insert); // pst vai executar o comando insert
				pst.setString(1, textNome.getText()); // pst vai pegar a String recebida na caixa de texto e colocar na
														// interrogação
				pst.setBlob(2, fis, tamanho); // pst vai pegar a foto carregada e colocar na segunda interrogação
				int confirma = pst.executeUpdate();
				if (confirma == 1) {
					JOptionPane.showMessageDialog(null, "Aluno cadastrado com sucesso");
					reset();
				} else {
					JOptionPane.showMessageDialog(null, "Erro!. Aluno não cadastrado.");
				}
				con.close();
			} catch (Exception e) {
				System.out.println(e);
			}
		}

	}

	private void buscarRa() {
		if (textRa.getText().isEmpty()) {
			JOptionPane.showMessageDialog(null, "Digite o RA");
			textRa.requestFocus();
		} else {
			String readRa = "select * from alunos where ra = ?";
			try {
				con = dao.conectar();
				pst = con.prepareStatement(readRa);
				pst.setString(1, textRa.getText());
				rs = pst.executeQuery(); // executa a instrução SQL
				if (rs.next()) { // se existir um aluno com o ra correspondente
					textNome.setText(rs.getString(2)); // pega a segunda coluna da tabela que é o nome e seta no
														// textNome
					Blob blob = (Blob) rs.getBlob(3); // objeto blob pega os dados da terceira coluna da tabela que
														// estão em binário
					byte[] img = blob.getBytes(1, (int) blob.length()); // array byte pega os dados e converte em imagem
					BufferedImage imagem = null;
					try {
						imagem = ImageIO.read(new ByteArrayInputStream(img)); // cria o arquivo de imagem
					} catch (Exception e) {
						System.out.println(e);
					}
					ImageIcon icone = new ImageIcon(imagem);
					Icon foto = new ImageIcon(icone.getImage().getScaledInstance(lblFoto.getWidth(),
							lblFoto.getHeight(), Image.SCALE_SMOOTH));// ajusta a resolução da imagem dentro da Jlabel e o scale_smooth garante a melhor qualidade
					lblFoto.setIcon(foto); 
				} else {
					JOptionPane.showMessageDialog(null, "Aluno não cadastrado");
				}
				con.close();
			} catch (Exception e) {
				System.out.println(e);
			}
		}
	}
	
	private void listarNomes() {
		DefaultListModel<String> modelo = new DefaultListModel<>(); //cria objeto que vai receber um vetor de alunos
		listNomes.setModel(modelo);
		String readLista = "select * from alunos where nome like '" + textNome.getText() + "%'" + "order by nome";
		try {
			con = dao.conectar();
			pst = con.prepareStatement(readLista);
			rs = pst.executeQuery();
			while(rs.next()) {
				scrollPaneLista.setVisible(true);
				modelo.addElement(rs.getString(2));
				if(textNome.getText().isEmpty()) {
					scrollPaneLista.setVisible(false);
				}
			}
			con.close();
		} catch (Exception e) {
			System.out.println(e);
		}
		
	}

	private void reset() {
		scrollPaneLista.setVisible(false);
		textRa.setText(null);
		textNome.setText(null);
		lblFoto.setIcon(new ImageIcon(Carometro.class.getResource("/img/camera.png")));
		textNome.requestFocus();
	}
}
