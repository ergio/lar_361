/******************************************************************************
 * Product: Adempiere ERP & CRM Smart Business Solution                       *
 * Copyright (C) 1999-2006 Adempiere, Inc. All Rights Reserved.               *
 * This program is free software; you can redistribute it and/or modify it    *
 * under the terms version 2 of the GNU General Public License as published   *
 * by the Free Software Foundation. This program is distributed in the hope   *
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the implied *
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.           *
 * See the GNU General Public License for more details.                       *
 * You should have received a copy of the GNU General Public License along    *
 * with this program; if not, write to the Free Software Foundation, Inc.,    *
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.                     *
 *****************************************************************************/
package ar.com.ergio.model;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Properties;
import java.util.logging.Level;

import javax.swing.JFrame;

import org.compiere.Adempiere;
import org.compiere.apps.ADialog;
import org.compiere.model.MBankAccount;
import org.compiere.model.MBankStatement;
import org.compiere.model.MBankStatementLine;
import org.compiere.model.MPayment;
import org.compiere.model.MSysConfig;
import org.compiere.util.CLogger;
import org.compiere.util.DB;
import org.compiere.util.Env;

public class TransaccionCuentaBancaria
{
    /** Logger */
    private static CLogger log = CLogger.getCLogger(TransaccionCuentaBancaria.class);

    /**
     * Constructor para evitar instanciación
     */
    private TransaccionCuentaBancaria() {}

    /**
     * Transferir todos los valores contemplados dentro de un Statement bancario,
     * de una cuenta origen a una cuenta destino.
     *
     * @param p_BankStatement_ID
     * @param p_From_C_BankAccount_ID
     * @param p_To_C_BankAccount_ID
     * @param p_Description
     * @param p_C_BPartner_ID
     * @param p_C_Currency_ID
     * @param p_StatementDate
     * @param p_DateAcct
     * @param ctx
     * @param trxName
     * @return Cantidad de lineas transferidas.
     */
    public static int transferirMovimientosEntreCuentas(final int p_BankStatement_ID,
            final int p_From_C_BankAccount_ID, final String p_Description,
            final int p_C_BPartner_ID, final Timestamp p_StatementDate, final Timestamp p_DateAcct,
            final Properties ctx, final String trxName)
    {
        // Contador de lineas tranferidas.
        int m_transferred = 0;

        final MBankStatement statement = new MBankStatement(ctx, p_BankStatement_ID, trxName);
        final MBankAccount mBankTo = new MBankAccount(ctx, statement.getBankAccount().get_ValueAsInt("CajaPrincipal_ID"), trxName);

        BigDecimal cashAmt = BigDecimal.ZERO;
        BigDecimal totalAmt = BigDecimal.ZERO;
        int p_C_Currency_ID = 0;
        int cash_transferred = 0;

        // Iterates over conciliated payments
        for (final MBankStatementLine line : statement.getLines(true))
        {
            // @fchiappano Si la linea ya fue tranferida, se pasa a la
            // siguiente.
            if (line.get_ValueAsBoolean("IsTransferred"))
                continue;

            final MPayment paymentFrom = new MPayment(ctx, line.getC_Payment_ID(), trxName);
            totalAmt = totalAmt.add(paymentFrom.getPayAmt());
            // @fchiappano Tomo la moneda utilizada en el pago.
            p_C_Currency_ID = paymentFrom.getC_Currency_ID();

            // Accumulates cash amounts
            if (paymentFrom.getTenderType().equals(MPayment.TENDERTYPE_Cash))
            {
                cashAmt = cashAmt.add(paymentFrom.getPayAmt());
                line.set_ValueOfColumn("IsTransferred", true);
                line.saveEx();
                cash_transferred ++;
                continue;
            }
            // Transfer other payments
            crearPago(p_DateAcct, p_StatementDate, mBankTo.getC_BankAccount_ID(), paymentFrom, p_C_BPartner_ID, p_Description,
                    ctx, trxName);

            // Mark bank statement line as transferred
            line.set_ValueOfColumn("IsTransferred", true);
            line.saveEx();
            m_transferred++;
        }

        // Transfer an accumulated cash amount
        if (cashAmt.compareTo(BigDecimal.ZERO) > 0)
        {
            final MPayment cashBankTo = new MPayment(ctx, 0, trxName);
            cashBankTo.setC_BankAccount_ID(mBankTo.getC_BankAccount_ID());
            cashBankTo.setDateAcct(p_DateAcct);
            cashBankTo.setDateTrx(p_StatementDate);
            cashBankTo.setTenderType(MPayment.TENDERTYPE_Cash);
            cashBankTo.setDescription(p_Description);
            cashBankTo.setC_BPartner_ID(p_C_BPartner_ID);
            cashBankTo.setC_Currency_ID(p_C_Currency_ID);
            cashBankTo.setPayAmt(cashAmt);
            cashBankTo.setOverUnderAmt(Env.ZERO);
            cashBankTo.setC_DocType_ID(true);
            cashBankTo.saveEx();
            cashBankTo.processIt(MPayment.DOCACTION_Complete);
            cashBankTo.saveEx();
        }

        debitarValores(statement, p_C_Currency_ID, p_C_BPartner_ID, totalAmt, ctx, trxName);

        // @fchiappano Marco el Statement original como transferido.
        statement.set_ValueOfColumn("Transferido", true);
        statement.saveEx();

        return m_transferred + cash_transferred;
    } // transferirMovimientosEntreCuentas

