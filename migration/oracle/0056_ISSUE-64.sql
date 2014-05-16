-- 29/04/2014 17:06:36 ART
-- Leimat #12 - LAR #64: Impresión de Remitos por CF
ALTER TABLE C_POS ADD IsShipment CHAR(1) DEFAULT 'N' NOT NULL
;

-- 13/05/2014 22:46 ART
-- Leimat #12 - LAR #64: Impresión de Remitos por CF
ALTER TABLE C_POS ADD C_DocTypeOnCredit_ID NUMERIC(10)
;

-- 13/05/2014 22:46 ART
-- Leimat #12 - LAR #64: Impresión de Remitos por CF
ALTER TABLE C_POS ADD CONSTRAINT cpos_cdoctypeshipment
    FOREIGN KEY (C_DocTypeOnCredit_ID) REFERENCES C_DocType (C_DocType_ID)
;

-- 21/03/2014 10:46:44 ART
-- Leimat #12: Impresión de Remitos por CF
INSERT INTO AD_Ref_List (AD_Ref_List_ID,AD_Reference_ID,EntityType,Value,Name,Updated,Created,CreatedBy,UpdatedBy,AD_Org_ID,IsActive,AD_Client_ID) VALUES (3000047,3000001,'LAR','R','Shipment',TO_DATE('2014-03-21 10:46:37','YYYY-MM-DD HH24:MI:SS'),TO_DATE('2014-03-21 10:46:37','YYYY-MM-DD HH24:MI:SS'),100,100,0,'Y',0)
;

-- 21/03/2014 10:46:44 ART
-- Leimat #12: Impresión de Remitos por CF
INSERT INTO AD_Ref_List_Trl (AD_Language,AD_Ref_List_ID, Description,Name, IsTranslated,AD_Client_ID,AD_Org_ID,Created,Createdby,Updated,UpdatedBy) SELECT l.AD_Language,t.AD_Ref_List_ID, t.Description,t.Name, 'N',t.AD_Client_ID,t.AD_Org_ID,t.Created,t.Createdby,t.Updated,t.UpdatedBy FROM AD_Language l, AD_Ref_List t WHERE l.IsActive='Y' AND l.IsSystemLanguage='Y' AND l.IsBaseLanguage='N' AND t.AD_Ref_List_ID=3000047 AND NOT EXISTS (SELECT * FROM AD_Ref_List_Trl tt WHERE tt.AD_Language=l.AD_Language AND tt.AD_Ref_List_ID=t.AD_Ref_List_ID)
;

-- 21/03/2014 10:54:39 ART
-- Leimat #12: Impresión de Remitos por CF
UPDATE AD_Ref_List_Trl SET Name='Remito',Updated=TO_DATE('2014-03-21 10:54:39','YYYY-MM-DD HH24:MI:SS'),UpdatedBy=100 WHERE AD_Ref_List_ID=3000047 AND AD_Language='es_AR'
;

-- 23/03/2014 19:44:57 ART
-- Leimat #12: Impresión de Remitos por CF
INSERT INTO AD_Process (AD_Process_ID,IsDirectPrint,IsReport,AccessLevel,IsBetaFunctionality,IsServerProcess,ShowHelp,EntityType,Statistic_Seconds,Statistic_Count,Classname,CopyFromProcess,Value,Name,AD_Org_ID,AD_Client_ID,Created,CreatedBy,Updated,UpdatedBy,IsActive) VALUES (3000071,'N','N','3','N','N','Y','LAR',23329,2005,'ar.com.ergio.process.ShipmentFiscalPrinting','N','LAR_ShipmentFiscalPrinting','LAR_ShipmentFiscalPrinting',0,0,TO_DATE('2014-03-23 19:44:56','YYYY-MM-DD HH24:MI:SS'),100,TO_DATE('2014-03-23 19:44:56','YYYY-MM-DD HH24:MI:SS'),100,'Y')
;

