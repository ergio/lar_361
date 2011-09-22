package ar.com.ergio.print.fiscal.view;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import org.compiere.apps.ADialog;
import org.compiere.apps.SwingWorker;
import org.compiere.apps.form.FormFrame;
import org.compiere.apps.form.FormPanel;
import org.compiere.grid.ed.VLookup;
import org.compiere.model.MRefList;
import org.compiere.swing.CButton;
import org.compiere.swing.CComboBox;
import org.compiere.swing.CLabel;
import org.compiere.swing.CPanel;
import org.compiere.util.DisplayType;
import org.compiere.util.Env;
import org.compiere.util.Msg;
import org.compiere.util.ValueNamePair;

import ar.com.ergio.model.FiscalDocumentPrint;
import ar.com.ergio.print.fiscal.action.FiscalCloseAction;
import ar.com.ergio.print.fiscal.action.FiscalPrinterAction;

public class FiscalPrinterControlPanel extends CPanel implements FormPanel{

	// Constantes
	
	private static final long serialVersionUID = -6703566239557862855L;

	private final AInfoFiscalPrinter infoFiscalPrinter = 
		new AInfoFiscalPrinter(
				null,
				Msg.parseTranslation(Env.getCtx(),"@FiscalPrinterControlPanel@"),
				"",
				JOptionPane.INFORMATION_MESSAGE
		);
	
	// Variables de instancia
	
	/** Frame contenedor */
	
	private FormFrame frame;
	
	/** Window no */
	
	private int windowNo;
		
	/** Interface de conexión con la impresora fiscal */
	
	private FiscalDocumentPrint iFiscalPrinter;
	
	/** Acción actual a procesar o en proceso */
	
	private FiscalPrinterAction actualAction;
	
	// Mensajes
	
	private static String MSG_PROCESSING;
	private static String MSG_PLEASEWAIT;
	private static String MSG_CLOSE;
	private static String MSG_FISCAL_CLOSE;
	private static String MSG_FISCAL_CLOSE_TYPE;
	private static String MSG_FISCAL_CONTROLLER;
	private static String MSG_FISCAL_PRINTER_CONTROL_PANEL;
	private static String FISCAL_CLOSE_TYPES_REF_NAME;

	
	// *********************************
	// 	   Panel principal
	// *********************************
	
	/** Panel principal */
	
	private CPanel mainPanel;
	
	/** Layout principal */
	
	private BorderLayout mainLayout = new BorderLayout();
	
	// *********************************
	// 	   Panel de cierre fiscal
	// *********************************
	
	/** Panel de comandos de cierre fiscal */
	
	private CPanel fiscalClosePanel;
	
	// Componentes del panel
	
	/** Tipo de cierre fiscal */
	
	public CComboBox comboFiscalCloseTypes;
	
	/** Combo con los controladores fiscales */
	
	public VLookup comboFiscalControllers;
	
	/** Botón para ejecutar el cierre */
	
	private CButton btnFiscalClose;
	
	// Labels
	
	private CLabel lblFiscalCloseType;
	private CLabel lblFiscalControllers;
	
	
	// *********************************
	// 	     Panel inferior
	// *********************************

	/** Panel inferior */
	
	private CPanel bottomPanel;
	
	/** Botón Cerrar  */
	
	private CButton btnCloseForm;

	// *********************************
	
	// Constructores
	
	public FiscalPrinterControlPanel(){
		initFiscalInterface();
		initMsgs();
	}

	// Heredados
	
	@Override
	public void dispose() {
		setVisible(false);
		getFrame().dispose();
	}