    /**
     * Transferir Valores a las Cuentas Bancarias correspondientes, según la forma de pago utilizada.
     * @param C_BankStatement_ID
     * @param ctx
     * @param trxName
     * @return
     */
    public static int transferirValoresPorFormaPago(final int p_BankStatement_ID, final String p_Description,
            final int p_C_BPartner_ID, final Timestamp p_StatementDate, final Timestamp p_DateAcct,
            final Properties ctx, final String trxName)
    {
        int m_transferred = 0;
        int cash_transferred = 0;
        int tipoPago = 0;

        // Chequeo si hay tarjetas de Debito y si las mismas tienen una cuenta bancaria configurada.
        tipoPago = comprobarCuentasPorFormaPago(p_BankStatement_ID, "LAR_Tarjeta_Debito_ID");
        if (tipoPago > 0)
        {
            final MLARTarjetaCredito debito = new MLARTarjetaCredito(ctx, tipoPago, trxName);
            final JFrame frame = new JFrame();
            frame.setIconImage(Adempiere.getImage16());
            ADialog.error(1, frame, "La tarjeta de debito " + debito.getDescription() + ", no posee una cuenta bancaria configurada.");
            return m_transferred;
        }

        // Chequeo si hay tarjetas de Credito y si las mismas tienen una cuenta bancaria configurada.
        tipoPago = comprobarCuentasPorFormaPago(p_BankStatement_ID, "LAR_Tarjeta_Credito_ID");
        if (tipoPago > 0)
        {
            final MLARTarjetaCredito credito = new MLARTarjetaCredito(ctx, tipoPago, trxName);
            final JFrame frame = new JFrame();
            frame.setIconImage(Adempiere.getImage16());
            ADialog.error(1, frame, "La tarjeta de credito " + credito.getDescription() + ", no posee una cuenta bancaria configurada.");
            return m_transferred;
        }

        // Chequeo si hay Tipos de Deposito y si los mismos tienen una cuenta bancaria configurada.
        tipoPago = comprobarCuentasPorFormaPago(p_BankStatement_ID, "LAR_Deposito_Directo_ID");
        if (tipoPago > 0)
        {
            final MLARTarjetaCredito deposito = new MLARTarjetaCredito(ctx, tipoPago, trxName);
            final JFrame frame = new JFrame();
            frame.setIconImage(Adempiere.getImage16());
            ADialog.error(1, frame, "El tipo de deposito directo " + deposito.getName() + ", no posee una cuenta bancaria configurada.");
            return m_transferred;
        }

        final MBankStatement statement = new MBankStatement(ctx, p_BankStatement_ID, trxName);

        BigDecimal totalAmt = Env.ZERO;
        BigDecimal cashAmt = BigDecimal.ZERO;
        int p_C_Currency_ID = 0;

        // Reccorro todas las lineas del statement para transferir a una nueva cuenta segun corresponda.
        for (MBankStatementLine linea : statement.getLines(true))
        {
            // Si la linea ya fue transferida, paso a la siguiente.
            if (linea.get_ValueAsBoolean("IsTransferred"))
                continue;

            // De la linea obtengo el pago
            final MPayment pago = (MPayment) linea.getC_Payment();

            // Tomo y guardo, la moneda utilizada en el pago para utilizarla posteriormente.
            p_C_Currency_ID = pago.getC_Currency_ID();

            if (pago.getTenderType().equals(MPayment.TENDERTYPE_Cash) &&
                    MSysConfig.getValue("LAR_TransfiereEfectivo_En_CierreDeCajas", Env.getAD_Client_ID(ctx)).equals("Y"))
            {
                cashAmt = cashAmt.add(pago.getPayAmt());
                linea.set_ValueOfColumn("IsTransferred", true);
                linea.saveEx();
                cash_transferred ++;
                continue;
            }
            else if (pago.getTenderType().equals(MPayment.TENDERTYPE_Check) ||
                    pago.getTenderType().equals("Z"))
            {
                final MBankAccount cuentaBancaria = new MBankAccount(ctx, statement.getC_BankAccount_ID(), trxName);
                final String sql = "UPDATE C_Payment"
                                 + "   SET C_BankAccount_ID='" + cuentaBancaria.get_ValueAsInt("CajaPrincipal_ID") + "'"
                                 + " WHERE C_Payment_ID='" + pago.getC_Payment_ID() + "'";
                DB.executeUpdate(sql, trxName);
                m_transferred ++;
                continue;
            }
            else if (pago.getTenderType().equals(MPayment.TENDERTYPE_CreditCard))
            {
                totalAmt = totalAmt.add(pago.getPayAmt());
                crearPago(p_DateAcct, p_StatementDate,
                        getCuentaPorFormaPago("LAR_Tarjeta_Credito_ID", pago.get_ValueAsInt("LAR_Tarjeta_Credito_ID")),
                        pago, p_C_BPartner_ID, p_Description, ctx, trxName);
                m_transferred ++;
                continue;
            }
            else if (pago.getTenderType().equals(MPayment.TENDERTYPE_DirectDebit))
            {
                totalAmt = totalAmt.add(pago.getPayAmt());
                crearPago(p_DateAcct, p_StatementDate,
                        getCuentaPorFormaPago("LAR_Tarjeta_Debito_ID", pago.get_ValueAsInt("LAR_Tarjeta_Debito_ID")),
                        pago, p_C_BPartner_ID, p_Description, ctx, trxName);
                m_transferred ++;
                continue;
            }
            else if (pago.getTenderType().equals(MPayment.TENDERTYPE_DirectDeposit))
            {
                totalAmt = totalAmt.add(pago.getPayAmt());
                crearPago(p_DateAcct, p_StatementDate,
                        getCuentaPorFormaPago("LAR_Desposito_Directo_ID", pago.get_ValueAsInt("LAR_Deposito_Directo_ID")),
                        pago, p_C_BPartner_ID, p_Description, ctx, trxName);
                m_transferred ++;
                continue;
            }
        }

        if (cashAmt.compareTo(BigDecimal.ZERO) > 0)
        {
            final MPayment cashBankTo = new MPayment(ctx, 0, trxName);
            final MBankAccount cuentaBancaria = new MBankAccount(ctx, statement.getC_BankAccount_ID(), trxName);
            cashBankTo.setC_BankAccount_ID(cuentaBancaria.get_ValueAsInt("CajaPrincipal_ID"));
            cashBankTo.setDateAcct(p_DateAcct);
            cashBankTo.setDateTrx(p_StatementDate);
            cashBankTo.setTenderType(MPayment.TENDERTYPE_Cash);
            cashBankTo.setDescription(p_Description);
            cashBankTo.setC_BPartner_ID(p_C_BPartner_ID);
            cashBankTo.setC_Currency_ID(p_C_Currency_ID);
            cashBankTo.setPayAmt(cashAmt);
            cashBankTo.setOverUnderAmt(Env.ZERO);
            cashBankTo.setC_DocType_ID(true);
            cashBankTo.setIsReceipt(true);
            cashBankTo.saveEx();
            cashBankTo.processIt(MPayment.DOCACTION_Complete);
            cashBankTo.saveEx();

            // Sumo el cashAmt al totalAmt para debitar el total de los valores posteriormente.
            totalAmt = totalAmt.add(cashAmt);
        }

        if (m_transferred > 0 || cash_transferred > 0)
        {
            debitarValores(statement, p_C_Currency_ID, p_C_BPartner_ID, totalAmt, ctx, trxName);

            statement.set_ValueOfColumn("Transferido", true);
            statement.saveEx();
        }

        return m_transferred + cash_transferred;
    } // transferirValoresPorFormaPago