-- 23/03/2014 19:44:57 ART
-- Leimat #12: Impresión de Remitos por CF
INSERT INTO AD_Process_Trl (AD_Language,AD_Process_ID, Help,Description,Name, IsTranslated,AD_Client_ID,AD_Org_ID,Created,Createdby,Updated,UpdatedBy) SELECT l.AD_Language,t.AD_Process_ID, t.Help,t.Description,t.Name, 'N',t.AD_Client_ID,t.AD_Org_ID,t.Created,t.Createdby,t.Updated,t.UpdatedBy FROM AD_Language l, AD_Process t WHERE l.IsActive='Y' AND l.IsSystemLanguage='Y' AND l.IsBaseLanguage='N' AND t.AD_Process_ID=3000071 AND NOT EXISTS (SELECT * FROM AD_Process_Trl tt WHERE tt.AD_Language=l.AD_Language AND tt.AD_Process_ID=t.AD_Process_ID)
;

-- 23/04/2014 17:06:36 ART
-- Leimat #12 - LAR #64: Impresión de Remitos por CF
INSERT INTO AD_WF_Node (AD_WF_Node_ID,Cost,DynPriorityChange,Priority,DocAction,Duration,EntityType,IsCentrallyMaintained,JoinElement,Limit,WaitTime,WorkingTime,XPosition,YPosition,WaitingTime,AD_Process_ID,AD_Workflow_ID,Action,SplitElement,Value,Name,UpdatedBy,Created,AD_Org_ID,CreatedBy,IsActive,Updated,AD_Client_ID) VALUES (3000007,0,0,0,'CO',0,'LAR','Y','X',0,0,0,0,0,0,3000071,117,'P','X','(DocFiscalPrinting)','LAR_ShipmentFiscalPrinting',100,TO_DATE('2014-04-23 17:06:35','YYYY-MM-DD HH24:MI:SS'),0,100,'Y',TO_DATE('2014-04-23 17:06:35','YYYY-MM-DD HH24:MI:SS'),0)
;

-- 23/04/2014 17:06:36 ART
-- Leimat #12 - LAR #64: Impresión de Remitos por CF
INSERT INTO AD_WF_Node_Trl (AD_Language,AD_WF_Node_ID, Help,Name,Description, IsTranslated,AD_Client_ID,AD_Org_ID,Created,Createdby,Updated,UpdatedBy) SELECT l.AD_Language,t.AD_WF_Node_ID, t.Help,t.Name,t.Description, 'N',t.AD_Client_ID,t.AD_Org_ID,t.Created,t.Createdby,t.Updated,t.UpdatedBy FROM AD_Language l, AD_WF_Node t WHERE l.IsActive='Y' AND l.IsSystemLanguage='Y' AND l.IsBaseLanguage='N' AND t.AD_WF_Node_ID=3000007 AND NOT EXISTS (SELECT * FROM AD_WF_Node_Trl tt WHERE tt.AD_Language=l.AD_Language AND tt.AD_WF_Node_ID=t.AD_WF_Node_ID)
;

-- 29/04/2014 17:23:21 ART
-- Leimat #12 - LAR #64: Impresión de Remitos por CF
INSERT INTO AD_Element (AD_Element_ID,ColumnName,EntityType,Name,PrintName,AD_Client_ID,Created,Updated,IsActive,CreatedBy,UpdatedBy,AD_Org_ID) VALUES (3000160,'isshipment','LAR','isshipment','isshipment',0,TO_DATE('2014-04-29 17:23:21','YYYY-MM-DD HH24:MI:SS'),TO_DATE('2014-04-29 17:23:21','YYYY-MM-DD HH24:MI:SS'),'Y',100,100,0)
;