	@Override
	public void init(int WindowNo, FormFrame frame) {
		setFrame(frame);
		setWindowNo(WindowNo);
		try {
			jbInit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}	
	
	/**
	 * Inicializa la interface de conexión con la impresora fiscal
	 */
	private void initFiscalInterface(){
		setiFiscalPrinter(new FiscalDocumentPrint());
		addFiscalPrinterListeners();
	}
	
	/**
	 * Agregar los listeners a la interface de la impresora fiscal 
	 */
	private void addFiscalPrinterListeners() {
		getiFiscalPrinter().addDocumentPrintListener(infoFiscalPrinter);
		getiFiscalPrinter().setPrinterEventListener(infoFiscalPrinter);
		
		// Referenciar la impresora desde la ventana de info.
		infoFiscalPrinter.setFiscalDocumentPrint(getiFiscalPrinter());
	}
	
	/**
	 * Inicializo los mensajes
	 */
	private void initMsgs(){
		// Mensajes
		MSG_PROCESSING = getMsg("Processing");
		MSG_PLEASEWAIT = getMsg("PleaseWait");
		MSG_CLOSE = getMsg("Close");
		MSG_FISCAL_CLOSE = getMsg("FiscalClose");
		MSG_FISCAL_CLOSE_TYPE = getMsg("FiscalCloseType");
		MSG_FISCAL_CONTROLLER = Msg.getElement(Env.getCtx(), "C_Controlador_Fiscal_ID");
		MSG_FISCAL_PRINTER_CONTROL_PANEL = getMsg("FiscalPrinterControlPanel"); 
		// Nombres
		FISCAL_CLOSE_TYPES_REF_NAME = "Fiscal_Close_Types";
	}
		
	private void jbInit(){
		// Inicializar los componentes principales del form
		mainLayout = new BorderLayout();
		mainPanel = new CPanel();
		
		mainPanel.setLayout(mainLayout);
		getFrame().setContentPane(mainPanel);
		getFrame().setTitle(MSG_FISCAL_PRINTER_CONTROL_PANEL);
		// Crear e inicializar las áreas del panel de control  
		initAreas();
		// Agregarlas al panel principal
		addAreasToMainPanel();
	}
	
	/**
	 * Inicializo las áreas del panel de control
	 */
	private void initAreas(){
		initFiscalClosePanel();
		initBottomPanel();
	}
	
	/**
	 * Área de cierre fiscal
	 */
	private void initFiscalClosePanel(){
		// Inicializar el panel
		fiscalClosePanel = new CPanel();
		fiscalClosePanel.setBorder(BorderFactory.createTitledBorder(MSG_FISCAL_CLOSE));
		fiscalClosePanel.setLayout(new GridBagLayout());
		// Crear sus componentes y agregarlos al contenedor
		lblFiscalCloseType = new CLabel(MSG_FISCAL_CLOSE_TYPE);
		lblFiscalControllers = new CLabel(MSG_FISCAL_CONTROLLER);
		fiscalClosePanel.add(lblFiscalCloseType, new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 0, 5, 10), 0, 0));
		fiscalClosePanel.add(getComboFiscalCloseTypes(), new GridBagConstraints(1, 0, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 0, 5, 0), 0, 0));
		fiscalClosePanel.add(lblFiscalControllers, new GridBagConstraints(0, 1, 1, 1, 1, 1, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(5, 0, 5, 10), 0, 0));
		fiscalClosePanel.add(getComboFiscalControllers(), new GridBagConstraints(1, 1, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5, 0, 5, 0), 0, 0));
		fiscalClosePanel.add(getBtnFiscalClose(), new GridBagConstraints(0, 2, 2, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.SOUTH, new Insets(15, 0, 5, 0), 0, 0));
	}
	
	/**
	 * Área inferior
	 */
	private void initBottomPanel(){
		// inicializar el panel
		bottomPanel = new CPanel();
		bottomPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		// Crear sus componentes y agregarlos al contenedor
		bottomPanel.add(getBtnCloseForm());
	}
	
	/**
	 * Retornar o crear en caso que no exista el combo con los 
	 * tipos de cierres fiscales. 
	 * @return
	 */
	private CComboBox getComboFiscalCloseTypes(){
		if(comboFiscalCloseTypes == null){
			comboFiscalCloseTypes = new CComboBox(MRefList.getList(Env.getCtx(),MReference.getReferenceID(FISCAL_CLOSE_TYPES_REF_NAME), false));
			comboFiscalCloseTypes.setMandatory(true);
		}
		return comboFiscalCloseTypes;
	}
	
	/**
	 * Retornar o crear el lookup con los controladores fiscales
	 * @return
	 */
	private VLookup getComboFiscalControllers(){
		if(comboFiscalControllers == null){
			comboFiscalControllers = VComponentsFactory.VLookupFactory("C_Controlador_Fiscal_ID", "C_Controlador_Fiscal", getWindowNo(), DisplayType.TableDir);
			comboFiscalControllers.setMandatory(true);
		}
		return comboFiscalControllers;
	}
	
	
	/**
	 * Retornar o crear el botón que dispara el cierre fiscal
	 * @return
	 */
	private CButton getBtnFiscalClose(){
		if(btnFiscalClose == null){
			btnFiscalClose = new CButton(MSG_FISCAL_CLOSE,Env.getImageIcon("Process24.gif"));
			btnFiscalClose.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent arg0) {
					setActualAction(new FiscalCloseAction(getiFiscalPrinter(), null, ((ValueNamePair)getComboFiscalCloseTypes().getValue()).getValue(), (Integer)getComboFiscalControllers().getValue()));
					executeAction();
				}
			});
		}
		return btnFiscalClose;
	}
	
	/**
	 * Retornar o crear el botón de cierre de form
	 * @return
	 */
	private CButton getBtnCloseForm(){
		if(btnCloseForm == null){
			btnCloseForm = new CButton(MSG_CLOSE,Env.getImageIcon("End24.gif"));
			btnCloseForm.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					dispose();
				}
			});
		}
		return btnCloseForm;
	}
	
	/**
	 * Agregar las áreas del panel de control al panel principal
	 */
	private void addAreasToMainPanel(){
		mainPanel.add(fiscalClosePanel,BorderLayout.CENTER);
		mainPanel.add(bottomPanel,BorderLayout.SOUTH);
	}
	
	
	// Funciones del panel de control
	
	private void executeAction(){
		SwingWorker worker = new SwingWorker() {

			private String errorMsg = null;
			private String errorDesc = null;
			
			@Override
			public Object construct() {
				errorMsg = null;
				errorDesc = null;
				if(!getActualAction().execute()){
					errorMsg = getActualAction().getErrorMsg();
					errorDesc = getActualAction().getErrorDesc();
				}
				return errorMsg == null;
			}

			@Override
			public void finished() {
				boolean success = (Boolean)getValue();
				if(!success){
					if(errorDesc == null)
						errorMsg(errorMsg);
					else
						errorMsg(errorMsg, errorDesc);
				}
				getFrame().setBusy(false);
				mNormal();
			}
		};

		
		mWait();
		String waitMsg = MSG_PROCESSING + ", " + MSG_PLEASEWAIT;
		getFrame().setBusyMessage(waitMsg);
		getFrame().setBusyTimer(4);
		getFrame().setBusy(true);
		
		worker.start();
		
		// Mostrar ventana de la impresora fiscal
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				infoFiscalPrinter.setVisible(true);
			}
		});
	}
	
	protected String getMsg(String name) {
		return Msg.getMsg(Env.getCtx(), name);
	}
	
	protected void errorMsg(String msg) {
		errorMsg(msg,null);
	}
	
	protected void errorMsg(String msg, String subMsg) {
		ADialog.error(getWindowNo(),this,msg,subMsg);
	}
	
    private void mWait() {
    	getFrame().setCursor(new Cursor(Cursor.WAIT_CURSOR));
    }
    
    private void mNormal() {
    	getFrame().setCursor(Cursor.getDefaultCursor());
    }
	
	// Getters y Setters

	private void setFrame(FormFrame frame) {
		this.frame = frame;
	}

	private FormFrame getFrame() {
		return frame;
	}

	private void setWindowNo(int windowNo) {
		this.windowNo = windowNo;
	}

	private int getWindowNo() {
		return windowNo;
	}

	private void setiFiscalPrinter(FiscalDocumentPrint iFiscalPrinter) {
		this.iFiscalPrinter = iFiscalPrinter;
	}

	private FiscalDocumentPrint getiFiscalPrinter() {
		return iFiscalPrinter;
	}

	private void setActualAction(FiscalPrinterAction actualAction) {
		this.actualAction = actualAction;
	}

	private FiscalPrinterAction getActualAction() {
		return actualAction;
	}
}