    /**
     * Obtener la cuenta bancaria configurada, según la forma de pago.
     * @param ctx
     * @param trxName
     * @return
     */
    private static int getCuentaPorFormaPago(final String nombreColumna, final int tipoPago_ID)
    {
        int cuenta = 0;

        // Busco las cuentas bancarias segun la forma de pago.
        String sql = "SELECT C_BankAccount_ID"
                   + "  FROM LAR_TenderType_BankAccount"
                   + " WHERE " + nombreColumna + "=?";

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try
        {
            pstmt = DB.prepareStatement(sql, null);
            pstmt.setInt(1, tipoPago_ID);
            rs = pstmt.executeQuery();
            if (rs.next())
                cuenta = rs.getInt("C_BankAccount_ID");
        }
        catch (SQLException eSql)
        {
            log.log(Level.SEVERE, sql, eSql);
        }
        finally
        {
            DB.close(rs, pstmt);
            rs = null;
            pstmt = null;
        }
        return cuenta;
    } // getCuentaPorFormaPago

    /**
     * Crear un pago.
     *
     * @param p_DateAcct
     * @param p_StatementDate
     * @param bankAccount_ID
     * @param paymentFrom
     * @param p_C_BPartner_ID
     * @param p_C_Currency_ID
     * @param p_Description
     * @param ctx
     * @param trxName
     */
    private static void crearPago(final Timestamp p_DateAcct, final Timestamp p_StatementDate,
            final int bankAccount_ID, final MPayment paymentFrom, final int p_C_BPartner_ID,
            final String p_Description, final Properties ctx, final String trxName)
    {
        final MPayment payment = new MPayment(ctx, 0, trxName);
        payment.setC_BankAccount_ID(bankAccount_ID);
        payment.setDateAcct(p_DateAcct);
        payment.setDateTrx(p_StatementDate);
        payment.setTenderType(paymentFrom.getTenderType());

        // Obtener la Tarjeta de Credito del pago original.
        int tarjeta = paymentFrom.get_ValueAsInt("LAR_Tarjeta_Credito_ID");
        payment.set_ValueOfColumn("LAR_Tarjeta_Credito_ID", tarjeta != 0 ? tarjeta : null);
        // Obtener la Tarjeta de Debito del pago original.
        tarjeta = paymentFrom.get_ValueAsInt("LAR_Tarjeta_Debito_ID");
        payment.set_ValueOfColumn("LAR_Tarjeta_Debito_ID", tarjeta != 0 ? tarjeta : null);
        // Obtener el Deposito Directo del pago original.
        tarjeta = paymentFrom.get_ValueAsInt("LAR_Deposito_Directo_ID");
        payment.set_ValueOfColumn("LAR_Deposito_Directo_ID", tarjeta != 0 ? tarjeta : null);

        payment.setDescription(p_Description);
        payment.setC_BPartner_ID(p_C_BPartner_ID);
        payment.setC_Currency_ID(paymentFrom.getC_Currency_ID());
        payment.setPayAmt(paymentFrom.getPayAmt());
        payment.setOverUnderAmt(Env.ZERO);
        payment.setC_DocType_ID(true);
        payment.setIsReceipt(true);
        payment.saveEx();
        payment.processIt(MPayment.DOCACTION_Complete);
        payment.saveEx();
    } // crearPago