-- 29/04/2014 17:23:21 ART
-- Leimat #12 - LAR #64: Impresión de Remitos por CF
INSERT INTO AD_Element_Trl (AD_Language,AD_Element_ID, Help,PO_Description,PO_Help,Name,Description,PrintName,PO_PrintName,PO_Name, IsTranslated,AD_Client_ID,AD_Org_ID,Created,Createdby,Updated,UpdatedBy) SELECT l.AD_Language,t.AD_Element_ID, t.Help,t.PO_Description,t.PO_Help,t.Name,t.Description,t.PrintName,t.PO_PrintName,t.PO_Name, 'N',t.AD_Client_ID,t.AD_Org_ID,t.Created,t.Createdby,t.Updated,t.UpdatedBy FROM AD_Language l, AD_Element t WHERE l.IsActive='Y' AND l.IsSystemLanguage='Y' AND l.IsBaseLanguage='N' AND t.AD_Element_ID=3000160 AND NOT EXISTS (SELECT * FROM AD_Element_Trl tt WHERE tt.AD_Language=l.AD_Language AND tt.AD_Element_ID=t.AD_Element_ID)
;

-- 29/04/2014 17:23:22 ART
-- Leimat #12 - LAR #64: Impresión de Remitos por CF
INSERT INTO AD_Column (AD_Column_ID,AD_Table_ID,EntityType,Version,IsMandatory,IsTranslated,IsIdentifier,IsParent,FieldLength,IsSelectionColumn,AD_Reference_ID,IsKey,AD_Element_ID,IsEncrypted,IsUpdateable,IsAlwaysUpdateable,Name,ColumnName,CreatedBy,Updated,AD_Client_ID,AD_Org_ID,IsActive,Created,UpdatedBy) VALUES (3000878,748,'LAR',0,'Y','N','N','N',1,'N',20,'N',3000160,'N','Y','N','isshipment','isshipment',100,TO_DATE('2014-04-29 17:23:21','YYYY-MM-DD HH24:MI:SS'),0,0,'Y',TO_DATE('2014-04-29 17:23:21','YYYY-MM-DD HH24:MI:SS'),100)
;

-- 29/04/2014 17:23:22 ART
-- Leimat #12 - LAR #64: Impresión de Remitos por CF
INSERT INTO AD_Column_Trl (AD_Language,AD_Column_ID, Name, IsTranslated,AD_Client_ID,AD_Org_ID,Created,Createdby,Updated,UpdatedBy) SELECT l.AD_Language,t.AD_Column_ID, t.Name, 'N',t.AD_Client_ID,t.AD_Org_ID,t.Created,t.Createdby,t.Updated,t.UpdatedBy FROM AD_Language l, AD_Column t WHERE l.IsActive='Y' AND l.IsSystemLanguage='Y' AND l.IsBaseLanguage='N' AND t.AD_Column_ID=3000878 AND NOT EXISTS (SELECT * FROM AD_Column_Trl tt WHERE tt.AD_Language=l.AD_Language AND tt.AD_Column_ID=t.AD_Column_ID)
;

-- 29/04/2014 17:27:12 ART
-- Leimat #12 - LAR #64: Impresión de Remitos por CF
UPDATE AD_Element SET ColumnName='IsShipment', Name='IsShipment', PrintName='IsShipment',Updated=TO_DATE('2014-04-29 17:27:12','YYYY-MM-DD HH24:MI:SS'),UpdatedBy=100 WHERE AD_Element_ID=3000160
;

-- 29/04/2014 17:27:12 ART
-- Leimat #12 - LAR #64: Impresión de Remitos por CF
UPDATE AD_Element_Trl SET IsTranslated='N' WHERE AD_Element_ID=3000160
;

-- 29/04/2014 17:27:12 ART
-- Leimat #12 - LAR #64: Impresión de Remitos por CF
UPDATE AD_Column SET ColumnName='IsShipment', Name='IsShipment', Description=NULL, Help=NULL WHERE AD_Element_ID=3000160
;

-- 29/04/2014 17:27:12 ART
-- Leimat #12 - LAR #64: Impresión de Remitos por CF
UPDATE AD_Process_Para SET ColumnName='IsShipment', Name='IsShipment', Description=NULL, Help=NULL, AD_Element_ID=3000160 WHERE UPPER(ColumnName)='ISSHIPMENT' AND IsCentrallyMaintained='Y' AND AD_Element_ID IS NULL
;

