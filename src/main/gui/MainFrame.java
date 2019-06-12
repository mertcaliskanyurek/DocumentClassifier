package main.gui;

import main.TestBot;
import zemberek.core.logging.Log;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;

import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import java.util.Properties;

public class MainFrame extends JFrame implements WindowInterface{
	/*
	private static final ResourceBundle RESOURCES = ResourceBundle.getBundle("main.res.MainFrame").;
	private static final ResourceBundle ERRORS = ResourceBundle.getBundle("main.res.Error");
*/
	private Properties mResourceProps;
	private Properties mErrorProps;

	private JPanel contentPane;
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private JTextField textFieldAddCategoryName;
	private JTextField textFieldDeleteCategoryName;
	private static JTextArea textAreaOutput;
	private static JTextArea textAreaLog;

	private JPanel panel_kategori_sil;
	private JPanel panel_stop_words;
	private JPanel panel_cikti_yolu;
	private JPanel panel_kategori_ekle;

	private JLabel labelCiktiYolu;
	private JLabel lblStaffWordsDosyaYolu;

	private JRadioButton rdbtnTrain;
	private JButton btnTrain;
	private JButton btnTest;

	private MainFrameListener mListener;
	private File[] mDocumentFiles=null;

	public MainFrame(MainFrameListener listener)
	{
		mListener = listener;

		mResourceProps = LanguageUtils.getProperties(this.getClass(),LanguageUtils.PROPS_MAIN_FRAME);
		mErrorProps = LanguageUtils.getProperties(this.getClass(),LanguageUtils.PROPS_MAIN_ERROR);

		if(mResourceProps==null || mErrorProps==null)
		{
			System.out.println("Error while reading properties.");
			Log.error("Error while reading properties.");
			return;
		}

		setTitle(mResourceProps.getProperty("windowTitle"));
		this.setSize(820,600);
		//this.setSize(1280,768);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(0, 0, 0, 0));
		setContentPane(contentPane);

		JPanel panel_output = new JPanel();
		panel_output.setBorder(new TitledBorder(null, mResourceProps.getProperty("output"), TitledBorder.LEADING, TitledBorder.TOP, null, null));
		
		panel_kategori_sil = new JPanel();
		panel_kategori_sil.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), mResourceProps.getProperty("deleteCategory"), TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		
		panel_stop_words = new JPanel();
		panel_stop_words.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), mResourceProps.getProperty("stopWordsFile"), TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		
		panel_cikti_yolu = new JPanel();
		panel_cikti_yolu.setBorder(new TitledBorder(null, mResourceProps.getProperty("outputPath"), TitledBorder.LEADING, TitledBorder.TOP, null, null));
		
		btnTrain = new JButton(mResourceProps.getProperty("train"));
		btnTrain.addActionListener(new TrainPressListener());
		
		JPanel panel = new JPanel();
		panel.setBorder(new TitledBorder(null, mResourceProps.getProperty("log"), TitledBorder.LEADING, TitledBorder.TOP, null, null));
		
		panel_kategori_ekle = new JPanel();
		panel_kategori_ekle.setBorder(new TitledBorder(null, mResourceProps.getProperty("addCategory"), TitledBorder.LEADING, TitledBorder.TOP, null, null));
		
		JLabel lblKategoriIsmi = new JLabel(mResourceProps.getProperty("categoryName"));
		
		textFieldAddCategoryName = new JTextField();
		textFieldAddCategoryName.setColumns(10);
		
		JLabel lblDkmanEkle = new JLabel(mResourceProps.getProperty("addDocument"));
		
		JButton btnBrowseDocument = new JButton(mResourceProps.getProperty("browse"));
		btnBrowseDocument.addActionListener(new BrowseDocumentListener());
		
		JButton btnAddCategory = new JButton(mResourceProps.getProperty("addCategory"));
		btnAddCategory.addActionListener(new AddCategoryListener());

		ButtonGroup trainTestRadioButtons = new ButtonGroup();
		rdbtnTrain = new JRadioButton(mResourceProps.getProperty("train"));
		rdbtnTrain.setSelected(true);
		JRadioButton rdbtnTest = new JRadioButton(mResourceProps.getProperty("test"));

		trainTestRadioButtons.add(rdbtnTest);
		trainTestRadioButtons.add(rdbtnTrain);
		
		GroupLayout gl_panel_kategori_ekle = new GroupLayout(panel_kategori_ekle);
		gl_panel_kategori_ekle.setHorizontalGroup(
			gl_panel_kategori_ekle.createParallelGroup(Alignment.LEADING)
				.addGroup(Alignment.TRAILING, gl_panel_kategori_ekle.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel_kategori_ekle.createParallelGroup(Alignment.TRAILING)
						.addComponent(btnAddCategory, GroupLayout.DEFAULT_SIZE, 295, Short.MAX_VALUE)
						.addComponent(lblDkmanEkle, Alignment.LEADING)
						.addGroup(Alignment.LEADING, gl_panel_kategori_ekle.createSequentialGroup()
							.addComponent(lblKategoriIsmi)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(textFieldAddCategoryName, GroupLayout.DEFAULT_SIZE, 222, Short.MAX_VALUE))
						.addGroup(Alignment.LEADING, gl_panel_kategori_ekle.createSequentialGroup()
							.addComponent(rdbtnTrain)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(rdbtnTest)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(btnBrowseDocument, GroupLayout.DEFAULT_SIZE, 186, Short.MAX_VALUE)))
					.addContainerGap())
		);
		gl_panel_kategori_ekle.setVerticalGroup(
			gl_panel_kategori_ekle.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_kategori_ekle.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel_kategori_ekle.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblKategoriIsmi)
						.addComponent(textFieldAddCategoryName, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(lblDkmanEkle)
					.addGap(13)
					.addGroup(gl_panel_kategori_ekle.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnBrowseDocument)
						.addComponent(rdbtnTrain)
						.addComponent(rdbtnTest))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnAddCategory)
					.addGap(2))
		);
		panel_kategori_ekle.setLayout(gl_panel_kategori_ekle);
		
		btnTest = new JButton(mResourceProps.getProperty("test"));
		btnTest.addActionListener(new TestPressListener());
		
		GroupLayout gl_contentPane = new GroupLayout(contentPane);
		gl_contentPane.setHorizontalGroup(
			gl_contentPane.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.TRAILING)
						.addComponent(panel, Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 784, Short.MAX_VALUE)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addComponent(panel_output, GroupLayout.PREFERRED_SIZE, 458, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
								.addComponent(panel_kategori_ekle, GroupLayout.PREFERRED_SIZE, 320, GroupLayout.PREFERRED_SIZE)
								.addComponent(panel_cikti_yolu, GroupLayout.DEFAULT_SIZE, 320, Short.MAX_VALUE)
								.addComponent(panel_stop_words, GroupLayout.DEFAULT_SIZE, 320, Short.MAX_VALUE)
								.addComponent(panel_kategori_sil, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 320, Short.MAX_VALUE)
								.addGroup(gl_contentPane.createSequentialGroup()
									.addComponent(btnTrain, GroupLayout.PREFERRED_SIZE, 151, GroupLayout.PREFERRED_SIZE)
									.addGap(18)
									.addComponent(btnTest, GroupLayout.DEFAULT_SIZE, 151, Short.MAX_VALUE)))))
					.addContainerGap())
		);
		gl_contentPane.setVerticalGroup(
			gl_contentPane.createParallelGroup(Alignment.TRAILING)
				.addGroup(gl_contentPane.createSequentialGroup()
					.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_contentPane.createSequentialGroup()
							.addContainerGap()
							.addComponent(panel_kategori_ekle, GroupLayout.PREFERRED_SIZE, 155, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(panel_kategori_sil, GroupLayout.PREFERRED_SIZE, 95, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(panel_stop_words, GroupLayout.PREFERRED_SIZE, 78, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(panel_cikti_yolu, GroupLayout.PREFERRED_SIZE, 87, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(gl_contentPane.createParallelGroup(Alignment.LEADING)
								.addComponent(btnTrain, GroupLayout.PREFERRED_SIZE, 32, GroupLayout.PREFERRED_SIZE)
								.addComponent(btnTest, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
						.addComponent(panel_output, 0, 0, Short.MAX_VALUE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(panel, GroupLayout.PREFERRED_SIZE, 63, GroupLayout.PREFERRED_SIZE)
					.addContainerGap())
		);
		
		textAreaOutput = new JTextArea();
		textAreaOutput.setEditable(false);
		JScrollPane outputScroll = new JScrollPane(textAreaOutput);
		GroupLayout gl_panel_output = new GroupLayout(panel_output);
		gl_panel_output.setHorizontalGroup(
			gl_panel_output.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_output.createSequentialGroup()
					.addComponent(outputScroll, GroupLayout.PREFERRED_SIZE, 446, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		gl_panel_output.setVerticalGroup(
			gl_panel_output.createParallelGroup(Alignment.LEADING)
				.addComponent(outputScroll, GroupLayout.DEFAULT_SIZE, 464, Short.MAX_VALUE)
		);
		panel_output.setLayout(gl_panel_output);
		
		textAreaLog = new JTextArea();
		textAreaLog.setEditable(false);
		JScrollPane logScroll = new JScrollPane(textAreaLog);
		GroupLayout gl_panel = new GroupLayout(panel);
		gl_panel.setHorizontalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel.createSequentialGroup()
					.addComponent(logScroll, GroupLayout.PREFERRED_SIZE, 772, GroupLayout.PREFERRED_SIZE)
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		gl_panel.setVerticalGroup(
			gl_panel.createParallelGroup(Alignment.LEADING)
				.addComponent(logScroll, GroupLayout.DEFAULT_SIZE, 40, Short.MAX_VALUE)
		);
		panel.setLayout(gl_panel);

		labelCiktiYolu = new JLabel(mResourceProps.getProperty("filePath"));
		//for homework. delete after sended
		labelCiktiYolu.setText(TestBot.defaultOutputFilePath);
		
		JButton btnBrowseOutputDir = new JButton(mResourceProps.getProperty("browse"));
		btnBrowseOutputDir.addActionListener(new BrowseOutputFileListener());
		
		GroupLayout gl_panel_cikti_yolu = new GroupLayout(panel_cikti_yolu);
		gl_panel_cikti_yolu.setHorizontalGroup(
			gl_panel_cikti_yolu.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_cikti_yolu.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel_cikti_yolu.createParallelGroup(Alignment.LEADING)
						.addComponent(btnBrowseOutputDir, GroupLayout.DEFAULT_SIZE, 255, Short.MAX_VALUE)
						.addComponent(labelCiktiYolu))
					.addContainerGap())
		);
		gl_panel_cikti_yolu.setVerticalGroup(
			gl_panel_cikti_yolu.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_cikti_yolu.createSequentialGroup()
					.addContainerGap()
					.addComponent(labelCiktiYolu)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnBrowseOutputDir)
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		panel_cikti_yolu.setLayout(gl_panel_cikti_yolu);
		
		lblStaffWordsDosyaYolu = new JLabel(mResourceProps.getProperty("filePath"));
		lblStaffWordsDosyaYolu.setText(TestBot.defaultStopWordsFilePath);
		
		JButton btnBrowseStopWordsFile = new JButton(mResourceProps.getProperty("browse"));
		btnBrowseStopWordsFile.addActionListener(new BrowseStopWordsListener());
		
		GroupLayout gl_panel_stop_words = new GroupLayout(panel_stop_words);
		gl_panel_stop_words.setHorizontalGroup(
			gl_panel_stop_words.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_stop_words.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel_stop_words.createParallelGroup(Alignment.LEADING)
						.addComponent(btnBrowseStopWordsFile, GroupLayout.DEFAULT_SIZE, 255, Short.MAX_VALUE)
						.addComponent(lblStaffWordsDosyaYolu))
					.addContainerGap())
		);
		gl_panel_stop_words.setVerticalGroup(
			gl_panel_stop_words.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_stop_words.createSequentialGroup()
					.addGap(5)
					.addComponent(lblStaffWordsDosyaYolu)
					.addPreferredGap(ComponentPlacement.RELATED)
					.addComponent(btnBrowseStopWordsFile)
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		panel_stop_words.setLayout(gl_panel_stop_words);
		
		JLabel lblKategoriIsmi_1 = new JLabel(mResourceProps.getProperty("categoryName"));
		
		textFieldDeleteCategoryName = new JTextField();
		textFieldDeleteCategoryName.setColumns(10);
		
		JButton btnDeleteCategory = new JButton(mResourceProps.getProperty("delete"));
		btnDeleteCategory.addActionListener(new DeleteCategoryListener());
		
		GroupLayout gl_panel_kategori_sil = new GroupLayout(panel_kategori_sil);
		gl_panel_kategori_sil.setHorizontalGroup(
			gl_panel_kategori_sil.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_kategori_sil.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel_kategori_sil.createParallelGroup(Alignment.LEADING)
						.addComponent(btnDeleteCategory, GroupLayout.DEFAULT_SIZE, 255, Short.MAX_VALUE)
						.addGroup(gl_panel_kategori_sil.createSequentialGroup()
							.addComponent(lblKategoriIsmi_1)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(textFieldDeleteCategoryName, GroupLayout.DEFAULT_SIZE, 179, Short.MAX_VALUE)))
					.addContainerGap())
		);
		gl_panel_kategori_sil.setVerticalGroup(
			gl_panel_kategori_sil.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panel_kategori_sil.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_panel_kategori_sil.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblKategoriIsmi_1)
						.addComponent(textFieldDeleteCategoryName, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.UNRELATED)
					.addComponent(btnDeleteCategory)
					.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
		);
		panel_kategori_sil.setLayout(gl_panel_kategori_sil);
		contentPane.setLayout(gl_contentPane);
	}
	
	private void showMessage(String message)
	{
		 JOptionPane.showMessageDialog(null, message);
	}
	
	public void addLog(String log)
	{
		textAreaLog.append(log+'\n');
		//scroll bar show always last log
		textAreaLog.setCaretPosition(textAreaLog.getText().length());
		//logScroll.getVerticalScrollBar().addAdjustmentListener(e -> e.getAdjustable().setValue(e.getAdjustable().getMaximum()));
	}
	
	public void addOutput(String output)
	{
		textAreaOutput.append(output+'\n');
		textAreaOutput.setCaretPosition(textAreaOutput.getText().length());
	}

	@Override
	public void clearOutput() {
		textAreaOutput.setText("");
	}

	@Override
	public void setEnabledAllComponents(boolean enabled) {
		panel_cikti_yolu.setEnabled(enabled);
		panel_kategori_ekle.setEnabled(enabled);
		panel_kategori_sil.setEnabled(enabled);
		panel_stop_words.setEnabled(enabled);
		btnTrain.setEnabled(enabled);
		btnTest.setEnabled(enabled);
	}

	///Button Listeners
	class AddCategoryListener implements ActionListener
	{

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			String categoryName = textFieldAddCategoryName.getText();
			if(categoryName.equals(""))
			{
				showMessage(mErrorProps.getProperty("emptyCategoryName"));
				return;
			}
			
			if(mDocumentFiles == null)
			{
				showMessage(mErrorProps.getProperty("emptyDocuments"));
				return;
			}
			
			mListener.addCategoryPressed(categoryName,mDocumentFiles,rdbtnTrain.isSelected());

			mDocumentFiles = null;
		}
		
	}
	
	class BrowseDocumentListener implements ActionListener
	{

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub

			JFileChooser fc = new JFileChooser();
			fc.setMultiSelectionEnabled(true);
			int r = fc.showOpenDialog(contentPane);
			
			if(r == JFileChooser.APPROVE_OPTION){
				mDocumentFiles = fc.getSelectedFiles();
				for(File f:mDocumentFiles){
					addLog("Document ready : "+f.getName());
				}
			}
		}
		
	}
	
	class DeleteCategoryListener implements ActionListener
	{

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			String name = textFieldDeleteCategoryName.getText();
			if(name.equals(""))
			{
				showMessage(mErrorProps.getProperty("emptyName"));
				return;
			}
			
			mListener.deleteCategoryPressed(name,rdbtnTrain.isSelected());
		}
		
	}
	
	class BrowseStopWordsListener implements ActionListener
	{

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			JFileChooser fc = new JFileChooser();
			int r = fc.showOpenDialog(contentPane);
			
			if(r == JFileChooser.APPROVE_OPTION){
				File f = fc.getSelectedFile();
				mListener.stopWordsBrowsePressed(f);
				lblStaffWordsDosyaYolu.setText(f.getAbsolutePath());
				addLog("Stop words file selected : "+f.getName());
			}
			
		}
		
	}
	
	class BrowseOutputFileListener implements ActionListener
	{

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			JFileChooser fc = new JFileChooser();
			fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int r = fc.showOpenDialog(contentPane);
			
			if(r == JFileChooser.APPROVE_OPTION){
				String path = fc.getSelectedFile().getAbsolutePath();
				mListener.outputDirBrowsePressed(path);
				labelCiktiYolu.setText(path);
				addLog("Output path selected : "+path);
			}
		}
		
	}
	
	class TrainPressListener implements ActionListener
	{

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			mListener.onTrainPressed();
			addLog("Train Started");
		}
		
	}
	
	class TestPressListener implements ActionListener
	{

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			mListener.onTestPressed();
			addLog("Test Started");
		}
		
	}
}