    /**
     * Crear pago y nuevo Statement que debita los valores transferidos de la cuenta origen.
     *
     * @param statement
     * @param p_C_Currency_ID
     * @param totalAmt
     * @param ctx
     * @param trxName
     */
    private static void debitarValores(final MBankStatement statement, final int p_C_Currency_ID, final int p_C_BPartner_ID,
            final BigDecimal totalAmt, final Properties ctx, final String trxName)
    {
        // Pago que debita los valores transferidos de la cuenta.
        final MPayment paymentBankFrom = new MPayment(ctx, 0, trxName);
        paymentBankFrom.setC_BankAccount_ID(statement.getC_BankAccount_ID());
        paymentBankFrom.setDateAcct(new Timestamp(System.currentTimeMillis()));
        paymentBankFrom.setDateTrx(new Timestamp(System.currentTimeMillis()));
        paymentBankFrom.setTenderType(MPayment.TENDERTYPE_DirectDeposit);
        paymentBankFrom.setDescription("Pago en concepto de Transferencia de valores.");
        paymentBankFrom.setC_BPartner_ID(p_C_BPartner_ID);
        paymentBankFrom.setC_Currency_ID(p_C_Currency_ID);
        paymentBankFrom.setPayAmt(totalAmt);
        paymentBankFrom.setOverUnderAmt(Env.ZERO);
        paymentBankFrom.setIsReconciled(true);
        paymentBankFrom.setC_DocType_ID(false);
        paymentBankFrom.saveEx();
        paymentBankFrom.processIt(MPayment.DOCACTION_Complete);
        paymentBankFrom.saveEx();

        final MBankStatement newStmt = new MBankStatement(ctx, 0, trxName);
        newStmt.setC_BankAccount_ID(statement.getC_BankAccount_ID());
        newStmt.setName("Compensacion Transferencia");
        newStmt.setDocStatus(MBankStatement.DOCSTATUS_Drafted);
        // @fchiappano si el Statement original, Es un cierre de caja,
        // marco el nuevo Statement como cierre de caja tambien.
        if (statement.get_ValueAsBoolean("EsCierreCaja"))
        {
            newStmt.set_ValueOfColumn("EsCierreCaja", true);
            // Seteo el Saldo inicial en 0, para que no genere errores en
            // futuros calculos.
            newStmt.set_ValueOfColumn("SaldoInicial", Env.ZERO);
        }
        // @fchiappano Marco el nuevo Statement como transferido.
        newStmt.set_ValueOfColumn("Transferido", true);
        newStmt.saveEx();
        final MBankStatementLine newLine = new MBankStatementLine(newStmt);
        newLine.setC_Currency_ID(p_C_Currency_ID);
        newLine.setC_Payment_ID(paymentBankFrom.getC_Payment_ID());
        newLine.setLine(10);
        newLine.set_ValueOfColumn("IsTransferred", true);
        newLine.set_ValueOfColumn("TrxAmt", totalAmt.negate());
        newLine.set_ValueOfColumn("StmtAmt", totalAmt.negate());
        newLine.saveEx();
        // process statement
        newStmt.processIt(MBankStatement.DOCACTION_Complete);
        newStmt.saveEx();
    } // debitarValores