-- 29/04/2014 17:27:12 ART
-- Leimat #12 - LAR #64: Impresión de Remitos por CF
UPDATE AD_Process_Para SET ColumnName='IsShipment', Name='IsShipment', Description=NULL, Help=NULL WHERE AD_Element_ID=3000160 AND IsCentrallyMaintained='Y'
;

-- 29/04/2014 17:27:12 ART
-- Leimat #12 - LAR #64: Impresión de Remitos por CF
UPDATE AD_Field SET Name='IsShipment', Description=NULL, Help=NULL WHERE AD_Column_ID IN (SELECT AD_Column_ID FROM AD_Column WHERE AD_Element_ID=3000160) AND IsCentrallyMaintained='Y'
;

-- 29/04/2014 17:27:12 ART
-- Leimat #12 - LAR #64: Impresión de Remitos por CF
UPDATE AD_PrintFormatItem pi SET PrintName='IsShipment', Name='IsShipment' WHERE IsCentrallyMaintained='Y' AND EXISTS (SELECT * FROM AD_Column c WHERE c.AD_Column_ID=pi.AD_Column_ID AND c.AD_Element_ID=3000160)
;

-- 29/04/2014 17:36:55 ART
-- Leimat #12 - LAR #64: Impresión de Remitos por CF
INSERT INTO AD_Field (IsEncrypted,AD_Field_ID,DisplayLength,IsDisplayed,IsSameLine,IsHeading,AD_Column_ID,IsFieldOnly,IsCentrallyMaintained,AD_Tab_ID,IsReadOnly,EntityType,Name,UpdatedBy,AD_Org_ID,Created,IsActive,AD_Client_ID,CreatedBy,Updated) VALUES ('N',3001589,1,'Y','N','N',3000878,'N','Y',676,'N','LAR','IsShipment',100,0,TO_DATE('2014-04-29 17:36:55','YYYY-MM-DD HH24:MI:SS'),'Y',0,100,TO_DATE('2014-04-29 17:36:55','YYYY-MM-DD HH24:MI:SS'))
;

-- 29/04/2014 17:36:55 ART
-- Leimat #12 - LAR #64: Impresión de Remitos por CF
INSERT INTO AD_Field_Trl (AD_Language,AD_Field_ID, Help,Description,Name, IsTranslated,AD_Client_ID,AD_Org_ID,Created,Createdby,Updated,UpdatedBy) SELECT l.AD_Language,t.AD_Field_ID, t.Help,t.Description,t.Name, 'N',t.AD_Client_ID,t.AD_Org_ID,t.Created,t.Createdby,t.Updated,t.UpdatedBy FROM AD_Language l, AD_Field t WHERE l.IsActive='Y' AND l.IsSystemLanguage='Y' AND l.IsBaseLanguage='N' AND t.AD_Field_ID=3001589 AND NOT EXISTS (SELECT * FROM AD_Field_Trl tt WHERE tt.AD_Language=l.AD_Language AND tt.AD_Field_ID=t.AD_Field_ID)
;

-- 29/04/2014 17:40:08 ART
-- Leimat #12 - LAR #64: Impresión de Remitos por CF
UPDATE AD_Field SET SeqNo=200,IsDisplayed='Y' WHERE AD_Field_ID=3001589
;

-- 29/04/2014 17:40:08 ART
-- Leimat #12 - LAR #64: Impresión de Remitos por CF
UPDATE AD_Field SET SeqNo=210,IsDisplayed='Y' WHERE AD_Field_ID=3000084
;

-- 29/04/2014 17:40:08 ART
-- Leimat #12 - LAR #64: Impresión de Remitos por CF
UPDATE AD_Field SET SeqNo=220,IsDisplayed='Y' WHERE AD_Field_ID=3000086
;

-- 29/04/2014 17:40:08 ART
-- Leimat #12 - LAR #64: Impresión de Remitos por CF
UPDATE AD_Field SET SeqNo=230,IsDisplayed='Y' WHERE AD_Field_ID=3000085
;

-- 29/04/2014 17:40:08 ART
-- Leimat #12 - LAR #64: Impresión de Remitos por CF
UPDATE AD_Field SET SeqNo=240,IsDisplayed='Y' WHERE AD_Field_ID=3000087
;

-- 29/04/2014 17:41:03 ART
-- Leimat #12 - LAR #64: Impresión de Remitos por CF
UPDATE AD_Field SET IsSameLine='Y',Updated=TO_DATE('2014-04-29 17:41:03','YYYY-MM-DD HH24:MI:SS'),UpdatedBy=100 WHERE AD_Field_ID=3001589
;

-- 29/04/2014 17:54:35 ART
-- Leimat #12 - LAR #64: Impresión de Remitos por CF
UPDATE AD_Field_Trl SET Name='Es De Remitos',Updated=TO_DATE('2014-04-29 17:54:35','YYYY-MM-DD HH24:MI:SS'),UpdatedBy=100 WHERE AD_Field_ID=3001589 AND AD_Language='es_AR'
;

-- 02/05/2014 15:30:27 ART
-- Leimat #12 - LAR #64: Impresión de Remitos por CF
UPDATE AD_Val_Rule SET Code='C_DocType.DocSubTypeSO=''WR'' OR C_DocType.DocSubTypeSO=''WP''',Updated=TO_DATE('2014-05-02 15:30:27','YYYY-MM-DD HH24:MI:SS'),UpdatedBy=100 WHERE AD_Val_Rule_ID=208
;

-- 15/05/2014 9:10:07 ART
-- Leimat #12 - LAR #64: Impresión de Remitos por CF
INSERT INTO AD_Element (AD_Element_ID,ColumnName,EntityType,Name,PrintName,AD_Client_ID,Created,Updated,IsActive,CreatedBy,UpdatedBy,AD_Org_ID) VALUES (3000164,'c_doctypeoncredit_id','LAR','c_doctypeoncredit_id','c_doctypeoncredit_id',0,TO_DATE('2014-05-15 09:10:00','YYYY-MM-DD HH24:MI:SS'),TO_DATE('2014-05-15 09:10:00','YYYY-MM-DD HH24:MI:SS'),'Y',100,100,0)
;

-- 15/05/2014 9:10:07 ART
-- Leimat #12 - LAR #64: Impresión de Remitos por CF
INSERT INTO AD_Element_Trl (AD_Language,AD_Element_ID, Help,PO_Description,PO_Help,Name,Description,PrintName,PO_PrintName,PO_Name, IsTranslated,AD_Client_ID,AD_Org_ID,Created,Createdby,Updated,UpdatedBy) SELECT l.AD_Language,t.AD_Element_ID, t.Help,t.PO_Description,t.PO_Help,t.Name,t.Description,t.PrintName,t.PO_PrintName,t.PO_Name, 'N',t.AD_Client_ID,t.AD_Org_ID,t.Created,t.Createdby,t.Updated,t.UpdatedBy FROM AD_Language l, AD_Element t WHERE l.IsActive='Y' AND l.IsSystemLanguage='Y' AND l.IsBaseLanguage='N' AND t.AD_Element_ID=3000164 AND NOT EXISTS (SELECT * FROM AD_Element_Trl tt WHERE tt.AD_Language=l.AD_Language AND tt.AD_Element_ID=t.AD_Element_ID)
;

-- 15/05/2014 9:10:07 ART
-- Leimat #12 - LAR #64: Impresión de Remitos por CF
INSERT INTO AD_Column (AD_Column_ID,AD_Table_ID,EntityType,Version,IsMandatory,IsTranslated,IsIdentifier,IsParent,FieldLength,IsSelectionColumn,AD_Reference_ID,IsKey,AD_Element_ID,IsEncrypted,IsUpdateable,IsAlwaysUpdateable,Name,ColumnName,CreatedBy,Updated,AD_Client_ID,AD_Org_ID,IsActive,Created,UpdatedBy) VALUES (3000882,748,'LAR',0,'N','N','N','N',10,'N',19,'N',3000164,'N','Y','N','c_doctypeoncredit_id','c_doctypeoncredit_id',100,TO_DATE('2014-05-15 09:10:00','YYYY-MM-DD HH24:MI:SS'),0,0,'Y',TO_DATE('2014-05-15 09:10:00','YYYY-MM-DD HH24:MI:SS'),100)
;