    /**
     * Comprobar, si alguna forma de pago no posee cuenta bancaria configurada.
     * 
     * @param c_BankStatement_ID
     * @param nombreColumna
     * @return 0 si la configuracion esta correcta; formaPago_ID si es que la
     *         forma de pago no posee una cuenta bancaria configurada.
     */
    private static int comprobarCuentasPorFormaPago(final int c_BankStatement_ID, final String nombreColumna)
    {
        String sql = "SELECT DISTINCT(p." + nombreColumna + ")"
                   + "  FROM C_BankStatementLine sl JOIN C_Payment p ON sl.C_Payment_ID = p.C_Payment_ID"
                   + " WHERE sl.C_BankStatement_ID=?";

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try
        {
            pstmt = DB.prepareStatement(sql, null);
            pstmt.setInt(1, c_BankStatement_ID);
            rs = pstmt.executeQuery();

            while (rs.next())
            {
                if (rs.getInt(1) != 0)
                {
                    final int tipoPago_ID = rs.getInt(1);
                    pstmt = null;
                    rs = null;

                    sql = "SELECT LAR_TenderType_BankAccount_ID"
                        + "  FROM LAR_TenderType_BankAccount"
                        + " WHERE " + nombreColumna + "=?";

                    pstmt = DB.prepareStatement(sql, null);
                    pstmt.setInt(1, tipoPago_ID);
                    rs = pstmt.executeQuery();

                    if (!rs.next())
                        return tipoPago_ID;
                }
            }
        }
        catch (SQLException eSql)
        {
            log.log(Level.SEVERE, sql, eSql);
        }
        finally
        {
            DB.close(rs, pstmt);
            rs = null;
            pstmt = null;
        }

        return 0;
    } // comprobarCuentasPorFormaPago

} // TransaccionCuentaBancaria