-- 15/05/2014 9:10:07 ART
-- Leimat #12 - LAR #64: Impresión de Remitos por CF
INSERT INTO AD_Column_Trl (AD_Language,AD_Column_ID, Name, IsTranslated,AD_Client_ID,AD_Org_ID,Created,Createdby,Updated,UpdatedBy) SELECT l.AD_Language,t.AD_Column_ID, t.Name, 'N',t.AD_Client_ID,t.AD_Org_ID,t.Created,t.Createdby,t.Updated,t.UpdatedBy FROM AD_Language l, AD_Column t WHERE l.IsActive='Y' AND l.IsSystemLanguage='Y' AND l.IsBaseLanguage='N' AND t.AD_Column_ID=3000882 AND NOT EXISTS (SELECT * FROM AD_Column_Trl tt WHERE tt.AD_Language=l.AD_Language AND tt.AD_Column_ID=t.AD_Column_ID)
;

-- 15/05/2014 9:18:08 ART
-- Leimat #12 - LAR #64: Impresión de Remitos por CF
UPDATE AD_Element SET ColumnName='C_DocTypeOnCredit_ID', Name='C_DocTypeOnCredit_ID', PrintName='C_DocTypeOnCredit_ID',Updated=TO_DATE('2014-05-15 09:18:08','YYYY-MM-DD HH24:MI:SS'),UpdatedBy=100 WHERE AD_Element_ID=3000164
;

-- 15/05/2014 9:18:08 ART
-- Leimat #12 - LAR #64: Impresión de Remitos por CF
UPDATE AD_Element_Trl SET IsTranslated='N' WHERE AD_Element_ID=3000164
;

-- 15/05/2014 9:18:08 ART
-- Leimat #12 - LAR #64: Impresión de Remitos por CF
UPDATE AD_Column SET ColumnName='C_DocTypeOnCredit_ID', Name='C_DocTypeOnCredit_ID', Description=NULL, Help=NULL WHERE AD_Element_ID=3000164
;

-- 15/05/2014 9:18:08 ART
-- Leimat #12 - LAR #64: Impresión de Remitos por CF
UPDATE AD_Process_Para SET ColumnName='C_DocTypeOnCredit_ID', Name='C_DocTypeOnCredit_ID', Description=NULL, Help=NULL, AD_Element_ID=3000164 WHERE UPPER(ColumnName)='C_DOCTYPEONCREDIT_ID' AND IsCentrallyMaintained='Y' AND AD_Element_ID IS NULL
;

-- 15/05/2014 9:18:08 ART
-- Leimat #12 - LAR #64: Impresión de Remitos por CF
UPDATE AD_Process_Para SET ColumnName='C_DocTypeOnCredit_ID', Name='C_DocTypeOnCredit_ID', Description=NULL, Help=NULL WHERE AD_Element_ID=3000164 AND IsCentrallyMaintained='Y'
;

-- 15/05/2014 9:18:08 ART
-- Leimat #12 - LAR #64: Impresión de Remitos por CF
UPDATE AD_Field SET Name='C_DocTypeOnCredit_ID', Description=NULL, Help=NULL WHERE AD_Column_ID IN (SELECT AD_Column_ID FROM AD_Column WHERE AD_Element_ID=3000164) AND IsCentrallyMaintained='Y'
;

-- 15/05/2014 9:18:08 ART
-- Leimat #12 - LAR #64: Impresión de Remitos por CF
UPDATE AD_PrintFormatItem pi SET PrintName='C_DocTypeOnCredit_ID', Name='C_DocTypeOnCredit_ID' WHERE IsCentrallyMaintained='Y' AND EXISTS (SELECT * FROM AD_Column c WHERE c.AD_Column_ID=pi.AD_Column_ID AND c.AD_Element_ID=3000164)
;

-- 15/05/2014 9:40:57 ART
-- Leimat #12 - LAR #64: Impresión de Remitos por CF
INSERT INTO AD_Reference (AD_Reference_ID,ValidationType,EntityType,IsOrderByValue,Name,AD_Client_ID,AD_Org_ID,CreatedBy,Updated,IsActive,Created,UpdatedBy) VALUES (3000029,'T','LAR','N','C_DocType LAR PDV',0,0,100,TO_DATE('2014-05-15 09:40:50','YYYY-MM-DD HH24:MI:SS'),'Y',TO_DATE('2014-05-15 09:40:50','YYYY-MM-DD HH24:MI:SS'),100)
;

-- 15/05/2014 9:40:57 ART
-- Leimat #12 - LAR #64: Impresión de Remitos por CF
INSERT INTO AD_Reference_Trl (AD_Language,AD_Reference_ID, Help,Name,Description, IsTranslated,AD_Client_ID,AD_Org_ID,Created,Createdby,Updated,UpdatedBy) SELECT l.AD_Language,t.AD_Reference_ID, t.Help,t.Name,t.Description, 'N',t.AD_Client_ID,t.AD_Org_ID,t.Created,t.Createdby,t.Updated,t.UpdatedBy FROM AD_Language l, AD_Reference t WHERE l.IsActive='Y' AND l.IsSystemLanguage='Y' AND l.IsBaseLanguage='N' AND t.AD_Reference_ID=3000029 AND NOT EXISTS (SELECT * FROM AD_Reference_Trl tt WHERE tt.AD_Language=l.AD_Language AND tt.AD_Reference_ID=t.AD_Reference_ID)
;

-- 15/05/2014 9:45:39 ART
-- Leimat #12 - LAR #64: Impresión de Remitos por CF
INSERT INTO AD_Ref_Table (IsValueDisplayed,WhereClause,AD_Table_ID,AD_Reference_ID,AD_Key,AD_Display,EntityType,CreatedBy,Created,Updated,UpdatedBy,AD_Client_ID,AD_Org_ID,IsActive) VALUES ('N','(C_DocType.DocSubTypeSO=''WR'' OR C_DocType.DocSubTypeSO=''WP'') AND C_DocType.AD_Client_ID=@#AD_Client_ID@',217,3000029,1501,1509,'LAR',100,TO_DATE('2014-05-15 09:45:39','YYYY-MM-DD HH24:MI:SS'),TO_DATE('2014-05-15 09:45:39','YYYY-MM-DD HH24:MI:SS'),100,0,0,'Y')
;

-- 15/05/2014 9:47:55 ART
-- Leimat #12 - LAR #64: Impresión de Remitos por CF
UPDATE AD_Column SET AD_Reference_Value_ID=3000029, IsMandatory='Y', FieldLength=22, AD_Reference_ID=18,Updated=TO_DATE('2014-05-15 09:47:55','YYYY-MM-DD HH24:MI:SS'),UpdatedBy=100 WHERE AD_Column_ID=3000882
;

-- 15/05/2014 9:48:41 ART
-- Leimat #12 - LAR #64: Impresión de Remitos por CF
INSERT INTO AD_Field (IsEncrypted,AD_Field_ID,DisplayLength,IsDisplayed,IsSameLine,IsHeading,AD_Column_ID,IsFieldOnly,IsCentrallyMaintained,AD_Tab_ID,IsReadOnly,EntityType,Name,UpdatedBy,AD_Org_ID,Created,IsActive,AD_Client_ID,CreatedBy,Updated) VALUES ('N',3001590,22,'Y','N','N',3000882,'N','Y',676,'N','LAR','C_DocTypeOnCredit_ID',100,0,TO_DATE('2014-05-15 09:48:31','YYYY-MM-DD HH24:MI:SS'),'Y',0,100,TO_DATE('2014-05-15 09:48:31','YYYY-MM-DD HH24:MI:SS'))
;

-- 15/05/2014 9:48:41 ART
-- Leimat #12 - LAR #64: Impresión de Remitos por CF
INSERT INTO AD_Field_Trl (AD_Language,AD_Field_ID, Help,Description,Name, IsTranslated,AD_Client_ID,AD_Org_ID,Created,Createdby,Updated,UpdatedBy) SELECT l.AD_Language,t.AD_Field_ID, t.Help,t.Description,t.Name, 'N',t.AD_Client_ID,t.AD_Org_ID,t.Created,t.Createdby,t.Updated,t.UpdatedBy FROM AD_Language l, AD_Field t WHERE l.IsActive='Y' AND l.IsSystemLanguage='Y' AND l.IsBaseLanguage='N' AND t.AD_Field_ID=3001590 AND NOT EXISTS (SELECT * FROM AD_Field_Trl tt WHERE tt.AD_Language=l.AD_Language AND tt.AD_Field_ID=t.AD_Field_ID)
;

-- 15/05/2014 9:49:41 ART
-- Leimat #12 - LAR #64: Impresión de Remitos por CF
UPDATE AD_Field SET SeqNo=140,IsDisplayed='Y' WHERE AD_Field_ID=3001590
;

-- 15/05/2014 9:53:02 ART
-- Leimat #12 - LAR #64: Impresión de Remitos por CF
UPDATE AD_Column_Trl SET Name='Tipo de Documento CtaCte',Updated=TO_DATE('2014-05-15 09:53:02','YYYY-MM-DD HH24:MI:SS'),UpdatedBy=100 WHERE AD_Column_ID=3000882 AND AD_Language='es_AR'
;

-- 15/05/2014 9:53:55 ART
-- Leimat #12 - LAR #64: Impresión de Remitos por CF
UPDATE AD_Field_Trl SET Name='Tipo de Documento CtaCte',Updated=TO_DATE('2014-05-15 09:53:55','YYYY-MM-DD HH24:MI:SS'),UpdatedBy=100 WHERE AD_Field_ID=3001590 AND AD_Language='es_AR'
;

-- 15/05/2014 9:54:18 ART
-- Leimat #12 - LAR #64: Impresión de Remitos por CF
UPDATE AD_Field_Trl SET Name='Tipo de Documento Contado',Updated=TO_DATE('2014-05-15 09:54:18','YYYY-MM-DD HH24:MI:SS'),UpdatedBy=100 WHERE AD_Field_ID=10811 AND AD_Language='es_AR'
;

-- 15/05/2014 10:13:29 ART
-- Leimat #12 - LAR #64: Impresión de Remitos por CF
UPDATE AD_Field SET DisplayLogic='@IsShipment@=''N''',Updated=TO_DATE('2014-05-15 10:13:29','YYYY-MM-DD HH24:MI:SS'),UpdatedBy=100 WHERE AD_Field_ID=3001590
;

-- 15/05/2014 10:17:42 ART
-- Leimat #12 - LAR #64: Impresión de Remitos por CF
UPDATE AD_Field SET SeqNo=140,IsDisplayed='Y' WHERE AD_Field_ID=3000742
;

-- 15/05/2014 10:17:42 ART
-- Leimat #12 - LAR #64: Impresión de Remitos por CF
UPDATE AD_Field SET SeqNo=150,IsDisplayed='Y' WHERE AD_Field_ID=3001590
;

-- 15/05/2014 10:19:56 ART
-- Leimat #12 - LAR #64: Impresión de Remitos por CF
UPDATE AD_Field_Trl SET Name='Tipo de Recibo',Updated=TO_DATE('2014-05-15 10:19:56','YYYY-MM-DD HH24:MI:SS'),UpdatedBy=100 WHERE AD_Field_ID=3000742 AND AD_Language='es_AR'
;

