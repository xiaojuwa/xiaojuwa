#include "termparamwindow.h"

#include "ui_termparamwindow.h"



#include <QStandardItemModel>



#include <QMessageBox>



#include "HzToPy.h"



#include "ZItemDelegate.h"



#include "ZMessageBox.h"

#include"paramanage.h"

#include"selectlinedevframe.h"

#include"db_def/priv_func_def.h"

#include<QDateTime>

#include"waitdlg.h"

#include<math.h>

//#include<QS>

//返回的定值区号　serial_num line_no

//返回结果 32765    召唤的结果可忽略

//固定参数的定值区号

//dms_pro_mmi.X有的参数不在召唤列表中

//终端下分多个开关 这些参数属于开关 配网开关表中的微机保护线路号

//配网前置遥信定义表->通道 配网通道表->所属终端 可以得到开关与终端的关系

//配网通道表和所属终端可以认为是一一对应的

//消息总线多包接收

//终端id过滤



extern QString g_strLoginUserName;

string g_log_str;

char  g_log_buf[1024];

//bool g_bFinishedFlag=false;

//QMap<qint64,QVector<LineNoInfo_stru> > g_termId_lineNoIndexVec_map;

//#define  MENU_OPT_DEV_STAT_SHEET_EXEC 1

//#define  MENU_OPT_DEV_STAT_PARAM_CALL 2

TermParamWindow::TermParamWindow(QWidget *parent) :

    QMainWindow(parent),

    ui(new Ui::TermParamWindow)

{

    ui->setupUi(this);

    //add by lxf for ningxia

    readConf();

    //add end

    m_oper_timeout_slice    = 45;   //操作超时间隔(秒)



    //m_pDevCtrl          = NULL;



    m_term_dev_id       = 0L;

    m_term_xh_id        = 0L;



    m_start_time        = 0;

    m_reply_event       = 0;

    m_pProgressTimer    = NULL;



    m_paraRightMenu             = NULL;



    m_actionParaSelectAll       = NULL;

    m_actionParaSelectNone      = NULL;

    m_actionParaSelectReverse   = NULL;



    m_paraRightMenu_mul             = NULL;



    m_actionParaSelectAll_mul       = NULL;

    m_actionParaSelectNone_mul      = NULL;

    m_actionParaSelectReverse_mul   = NULL;

    m_actionPutInto_mul=NULL;

    m_pOperLabel    = NULL;

    m_pProgressBar  = NULL;

    m_pProgressDlg  = NULL;

    m_pThread=NULL;

    m_progress_idx  = 0;

    m_strCustodyName=g_strLoginUserName;

#ifdef HIDE_PARAM_MUL_OP

    ui->tabWidget_2->removeTab(2);

#endif



#ifdef HIDE_SHEET_TAB

    ui->tabWidget_2->removeTab(1);

#endif

    ui->tabWidget_2->setCurrentIndex(0);

    initMsg();

    initUi();

    init_mul();



    //initDevTree();



    m_pInitTimer = new QTimer( this );

    m_pInitTimer->setSingleShot( true );

    m_pInitTimer->start( 100 );

    connect( m_pInitTimer, SIGNAL( timeout() ), this, SLOT( OnInit() ) );



#ifdef _SOUTHGRID_PROJECT_

    /*ui->comboBox_Area->hide();

    ui->pushBtn_CallArea->hide();

    ui->pushBtn_AlterArea->hide();*/

#endif

    //add by lxf for ningxia

    //if(!g_strLoginUserName.contains("操作员"))

#if 0

    if(m_op_user_set.find(g_strLoginUserName)==m_op_user_set.end())

    {

        //QMessageBox::warning(this,"11111111111","22222222222222");

        ui->pushBtn_Write->setEnabled(false);;

        ui->pushBtn_Act->setEnabled(false);

    }

#endif



    //查看本机节点是否允许操作

    char hostname[64];

    gethostname(hostname,64);

    if(m_node_name_set.find(hostname)==m_node_name_set.end()&&m_node_name_set.size()>0)//不配置 所遇机器节点正常使用

    {

        setCtrlEnable(false);

        //qint64 resp_area=getLocalRespArea();

        // QList<qint64> termId_lst;

        //getLocalRespAreaTermId(termId_lst,resp_area,13510,"dms_terminal_info");

    }

    //add by lxf 根据总控台登录区域进行过滤

    qint64 resp_area=getLocalRespArea();

    //getLocalRespAreaTermId(m_localresptermId_lst,resp_area,13510,"dms_terminal_info");

    getLocalRespAreaTermId(m_localresptermId_lst,resp_area,13500,"dms_feeder_device");

    //add end

}



TermParamWindow::~TermParamWindow()

{

    StopProgressStep();



    if( m_pOperLabel != NULL )

    {

        delete m_pOperLabel;

        m_pOperLabel = NULL;

    }



    if( m_pProgressBar != NULL )

    {

        delete m_pProgressBar;

        m_pProgressBar = NULL;

    }



    delete ui;

    delete m_WarnMsg;

    m_pMessageBus->messageExit(0);

    DllReleaseAlarmOp(m_alarm_op);

}

void TermParamWindow::init_mul()

{



#ifndef SHOW_MUL_CALL_RESULT

    ui->pushBtn_Read_mul->hide();

#endif

    ui->pushBtn_act_mul->setEnabled(false);





    ui->pushBtn_CallArea_mul->setIcon( QIcon( tr(":/Resources/edit.ico") ) );

    ui->pushBtn_Read_mul->setIcon( QIcon( tr(":/Resources/up.ico") ) );









    ui->pushBtn_Write_mul->setIcon( QIcon( tr(":/Resources/down.ico") ) );





    ui->pushBtn_act_mul->setIcon( QIcon( tr(":/Resources/apply.ico") ) );



    m_treeWidget_width_mul = 300;

    QStringList lstColumnName;

    lstColumnName   << tr("终端名称")

                    <<tr("当前定值区")

                   << tr("召唤定值区")

                  #ifdef SHOW_MUL_CALL_RESULT

                   <<tr("召唤参数")

                 #endif

                  << tr("参数下发")

                  <<tr("参数激活")

                 <<tr("投退状态");





    QList< QStandardItem * >    listColumn;



    QString column_name;

    foreach( column_name, lstColumnName )

    {

        QStandardItem *pH = new QStandardItem( column_name );

        //QFont f = pH->font();

        //f.setPointSize( 12 );

        //pH->setFont( f );

        listColumn.append( pH );

    }



    QTableView  *pTableView=ui->tableView_term_mul;

    //添加表头

    //准备数据模型

    QStandardItemModel *pModel = new QStandardItemModel();

    for( int i = 0; i < listColumn.size(); i++ )

        pModel->setHorizontalHeaderItem( i, listColumn[i] );

    //利用setModel()方法将数据模型与QTableView绑定

    pTableView->setModel( pModel );

    for( int c = 0; c < pModel->columnCount(); c++ )

        pTableView->setColumnWidth( c, 200 );



    // pTableView->resizeColumnsToContents();

    //pTableView->update();



    //connect( pModel, SIGNAL(dataChanged(QModelIndex,QModelIndex)), this, SLOT(OnFieldChanged(QModelIndex,QModelIndex)) );



    QHeaderView *pHV = pTableView->horizontalHeader();

    if( pHV != NULL )

    {

        pHV->setContextMenuPolicy( Qt::CustomContextMenu );

        connect( pHV, SIGNAL(customContextMenuRequested(QPoint)), this, SLOT(clicked_paraRightMenu_mul(QPoint)) );

    }





    QIcon putinto_icon = QIcon( tr(":/Resources/x13510.bmp") );

    QIcon quit_icon   = QIcon( tr(":/Resources/x13502.bmp") );

    ui->comboBox_status->addItem(putinto_icon,tr("投入"),1);

    ui->comboBox_status->addItem(quit_icon,tr("退出"),0);



}



void TermParamWindow::on_paraSelectAll_mul(bool b)

{





    QStandardItemModel *pModel = NULL;



    pModel = (QStandardItemModel *)ui->tableView_term_mul->model();

    if( pModel == NULL ) return;



    for( int row = 0; row < pModel->rowCount(); row++ )

    {

        QStandardItem *pItem = pModel->item( row, MUL_TERM_COL_NAME );

        if( pItem == NULL ) continue;



        if(!pItem->isCheckable()) continue;

        if( pItem->checkState() != Qt::Checked )

        {

            pItem->setCheckState( Qt::Checked );

        }

    }

}



void TermParamWindow::on_paraSelectNone_mul(bool b)

{

    QStandardItemModel *pModel = NULL;



    pModel = (QStandardItemModel *)ui->tableView_term_mul->model();

    if( pModel == NULL ) return;



    for( int row = 0; row < pModel->rowCount(); row++ )

    {

        QStandardItem *pItem = pModel->item( row, MUL_TERM_COL_NAME );

        if( pItem == NULL ) continue;

        if(!pItem->isCheckable()) continue;

        if( pItem->checkState() != Qt::Unchecked )

        {

            pItem->setCheckState( Qt::Unchecked  );

        }

    }

}



void TermParamWindow::on_paraSelectReverse_mul(bool b)

{

    QStandardItemModel *pModel = NULL;



    pModel = (QStandardItemModel *)ui->tableView_term_mul->model();

    if( pModel == NULL ) return;



    for( int row = 0; row < pModel->rowCount(); row++ )

    {

        QStandardItem *pItem = pModel->item( row, MUL_TERM_COL_NAME );

        if( pItem == NULL ) continue;

        if(!pItem->isCheckable()) continue;

        if( pItem->checkState() != Qt::Unchecked )

        {

            pItem->setCheckState( Qt::Unchecked  );

        }

        else

        {

            pItem->setCheckState( Qt::Checked  );

        }

    }

}



void TermParamWindow::on_paraSelectPutInto_mul(bool b)

{

}

void TermParamWindow::OnInit()

{

    MyWaitCursor mwc;



    if( m_pInitTimer != NULL )

    {

        m_pInitTimer->stop();



        disconnect( m_pInitTimer, SIGNAL( timeout() ), this, SLOT( OnInit() ) );



        delete m_pInitTimer;

        m_pInitTimer = NULL;

    }



    //QProgressDialog dlg( this );

    //dlg.setWindowTitle( tr("请稍候") );

    //dlg.setLabelText( tr("正在加载中...") );

    //dlg.exec();



    initStatusBar();



    //initDevTreeLocate();



    init_cmb_term_brand();

    init_cmb_term_type();

    init_cmb_term_status();

    initTableWidget_paramFile();

    initTableWidget_Term();

#if 0

    inittableView_Para( ui->tableView_Para0, false );

    connect( ui->tableView_Para0, SIGNAL(pressed(const QModelIndex &)), this, SLOT(onTableViewPara0Pressed(const QModelIndex &)) );



    inittableView_YxPara( ui->tableView_Para1 );

    connect( ui->tableView_Para1, SIGNAL(pressed(const QModelIndex &)), this, SLOT(onTableViewPara1Pressed(const QModelIndex &)) );



    inittableView_Para( ui->tableView_Para2, true );

    connect( ui->tableView_Para2, SIGNAL(pressed(const QModelIndex &)), this, SLOT(onTableViewPara2Pressed(const QModelIndex &)) );

#endif

    createParaRightMenu();

    createParaRightMenu_mul();



    //初始化缓存数据

    m_rtdbOper.loadCacheInfo_TermParam();

    //m_rtdbOper.loadAllCacheInfo( true );



    initDevTree( false );

    initDevTreeLocate( false );

    initDevTreeLocate_mul( false );



    init_tab();



    this->setEnabled( true );



    BeforeCallArea();

}



void TermParamWindow::doTableViewParaPressed( QTableView *pTableView, const QModelIndex &index )

{

    if( pTableView == NULL ) return;



    QStandardItemModel *pModel = (QStandardItemModel *)pTableView->model();

    if( pModel == NULL ) return;



    int row = index.row();

    int col = index.column();



    switch( col )

    {

    case 0:

    {

        QStandardItem * pItem = pModel->item( row, col );   //是否选择

        if( pItem != NULL && pItem->isCheckable() )

        {

            Qt::CheckState pre_state = pItem->checkState();

            Qt::CheckState new_state = ( ( pre_state == Qt::Unchecked ) ? Qt::Checked : Qt::Unchecked );



            pItem->setCheckState( new_state );

        }

    }

        break;

    default:

        break;

    }

}



void TermParamWindow::onTableViewPara0Pressed( const QModelIndex &index )

{

    doTableViewParaPressed( ui->tableView_Para0, index );

}



void TermParamWindow::onTableViewPara1Pressed( const QModelIndex &index )

{

    doTableViewParaPressed( ui->tableView_Para1, index );

}



void TermParamWindow::onTableViewPara2Pressed( const QModelIndex &index )

{

    doTableViewParaPressed( ui->tableView_Para2, index );

}



void TermParamWindow::initUi()

{

    setWindowIcon( QIcon( tr(":/Resources/f18.ico") ) );



    m_treeWidget_width = 300;





    setWidText(g_strLoginUserName);



    connect( ui->comboBox_Line, SIGNAL(currentIndexChanged(int)), this, SLOT(OnLineIndexChanged(int)) );

    connect( ui->comboBox_Area, SIGNAL(currentIndexChanged(int)), this, SLOT(OnAreaIndexChanged(int)) );



    ui->pushBtn_CallArea->setIcon( QIcon( tr(":/Resources/edit.ico") ) );

    ui->pushBtn_CallArea->setToolTip( tr( "召唤定值区" ) );

    //connect( ui->pushBtn_CallArea, SIGNAL(clicked()), this, SLOT( on_pushBtn_CallArea_clicked() ) );



    ui->pushBtn_AlterArea->setIcon( QIcon( tr(":/Resources/paint.ico") ) );

    ui->pushBtn_AlterArea->setToolTip( tr( "切换定值分区" ) );

    ui->pushBtn_AlterArea->setEnabled(false);//0725

    //connect( ui->pushBtn_AlterArea, SIGNAL(clicked()), this, SLOT( on_pushBtn_AlterArea_clicked() ) );



    ui->pushBtn_Read->setIcon( QIcon( tr(":/Resources/up.ico") ) );

    //connect( ui->pushBtn_Read, SIGNAL(clicked()), this, SLOT( on_pushBtn_Read_clicked() ) );



    ui->pushBtn_Write->setIcon( QIcon( tr(":/Resources/down.ico") ) );

    //connect( ui->pushBtn_Write, SIGNAL(clicked()), this, SLOT( on_pushBtn_Write_clicked() ) );



    // ui->pushBtn_Act->setIcon( QIcon( tr(":/Resources/apply.ico") ) );

    //connect( ui->pushBtn_Act, SIGNAL(clicked()), this, SLOT( on_pushBtn_Act_clicked() ) );



    ui->pushBtn_Compare->setIcon( QIcon( tr(":/Resources/f1.ico") ) );

    // ui->pushBtn_check->setIcon( QIcon( tr(":/Resources/f1.ico") ) );

    //   connect( ui->pushBtn_Compare, SIGNAL(clicked()), this, SLOT( on_pushBtn_Compare_clicked() ) );





    ui->lineEdit_TermName->setReadOnly( true );

    ui->lineEdit_TermName->setStyleSheet( "background-color:rgb(255,230,255)" );



    //add by lxf for ningxia

    ui->pushBtn_SaveFile->setIcon(QIcon( tr(":/Resources/savebutton.ico") ));

    // connect( ui->pushBtn_SaveFile, SIGNAL(clicked()), this, SLOT( on_pushBtn_SaveFile_clicked() ) );

    //add end

    //ui->splitterTree->setSizes( QList<int>() << 100 << 200 );

    //ui->splitter->setSizes( QList<int>() << 150 << 200 );



    QSplitterHandle *pHandle = ui->splitter->handle( 0 );

    if( pHandle != NULL )

    {

        pHandle->setMinimumWidth( 100 );

        pHandle->setMaximumWidth( 300 );

    }



    pHandle = ui->splitter_3->handle( 0 );

    if( pHandle != NULL )

    {

        pHandle->setMinimumWidth( 100 );

        pHandle->setMaximumWidth( 300 );

    }



    ui->lineEdit_TreeLocate->setToolTip( tr( "输入首字母或文字后回车进行匹配" ) );



    ui->pushButton_TreeGo->setText( QString( tr("") ) );

    ui->pushButton_TreeGo->setIcon( QIcon( tr(":/Resources/find.ico") ) );

    ui->pushButton_TreeGo->setToolTip( tr( "模糊检索快速定位树上设备" ) );



    ui->lineEdit_TermLocate->setToolTip( tr( "输入首字母或文字后回车进行匹配" ) );



    ui->pushButton_TermGo->setText( QString( tr("") ) );

    ui->pushButton_TermGo->setIcon( QIcon( tr(":/Resources/find.ico") ) );

    ui->pushButton_TermGo->setToolTip( tr( "模糊检索快速定位终端" ) );



    ui->pushBtn_fixedSearch->setText( QString( tr("") ) );

    ui->pushBtn_fixedSearch->setIcon( QIcon( tr(":/Resources/find.ico") ) );

    ui->pushBtn_fixedSearch->setToolTip( tr( "模糊检索快速定位定值" ) );



    connect( ui->lineEdit_TermLocate, SIGNAL(textChanged(QString)), this, SLOT( OnTermLocateTextChanged(QString) ) );

    connect( ui->lineEdit_TermLocate, SIGNAL(returnPressed()), this, SLOT( OnTermLocateReturnPressed() ) );



    connect( ui->lineEdit_TreeLocate, SIGNAL(returnPressed()), this, SLOT( OnTreeLocateReturnPressed() ) );

    connect( ui->lineEdit_fixedName, SIGNAL(returnPressed()), this, SLOT( on_pushBtn_fixedSearch_clicked() ) );



    this->setEnabled( false );

}



void TermParamWindow::initStatusBar()

{

    QStatusBar * pStatusBar = this->statusBar();

    if( pStatusBar == NULL ) return;



    m_pOperLabel = new QLabel();                    //新建标签

    m_pOperLabel->setMinimumSize( 200, 23 );        //设置标签最小尺寸

    m_pOperLabel->setFixedWidth( 260 );

    m_pOperLabel->setFrameShape(QFrame::Panel);     //设置标签形状

    m_pOperLabel->setFrameShadow(QFrame::Sunken);   //设置标签阴影

    m_pOperLabel->setText( tr("操作:") );



    m_pProgressBar = new QProgressBar();

    m_pProgressBar->setMinimumSize( 300, 23 );

    m_pProgressBar->setFixedWidth( 500 );

    m_pProgressBar->setRange( 0, m_oper_timeout_slice );

    m_pProgressBar->setValue( 0 );



    pStatusBar->addWidget( m_pOperLabel );

    pStatusBar->addWidget( m_pProgressBar );



    m_pOperLabel->setText( tr("空闲") );

    m_pProgressBar->setVisible( false );

}

/*

void TermParamWindow::initDevTree()

{

    m_pDevCtrl = new DevTreeCtrl( this );

    QLayout *pWTL = ui->widgetTree->layout();

    if( pWTL != NULL ) pWTL->addWidget(m_pDevCtrl);

    connect( m_pDevCtrl, SIGNAL(itemDoubleClicked(HTreeWidgetItem*,int)), this, SLOT( treeItemDoubleClicked(HTreeWidgetItem*,int) ) );



    m_pDevCtrl->clearDefData();

    m_pDevCtrl->hideTypeNode();



    //构造设备树

    QString sql;



    //****************第1级

    //****************parent=0

    //区域表	 404

    sql = QString("SELECT id, name, father_id FROM subcontrolarea");

    m_pDevCtrl->addDefData(0, TABLE_NO_SUBCONTROLAREA, tr("区域"), sql);



    //厂站表 405  parent=-1     其他设备

    sql = QString("SELECT id, name,subarea_id FROM substation WHERE subarea_id =0 order by name");

    m_pDevCtrl->addDefData(-1, TABLE_NO_SUBSTATION, tr("厂站"), sql);



    //配网馈线表  13500 parent=-1     其他设备

    sql = QString("SELECT id,name,st_id FROM dms_feeder_device WHERE  st_id =0  order by name");

    m_pDevCtrl->addDefData(-1, TABLE_NO_DMS_FEEDER_DEVICE, tr("馈线"), sql);



    //****************第2级



    //厂站表 405 parent=区域

    sql = QString("SELECT id, name,subarea_id FROM substation WHERE subarea_id =[PARENT_ID] order by name");

    m_pDevCtrl->addDefData(TABLE_NO_SUBCONTROLAREA, TABLE_NO_SUBSTATION, tr("厂站"), sql);



    //配网馈线表  13500 parent=station

    sql = QString("SELECT id,name,st_id FROM dms_feeder_device WHERE  st_id =[PARENT_ID] order by name");

    m_pDevCtrl->addDefData(TABLE_NO_SUBSTATION, TABLE_NO_DMS_FEEDER_DEVICE, tr("馈线"), sql);



    //配网终端信息表 13510 parent=feederline

    //sql = QString("SELECT id, name,feeder_id FROM dms_terminal_info WHERE feeder_id =[PARENT_ID] order by name");

    //m_pDevCtrl->addDefData(TABLE_NO_DMS_FEEDER_DEVICE, TABLE_NO_DMS_TERMINAL_INFO, tr("终端"), sql);



    m_pDevCtrl->loadTree();



    DevTree_FilterArea();



    return;



    //树节点全部展开

    m_pDevCtrl->expandAll();

    //树节点全部收缩

    QTreeWidgetItemIterator it( m_pDevCtrl );

    while( (*it++) != NULL )

    {

        HTreeWidgetItem *pItem = (HTreeWidgetItem *)*it;

        if( pItem != NULL )

        {

            pItem->setExpanded( false );



            int table_no = m_pDevCtrl->getTableNo( pItem );

            if( table_no != TABLE_NO_DMS_FEEDER_DEVICE )

            {

                if( pItem->childCount() == 0  )

                {

                    m_pDevCtrl->setItemHidden( pItem, true );    //隐藏无子节点

                }

            }

        }

    }

}

*/

void TermParamWindow::initDevTree( bool b )

{

    cout<<"initDevTree"<<endl;

    qint8 is_read_from_conf = 0;

    qint8 local_region_id   = 0;

    get_local_area_no( is_read_from_conf, local_region_id );



    QListSubControlArea lstSubControlArea;

    m_rtdbOper.GetSubControlArea( lstSubControlArea );



    QListSubStation lstSubStation;

    m_rtdbOper.GetSubStation( lstSubStation );



#if 0

    QListFeederDevice lstFeederDevice;

    m_rtdbOper.GetFeederDevice( lstFeederDevice );

#endif

    QListFeederDevice lstFeederDevice;

    getFeederInfo(lstFeederDevice);



    QList_T_SubControlArea lstArea;



    SUBCONTROLAREA_STRUCT area;

    foreach( area, lstSubControlArea )

    {

        T_SubControlArea item;

        item.m_SubControlArea = area;

        long tmp_area_id= item.m_SubControlArea.id;

        if(m_area_id.find(tmp_area_id)==m_area_id.end()&&m_area_id.size()>0)//不配区域id  就显示全部

            continue;

        lstArea.append( item );

    }



    SUBSTATION_STRUCT station;

    foreach( station, lstSubStation )

    {

        for( int i = 0; i < lstArea.size(); i++ )

        {

            T_SubControlArea &area = lstArea[i];

            long tmp_area_id=station.subarea_id;

            if(m_area_id.find(tmp_area_id)==m_area_id.end()&&m_area_id.size()>0)//不配区域id  就显示全部

                continue;

            if( station.subarea_id == area.m_SubControlArea.id )

            {

                T_SubStation item;

                item.m_SubStation = station;

                area.m_lstSubStation.append( item );



                break;

            }

        }

    }





    QListDmsTerminalInfo lstTermInfo;

    m_rtdbOper.GetTerminalInfo(lstTermInfo);

    DMS_TERMINAL_INFO_STRUCT termInfo;

    m_termId_feederId_map.clear();

    m_termId_info_map.clear();

    m_feederId_termLst_map.clear();



    foreach(termInfo, lstTermInfo)

    {

        m_termId_feederId_map[termInfo.id]=termInfo.feeder_id;

        m_termId_info_map[termInfo.id]=termInfo;



        if(m_feederId_termLst_map.find(termInfo.feeder_id)==m_feederId_termLst_map.end())

        {

            QList<DMS_TERMINAL_INFO_STRUCT>  termLst;

            termLst.push_back(termInfo);

            m_feederId_termLst_map[termInfo.feeder_id]=termLst;

        }

        else

        {

            m_feederId_termLst_map[termInfo.feeder_id].push_back(termInfo);

        }

    }



    m_feederId_info_map.clear();

    DMS_FEEDER_DEVICE_STRUCT feeder;

    foreach( feeder, lstFeederDevice )

    {



        if(m_localresptermId_lst.indexOf(feeder.id)==-1)

            continue;



        m_feederId_info_map[feeder.id]=feeder;

#if 0

        //只对馈线按区域过滤

        if( is_read_from_conf != 0 && local_region_id != 1/*1可看所有的区域*/ )

        {

            D5000_KEYID kid( feeder.id );

            if( kid.area_id > 0 && kid.area_id != local_region_id )

            {

                continue;

            }

        }

#endif



        bool bAppend = false;



        for( int i = 0; i < lstArea.size(); i++ )

        {

            T_SubControlArea &area = lstArea[i];

            for( int j = 0; j < area.m_lstSubStation.size(); j++ )

            {

                T_SubStation &item = area.m_lstSubStation[j];



                if( feeder.st_id == item.m_SubStation.id )

                {

                    item.m_lstFeeder.append( feeder );

                    bAppend = true;

                    break;

                }

            }



            if( bAppend ) break;

        }

    }



    for( int i = lstArea.size()-1; i >= 0; i-- )

    {

        T_SubControlArea &area = lstArea[i];



        for( int j = area.m_lstSubStation.size()-1; j >= 0; j-- )

        {

            T_SubStation &item = area.m_lstSubStation[j];

            if( item.m_lstFeeder.size() == 0 )

            {

                area.m_lstSubStation.removeAt( j );

            }

        }



        if( area.m_lstSubStation.size() == 0 )

        {

            lstArea.removeAt( i );

        }

    }



    QTreeWidget *pTreeWidget = ui->treeWidget;

    if( pTreeWidget == NULL ) return;



    pTreeWidget->clear();

    pTreeWidget->setHeaderHidden( true );

    pTreeWidget->setStyleSheet( "QTreeView::item:hover{background-color:rgb(128,0,0,32)}" "QTreeView::item:selected{background-color:rgb(0,0,255,125)}" );



    QTreeWidgetItem *pStationNodeItem = NULL;



    T_SubControlArea t_area;

    foreach( t_area, lstArea )

    {

        QTreeWidgetItem *pAreaItem = NULL;



        long tmp_area_id=t_area.m_SubControlArea.id;

        if(m_area_id.find(tmp_area_id)==m_area_id.end()&&m_area_id.size()>0)//不配区域id  正常显示

            continue;



        bool bFlag = false;

        if( t_area.m_SubControlArea.area_type == 0 && t_area.m_SubControlArea.father_id > 0 )

        {

            SUBCONTROLAREA_STRUCT area;

            if( m_rtdbOper.GetSubControlArea( area, t_area.m_SubControlArea.father_id ) )

            {

                if( area.area_type == 3/*区域类型-省*/ )

                {

                    bFlag = true;

                }

            }

        }



        if( bFlag || t_area.m_SubControlArea.area_type == 4/*区域类型-地*/ )

        {

            if( pStationNodeItem == NULL )

            {

                pStationNodeItem = new QTreeWidgetItem( QStringList() << tr("变电站") );

                pStationNodeItem->setIcon( 0, QIcon( tr(":/Resources/h7.ico") ) );

                pStationNodeItem->setData( 0, Qt::UserRole+10, (qint64)0L );

                pTreeWidget->addTopLevelItem( pStationNodeItem );

            }



            pAreaItem = pStationNodeItem;

        }

        else

        {

            pAreaItem = new QTreeWidgetItem( QStringList() << t_area.m_SubControlArea.name );

            pAreaItem->setIcon( 0, QIcon( tr(":/Resources/x%1.bmp").arg(TABLE_NO_SUBCONTROLAREA) ) );

            pAreaItem->setData( 0, Qt::UserRole+10, t_area.m_SubControlArea.id );



#ifdef _DEBUG

            pAreaItem->setToolTip( 0, tr("区域%1").arg(D5000_KEYID(t_area.m_SubControlArea.id).ToString().c_str()) );

#endif



            pTreeWidget->addTopLevelItem( pAreaItem );

        }



        T_SubStation t_station;

        foreach( t_station, t_area.m_lstSubStation )

        {

            QTreeWidgetItem *pStationItem = new QTreeWidgetItem( QStringList() << t_station.m_SubStation.name );

            pStationItem->setIcon( 0, QIcon( tr(":/Resources/x%1.bmp").arg(TABLE_NO_SUBSTATION) ) );

            pStationItem->setData( 0, Qt::UserRole+10, t_station.m_SubStation.id );



#ifdef _DEBUG

            pStationItem->setToolTip( 0, tr("厂站%1").arg(D5000_KEYID(t_station.m_SubStation.id).ToString().c_str()) );

#endif



            pAreaItem->addChild( pStationItem );



            DMS_FEEDER_DEVICE_STRUCT t_feeder;

            foreach( t_feeder, t_station.m_lstFeeder )

            {

                QTreeWidgetItem *pFeederItem = new QTreeWidgetItem( QStringList() << t_feeder.name );

                pFeederItem->setIcon( 0, QIcon( tr(":/Resources/x%1.bmp").arg(TABLE_NO_DMS_FEEDER_DEVICE) ) );

                pFeederItem->setData( 0, Qt::UserRole+10, t_feeder.id );

                pFeederItem->setToolTip( 0, tr("馈线%1").arg(D5000_KEYID(t_feeder.id).ToString().c_str()) );

                pStationItem->addChild( pFeederItem );

            }

        }

    }



    connect( pTreeWidget, SIGNAL(itemDoubleClicked(QTreeWidgetItem *, int)), this, SLOT( OnTreeItemDoubleClicked(QTreeWidgetItem *, int) ) );



#ifndef HIDE_PARAM_MUL_OP

    loadDevTree_mul(lstArea);

#endif

}

/*

void TermParamWindow::initDevTreeLocate()

{

    m_TreeDevLocate.Clear();



    HzToPy help;



    QTreeWidgetItemIterator it( m_pDevCtrl );

    while( (*it) != NULL )

    {

        HTreeWidgetItem *pItem = (HTreeWidgetItem *)*it;

        if( pItem != NULL && m_pDevCtrl->isDeviceItem( pItem ) )

        {

            qint64 dev_id = m_pDevCtrl->getDevId( pItem );

            int table_no = m_pDevCtrl->getTableNo( pItem );



            switch( table_no )

            {

            case TABLE_NO_SUBCONTROLAREA:

            {

                SUBCONTROLAREA_STRUCT rec;

                if( m_rtdbOper.GetSubControlArea( rec, dev_id ) )

                {

                    SMatchItem item;

                    strcpy( item.hz_code, rec.name );

                    help.TransHZIntoPY( item.hz_code, item.py_code );

                    item.userData = pItem;



                    m_TreeDevLocate.m_lstCache.append( item );

                }

            }

                break;

            case TABLE_NO_SUBSTATION:

            {

                SUBSTATION_STRUCT rec;

                if( m_rtdbOper.GetSubStation( rec, dev_id ) )

                {

                    SMatchItem item;

                    strcpy( item.hz_code, rec.name );

                    help.TransHZIntoPY( item.hz_code, item.py_code );

                    item.userData = pItem;



                    m_TreeDevLocate.m_lstCache.append( item );

                }

            }

                break;

            case TABLE_NO_DMS_FEEDER_DEVICE:

            {

                DMS_FEEDER_DEVICE_STRUCT rec;

                if( m_rtdbOper.GetFeederDevice( rec, dev_id ) )

                {

                    SMatchItem item;

                    strcpy( item.hz_code, rec.name );

                    help.TransHZIntoPY( item.hz_code, item.py_code );

                    item.userData = pItem;



                    m_TreeDevLocate.m_lstCache.append( item );

                }

            }

                break;

            default:

                break;

            }

        }



        it++;

    }



    connect( ui->lineEdit_TreeLocate, SIGNAL(textChanged(QString)), this, SLOT( OnTreeLocateTextChanged(QString) ) );

    connect( ui->lineEdit_TreeLocate, SIGNAL(returnPressed()), this, SLOT( OnTreeLocateReturnPressed() ) );

}*/



void TermParamWindow::initDevTreeLocate( bool b )

{

    m_TreeDevLocate.Clear();



    HzToPy help;



    QTreeWidgetItemIterator it( ui->treeWidget );

    while( (*it) != NULL )

    {

        QTreeWidgetItem *pItem = *it;

        if( pItem != NULL )

        {

            qint64 dev_id = pItem->data( 0, Qt::UserRole+10 ).toLongLong();



            int table_no = D5000_KEYID(dev_id).table_no;



            switch( table_no )

            {

            case TABLE_NO_SUBCONTROLAREA:

            {

                SUBCONTROLAREA_STRUCT rec;

                if( m_rtdbOper.GetSubControlArea( rec, dev_id ) )

                {

                    SMatchItem item;

                    strcpy( item.hz_code, rec.name );

                    help.TransHZIntoPY( item.hz_code, item.py_code );

                    item.userData = pItem;



                    m_TreeDevLocate.m_lstCache.append( item );



                }

            }

                break;

            case TABLE_NO_SUBSTATION:

            {

                SUBSTATION_STRUCT rec;

                if( m_rtdbOper.GetSubStation( rec, dev_id ) )

                {

                    SMatchItem item;

                    strcpy( item.hz_code, rec.name );

                    help.TransHZIntoPY( item.hz_code, item.py_code );

                    item.userData = pItem;



                    m_TreeDevLocate.m_lstCache.append( item );

                    // qDebug()<<"chiness  ="<<rec.name<<" py="<<item.py_code<<endl;

                }

            }

                break;

            case TABLE_NO_DMS_FEEDER_DEVICE:

            {

                DMS_FEEDER_DEVICE_STRUCT rec;

                if( m_rtdbOper.GetFeederDevice( rec, dev_id ) )

                {

                    SMatchItem item;

                    strcpy( item.hz_code, rec.name );

                    help.TransHZIntoPY( item.hz_code, item.py_code );

                    item.userData = pItem;



                    m_TreeDevLocate.m_lstCache.append( item );



                    if(m_feederId_termLst_map.find(rec.id)!=m_feederId_termLst_map.end())

                    {

                        QList<DMS_TERMINAL_INFO_STRUCT>  termLst=m_feederId_termLst_map[rec.id];

                        // cout<<"termLst=="<<termLst.count()<<endl;

                        for(int tt=0;tt<termLst.count();++tt)

                        {

                            // cout<<"tt=="<<tt<<endl;

                            SMatchItem item_tmp;

                            strcpy( item_tmp.hz_code, termLst[tt].name);

                            help.TransHZIntoPY( item_tmp.hz_code, item_tmp.py_code );

                            item_tmp.userData = pItem;



                            m_TreeDevLocate.m_lstCache.append( item_tmp );

                        }

                    }



                }

            }

                break;

            default:

                break;

            }

        }



        it++;

    }



    connect( ui->lineEdit_TreeLocate, SIGNAL(textChanged(QString)), this, SLOT( OnTreeLocateTextChanged(QString) ) );

    connect( ui->lineEdit_TreeLocate, SIGNAL(returnPressed()), this, SLOT( OnTreeLocateReturnPressed() ) );

}



void TermParamWindow::DevTree_FilterArea()

{

    /*  qint8 is_read_from_conf = 0;

    qint8 local_region_id   = 0;

    get_local_area_no( is_read_from_conf, local_region_id );



    qDebug() << tr("is_read_from_conf=%1  local_region_id=%2").arg(is_read_from_conf).arg(local_region_id);



    if( is_read_from_conf == 0 ) return;

    if( local_region_id <= 0 ) return;



    int hide_count = 0;



    QTreeWidgetItemIterator it( m_pDevCtrl );

    while( (*it++) != NULL )

    {

        HTreeWidgetItem *pItem = (HTreeWidgetItem *)*it;

        if( pItem != NULL )

        {

            qint64 dev_id = m_pDevCtrl->getDevId( pItem );

            if( dev_id != 0L )

            {

                D5000_KEYID kid( dev_id );

                if( kid.area_id >= 0 && kid.area_id != local_region_id )

                {

                    m_pDevCtrl->setItemHidden( pItem, true );    //隐藏

                    hide_count++;

                }

            }

        }

    }



    qDebug() << tr("过滤了 %1 个节点").arg(hide_count);

*/

}



void TermParamWindow::OnTreeLocateTextChanged( const QString &strText )

{

    MyWaitCursor mwc;



    TLocate &locate = m_TreeDevLocate.m_locate;



    locate.Clear();



    QString strLocate = strText.toLower();

    if( strLocate.isEmpty() || strLocate.length() == 0 ) return;



    SMatchItem item ;

    QSet<QTreeWidgetItem *>  item_set;

    foreach( item, m_TreeDevLocate.m_lstCache )

    {

        QTreeWidgetItem *pItem = (QTreeWidgetItem *)item.userData;

        if( pItem == NULL || pItem->isHidden() ) continue;



        QString strHzCode = QString::fromAscii( item.hz_code ).toLower();

        QString strPyCode = QString::fromAscii( item.py_code ).toLower();



        int idx_hz = strHzCode.indexOf( strLocate );

        int idx_py = strPyCode.indexOf( strLocate );



        if( idx_hz >= 0 || idx_py >= 0 )

        {

            if( idx_hz < 0 ) idx_hz = 0x7fffffff;

            if( idx_py < 0 ) idx_py = 0x7fffffff;



            SMatchItem mi( item );

            mi.index = ( ( idx_hz < idx_py ) ? idx_hz : idx_py );



            locate.Append( mi );

            item_set.insert(pItem);

        }

    }

#if 0

    DMS_TERMINAL_INFO_STRUCT term_item;

    HzToPy help;

    foreach(term_item,m_termId_info_map)

    {

        SMatchItem item_s;

        strcpy( item_s.hz_code, term_item.name );

        help.TransHZIntoPY( item_s.hz_code, item_s.py_code );



        QString strHzCode = QString::fromAscii( item.hz_code ).toLower();

        QString strPyCode = QString::fromAscii( item.py_code ).toLower();



        int idx_hz = strHzCode.indexOf( strLocate );

        int idx_py = strPyCode.indexOf( strLocate );



        if( idx_hz >= 0 || idx_py >= 0 )

        {

            qint64 feeder_id=term_item.feeder_id;

            QString feederName_str=m_feederId_info_map[feeder_id].name;



            SMatchItem item_tmp ;

            foreach( item_tmp, m_TreeDevLocate.m_lstCache )

            {

                QTreeWidgetItem *pItem = (QTreeWidgetItem *)item_tmp.userData;

                if( pItem == NULL || pItem->isHidden() ) continue;



                if(item_set.find(pItem)!=item_set.end())

                    break;



                QString strHzCode_tmp = QString::fromAscii( item_tmp.hz_code ).toLower();

                QString strPyCode_tmp = QString::fromAscii( item_tmp.py_code ).toLower();



                int idx_hz_tmp = strHzCode_tmp.indexOf( feederName_str);

                int idx_py_tmp = strPyCode_tmp.indexOf( feederName_str );



                if( idx_hz_tmp >= 0 || idx_py_tmp >= 0 )

                {

                    if( idx_hz_tmp < 0 ) idx_hz_tmp = 0x7fffffff;

                    if( idx_py_tmp < 0 ) idx_py_tmp = 0x7fffffff;



                    SMatchItem mi( item_tmp );

                    mi.index = ( ( idx_hz_tmp < idx_py_tmp ) ? idx_hz_tmp : idx_py_tmp );



                    locate.Append( mi );



                }

            }

        }

    }

#endif

    locate.Sort();

    locate.ResetIndex();







}



void TermParamWindow::OnTreeLocateReturnPressed()

{

    on_pushButton_TreeGo_clicked();

}



void TermParamWindow::on_pushButton_TreeGo_clicked()

{

    MyWaitCursor mwc;



    //m_pDevCtrl->locateDev( 0, ui->lineEdit_TreeLocate->text().toLower() );

    //return;



    TLocate &locate = m_TreeDevLocate.m_locate;



    void *userData = locate.UserData();

    if( userData != NULL )

    {

        locate.IncreaseIndex();



        QTreeWidgetItem *pItem = (QTreeWidgetItem *)userData;

        pItem->setSelected( true );



        ui->treeWidget->setCurrentItem( pItem );

        ui->treeWidget->scrollToItem( pItem );



        OnTreeItemDoubleClicked(pItem, 0);

        ui->lineEdit_TermLocate->setText(ui->lineEdit_TreeLocate->text());

        on_pushButton_TermGo_clicked();

    }

}

/*

void TermParamWindow::treeItemDoubleClicked( HTreeWidgetItem *pItem, int column )

{

    if( pItem == NULL ) return;

    if( ! m_pDevCtrl->isDeviceItem( pItem ) ) return;



    qint64 dev_id = m_pDevCtrl->getDevId( pItem );

    int table_no = m_pDevCtrl->getTableNo( pItem );



    if( table_no == TABLE_NO_DMS_FEEDER_DEVICE )

    {

        BeforeCallArea();



        load_term( dev_id );



        show_term();

    }

    else

    {

        pItem->setExpanded( ! pItem->isExpanded() );

    }

}

*/

void TermParamWindow::OnTreeItemDoubleClicked( QTreeWidgetItem *pItem, int column )

{

    if( pItem == NULL ) return;



    qint64 dev_id = pItem->data( 0, Qt::UserRole+10 ).toLongLong();



    int table_no = D5000_KEYID(dev_id).table_no;



    if( table_no == TABLE_NO_DMS_FEEDER_DEVICE )

    {

        BeforeCallArea();



        load_term( dev_id );



        show_term();

    }

    else

    {

        pItem->setExpanded( pItem->isExpanded() );

    }

}

void TermParamWindow::OnTreeItemDoubleClicked_mul( QTreeWidgetItem *pItem, int column )

{

    if( pItem == NULL ) return;



    qint64 dev_id = pItem->data( 0, Qt::UserRole+10 ).toLongLong();



    int table_no = D5000_KEYID(dev_id).table_no;



#if 0

    if( table_no == TABLE_NO_DMS_FEEDER_DEVICE )

    {

        load_cb_mul(dev_id);

        load_term_mul(dev_id);

        show_term_mul();



        ui->comboBox_param->clear();

        m_param_lst.clear();

        m_code_paramDict_map.clear();

        QStandardItemModel *pModel = NULL;

        QTableView  *pTableView=ui->tableView_term_mul;

        pModel=(QStandardItemModel *)pTableView->model();

        for(int row=0;row<pModel->rowCount();++row)

        {

            QStandardItem *pItem = pModel->item( row, MUL_TERM_COL_NAME );

            if( pItem == NULL ) continue;

            qint64 term_id=pItem->data(Qt::UserRole+10).toLongLong();

            if(!load_param_mul(term_id))

            {

                m_param_lst.clear();

                return ;

            }

        }





        for(int row=0;row<m_param_lst.count();++row)

        {

            ui->comboBox_param->addItem(m_param_lst[row].name,QString(m_param_lst[row].param_code));

            m_code_paramDict_map[m_param_lst[row].param_code]=m_param_lst[row];

        }





    }

    else

#endif

        if(table_no==TABLE_NO_DMS_TERM_XH)

        {



            qint64 feeder_dev_id = pItem->data( 0, Qt::UserRole+11 ).toLongLong();



            load_cb_mul(dev_id,feeder_dev_id);

            load_term_mul(dev_id);

            show_term_mul();



            ui->comboBox_param->clear();

            m_param_lst.clear();

            m_code_paramDict_map.clear();

            QStandardItemModel *pModel = NULL;

            QTableView  *pTableView=ui->tableView_term_mul;

            pModel=(QStandardItemModel *)pTableView->model();

            for(int row=0;row<pModel->rowCount();++row)

            {

                QStandardItem *pItem = pModel->item( row, MUL_TERM_COL_NAME );

                if( pItem == NULL ) continue;

                qint64 term_id=pItem->data(Qt::UserRole+10).toLongLong();

                if(!load_param_mul(term_id))

                {

                    m_param_lst.clear();

                    return ;

                }

                else

                {

                    break;//所有终端型号一样  不需要重新获取

                }

            }





            for(int row=0;row<m_param_lst.count();++row)

            {

                ui->comboBox_param->addItem(m_param_lst[row].name,QString(m_param_lst[row].param_code));

                m_code_paramDict_map[m_param_lst[row].param_code]=m_param_lst[row];

            }

        }

        else

        {

            pItem->setExpanded( pItem->isExpanded() );

        }

}



void TermParamWindow::OnTreeLocateTextChanged_mul(const QString &strText)

{

    MyWaitCursor mwc;



    TLocate &locate = m_TreeDevLocate_mul.m_locate;



    locate.Clear();



    QString strLocate = strText.toLower();

    if( strLocate.isEmpty() || strLocate.length() == 0 ) return;



    SMatchItem item;

    foreach( item, m_TreeDevLocate_mul.m_lstCache )

    {

        QTreeWidgetItem *pItem = (QTreeWidgetItem *)item.userData;

        if( pItem == NULL || pItem->isHidden() ) continue;



        QString strHzCode = QString::fromAscii( item.hz_code ).toLower();

        QString strPyCode = QString::fromAscii( item.py_code ).toLower();



        int idx_hz = strHzCode.indexOf( strLocate );

        int idx_py = strPyCode.indexOf( strLocate );



        if( idx_hz >= 0 || idx_py >= 0 )

        {

            if( idx_hz < 0 ) idx_hz = 0x7fffffff;

            if( idx_py < 0 ) idx_py = 0x7fffffff;



            SMatchItem mi( item );

            mi.index = ( ( idx_hz < idx_py ) ? idx_hz : idx_py );



            locate.Append( mi );

        }

    }



    locate.Sort();



    locate.ResetIndex();

}



void TermParamWindow::OnTreeLocateReturnPressed_mul()

{

    on_pushButton_TreeGo_mul_clicked();

}

void TermParamWindow::initCheckComboBox( ZCheckComboBox *pCmbBox, QString select_all_label )

{

    if( pCmbBox == NULL ) return;



    pCmbBox->set_AllOption_Index( 0 );

    pCmbBox->init_icon( QString::fromUtf8(":/Resources/checked.png"),

                        QString::fromUtf8(":/Resources/check15.png"),

                        QString::fromUtf8(":/Resources/uncheck.png") );



    pCmbBox->clear();



    qint32 item_value = 0x7fffffff;



    pCmbBox->appendItem( select_all_label.isEmpty() || select_all_label.isNull() ? tr("<全选>") : select_all_label, true );

    pCmbBox->setItemData( pCmbBox->count()-1, item_value, Qt::UserRole+10 );



    pCmbBox->appendSeparator();

    pCmbBox->setItemData( pCmbBox->count()-1, item_value, Qt::UserRole+10 );

}



void TermParamWindow::init_cmb_term_brand()

{

    ZCheckComboBox *pCmbBox = ui->comboBox_Brand;

    if( pCmbBox == NULL ) return;



    initCheckComboBox( pCmbBox, tr("<所有厂家>") );



    QListSysMenuInfo lstSysMenuInfo;

    if( ! m_rtdbOper.GetSysMenuInfo( lstSysMenuInfo, TABLE_NO_DMS_TERMINAL_INFO, TABLE_NO_DMS_TERMINAL_INFO_FIELD_NAME_FACTORY ) ) return;



    SYS_MENU_INFO_STRUCT menu;

    foreach( menu, lstSysMenuInfo )

    {

        pCmbBox->appendItem( menu.display_value, false );

        pCmbBox->setItemData( pCmbBox->count()-1, menu.actual_value, Qt::UserRole+10 );

    }



    connect( pCmbBox, SIGNAL(checkedStateChange(int,bool)), this, SLOT( OnFilterChange(int,bool) ) );

}



void TermParamWindow::init_cmb_term_type()

{

    ZCheckComboBox *pCmbBox = ui->comboBox_Type;

    if( pCmbBox == NULL ) return;



    initCheckComboBox( pCmbBox, tr("<所有类别>") );



    QListSysMenuInfo lstSysMenuInfo;

    m_rtdbOper.GetSysMenuInfo( lstSysMenuInfo, TABLE_NO_DMS_TERMINAL_INFO, TABLE_NO_DMS_TERMINAL_INFO_FIELD_NAME_TERM_TYPE );

    //if( lstSysMenuInfo.size() == 0 ) m_rtdbOper.GetSysMenuInfo( lstSysMenuInfo, TABLE_NO_DMS_TERMINAL_INFO, TABLE_NO_DMS_TERMINAL_INFO_FIELD_NAME_TERMINAL_TYPE );



    SYS_MENU_INFO_STRUCT menu;

    foreach( menu, lstSysMenuInfo )

    {

        pCmbBox->appendItem( menu.display_value, false );

        pCmbBox->setItemData( pCmbBox->count()-1, menu.actual_value, Qt::UserRole+10 );

    }



    connect( pCmbBox, SIGNAL(checkedStateChange(int,bool)), this, SLOT( OnFilterChange(int,bool) ) );

}



void TermParamWindow::init_cmb_term_status()

{

    ZCheckComboBox *pCmbBox = ui->comboBox_Status;

    if( pCmbBox == NULL ) return;



    initCheckComboBox( pCmbBox, tr("<所有工况>") );



    QListSysMenuInfo lstSysMenuInfo;

    if( ! m_rtdbOper.GetSysMenuInfo( lstSysMenuInfo, TABLE_NO_DMS_COM_TERMINAL, 46/*通讯终端工况*/ ) ) return;



    SYS_MENU_INFO_STRUCT menu;

    foreach( menu, lstSysMenuInfo )

    {

        pCmbBox->appendItem( menu.display_value, false );

        pCmbBox->setItemData( pCmbBox->count()-1, menu.actual_value, Qt::UserRole+10 );

    }



    connect( pCmbBox, SIGNAL(checkedStateChange(int,bool)), this, SLOT( OnFilterChange(int,bool) ) );

}



void TermParamWindow::init_tab()

{

    //设置图标

    {

        int idx = ui->tabWidget->indexOf( ui->tab_0 );

        if( idx >= 0 ) ui->tabWidget->setTabIcon( idx, QIcon( tr(":/Resources/f1.ico") ) );

    }

    {

        int idx = ui->tabWidget->indexOf( ui->tab_1 );

        if( idx >= 0 ) ui->tabWidget->setTabIcon( idx, QIcon( tr(":/Resources/f11.ico") ) );

    }

    {

        int idx = ui->tabWidget->indexOf( ui->tab_2 );

        if( idx >= 0 ) ui->tabWidget->setTabIcon( idx, QIcon( tr(":/Resources/f19.ico") ) );

    }



    QListSysMenuInfo lstSysMenuInfo;

    m_rtdbOper.GetSysMenuInfo( lstSysMenuInfo, tr("终端参数类别") );

    SYS_MENU_INFO_STRUCT sub_menu;

    foreach( sub_menu, lstSysMenuInfo )

    {

        if( sub_menu.actual_value >= 0 && sub_menu.actual_value < 3/*ui->tabWidget->count()*/ )

        {

            ui->tabWidget->setTabText( sub_menu.actual_value, sub_menu.display_value );

        }

    }

}



void TermParamWindow::get_checked_value( ZCheckComboBox *pCmbBox, QListInt32 &lst )

{

    lst.clear();



    if( pCmbBox == NULL ) return;



    for( int idx = 0; idx < pCmbBox->count(); idx++ )

    {

        if( pCmbBox->itemData( idx ).toBool() )

        {

            qint32 value = pCmbBox->itemData( idx, Qt::UserRole+10 ).toInt();

            lst.append( value );

        }

    }

}



void TermParamWindow::get_checked_term_brand( QListInt32 &lst )

{

    get_checked_value( ui->comboBox_Brand, lst );

}



void TermParamWindow::get_checked_term_type( QListInt32 &lst )

{

    get_checked_value( ui->comboBox_Type, lst );

}



void TermParamWindow::get_checked_term_status( QListInt32 &lst )

{

    get_checked_value( ui->comboBox_Status, lst );

}



void TermParamWindow::initTableWidget_Term()

{

    QStringList lstHeader;

    lstHeader << tr("终端名称");

    lstHeader << tr("厂家");

    lstHeader << tr("类别");

    lstHeader << tr("工况");

    lstHeader<<tr("状态");



    QListInt32 lstColumnWidth;

    lstColumnWidth << 150;

    lstColumnWidth << 100;

    lstColumnWidth << 100;

    lstColumnWidth << 60;

    lstColumnWidth << 60;



    fillTableWidgetHead( ui->tableWidget_Term, lstHeader, lstColumnWidth );



    //隐藏列头

    //ui->tableWidget_Term->verticalHeader()->hide();

    //设置选中时为整行选中

    ui->tableWidget_Term->setSelectionBehavior( QAbstractItemView::SelectRows );

    //单行选

    ui->tableWidget_Term->setSelectionMode( QAbstractItemView::SingleSelection );

    //设置表格的单元为只读

    ui->tableWidget_Term->setEditTriggers( QAbstractItemView::NoEditTriggers );



    ui->tableWidget_Term->horizontalHeader()->setStretchLastSection( false ); //关键



    ui->tableWidget_Term->resizeColumnsToContents();

    ui->tableWidget_Term->update();



    ui->tableWidget_Term->setAlternatingRowColors( true );	//行间隔背景色

    connect( ui->tableWidget_Term, SIGNAL(itemDoubleClicked(QTableWidgetItem *)), this, SLOT( on_TermDoubleClicked(QTableWidgetItem *) ) );

}



void TermParamWindow::inittableView_Para( QTableView *pTableView, bool bReadOnly )

{

    if( pTableView == NULL ) return;



    QStringList lstColumnName;

    lstColumnName   << tr("参数名称")

                    << tr("代码")

                    << tr("召唤值 ")

                    <<tr("参数单位")

                   <<tr("本地数据类型")

                    <<tr("召唤数据类型")

                      <<tr("数据类型比较")

                   <<tr("参数范围")

                  <<tr("步长")

                 <<tr("是否允许修改")

                << tr("召测时间")

                << tr("操作结果")

                << tr("整定系统参数值")

                << tr("修改时间")

                << tr("整定系统比对结果")

                <<tr("校验值")

               <<tr("校验结果");





    QList< QStandardItem * >    listColumn;



    QString column_name;

    foreach( column_name, lstColumnName )

    {

        QStandardItem *pH = new QStandardItem( column_name );

        //QFont f = pH->font();

        //f.setPointSize( 12 );

        //pH->setFont( f );

        listColumn.append( pH );

    }



    //添加表头

    //准备数据模型

    QStandardItemModel *pModel = new QStandardItemModel();

    for( int i = 0; i < listColumn.size(); i++ )

        pModel->setHorizontalHeaderItem( i, listColumn[i] );

    //利用setModel()方法将数据模型与QTableView绑定

    pTableView->setModel( pModel );



    //pModel->setHeaderData( 1, Qt::Horizontal, QColor(180,230,230), Qt::BackgroundRole );



    //设置列宽不可变动，即不能通过鼠标拖动增加列宽

    //pTableView->horizontalHeader()->setResizeMode( 0, QHeaderView::Fixed/*ResizeToContents*/ );

    //pTableView->horizontalHeader()->setResizeMode( 1, QHeaderView::Fixed );

    //设置表格的各列的宽度值

    for( int c = 0; c < pModel->columnCount(); c++ )

        pTableView->setColumnWidth( c, 100 );

    //默认显示行头，如果你觉得不美观的话，我们可以将其隐藏

    //pTableView->horizontalHeader()->hide();

    //隐藏列头

    //pTableView->verticalHeader()->hide();

    //设置选中时为整行选中

    pTableView->setSelectionBehavior( QAbstractItemView::SelectRows );

    //单行选

    pTableView->setSelectionMode( QAbstractItemView::SingleSelection );

    //设置表格的单元为只读属性，即不能编辑

    if( bReadOnly ) pTableView->setEditTriggers( QAbstractItemView::NoEditTriggers );



    //QFont ft = pTableView->font();

    //ft.setPointSize( 12/*ft.pointSize() * 1.5*/ );

    //pTableView->setFont( ft );



    pTableView->resizeColumnsToContents();

    pTableView->update();



    //connect( pModel, SIGNAL(dataChanged(QModelIndex,QModelIndex)), this, SLOT(OnFieldChanged(QModelIndex,QModelIndex)) );



    QHeaderView *pHV = pTableView->horizontalHeader();

    if( pHV != NULL )

    {

        pHV->setContextMenuPolicy( Qt::CustomContextMenu );

        connect( pHV, SIGNAL(customContextMenuRequested(QPoint)), this, SLOT(clicked_paraRightMenu(QPoint)) );

    }

    if(m_is_allow_zd_op!=1)

    {

        pTableView->setColumnHidden(PARA_COL_UPDATE_TIME,true);

        pTableView->setColumnHidden(PARA_COL_ZD_SYS_VALUE,true);

        pTableView->setColumnHidden(PARA_COL_ZD_SYS_CMPY,true);

        ui->pushBtn_Compare->setEnabled(false);

    }

    //pModel->horizontalHeaderItem()

}



void TermParamWindow::inittableView_YxPara( QTableView *pTableView )

{

    if( pTableView == NULL ) return;



    QStringList lstColumnName;

    lstColumnName   << tr("动作名称")

                    << tr("代码")

                    << tr("动作值 ")

                    <<tr("参数单位")

                   <<tr("参数范围")

                  <<tr("步长")

                 <<tr("是否允许修改")

                << tr("召测时间")

                << tr("操作结果")

                << tr("整定系统参数值")

                << tr("修改时间")

                << tr("比对结果")

                <<tr("校验值")

               <<tr("校验结果");;



    QList< QStandardItem * >    listColumn;



    QString column_name;

    foreach( column_name, lstColumnName )

    {

        QStandardItem *pH = new QStandardItem( column_name );

        //QFont f = pH->font();

        //f.setPointSize( 12 );

        //pH->setFont( f );

        listColumn.append( pH );

    }



    //添加表头

    //准备数据模型

    QStandardItemModel *pModel = new QStandardItemModel();

    for( int i = 0; i < listColumn.size(); i++ )

        pModel->setHorizontalHeaderItem( i, listColumn[i] );

    //利用setModel()方法将数据模型与QTableView绑定

    pTableView->setModel( pModel );



    //pModel->setHeaderData( 1, Qt::Horizontal, QColor(180,230,230), Qt::BackgroundRole );



    //设置列宽不可变动，即不能通过鼠标拖动增加列宽

    //pTableView->horizontalHeader()->setResizeMode( 0, QHeaderView::Fixed/*ResizeToContents*/ );

    //pTableView->horizontalHeader()->setResizeMode( 1, QHeaderView::Fixed );

    //设置表格的各列的宽度值

    for( int c = 0; c < pModel->columnCount(); c++ )

        pTableView->setColumnWidth( c, 100 );

    //默认显示行头，如果你觉得不美观的话，我们可以将其隐藏

    //pTableView->horizontalHeader()->hide();

    //隐藏列头

    //pTableView->verticalHeader()->hide();

    //设置选中时为整行选中

    pTableView->setSelectionBehavior( QAbstractItemView::SelectRows );

    //单行选

    pTableView->setSelectionMode( QAbstractItemView::SingleSelection );

    //设置表格的单元为只读属性，即不能编辑

    //pTableView->setEditTriggers( QAbstractItemView::NoEditTriggers );



    //QFont ft = pTableView->font();

    //ft.setPointSize( 12/*ft.pointSize() * 1.5*/ );

    //pTableView->setFont( ft );



    QStringList strYx;

    strYx << tr("退出") << tr("投入");

    pTableView->setItemDelegateForColumn( PARA_COL_VALUE, new ComboBoxDelegate(strYx, this) );



    pTableView->resizeColumnsToContents();

    pTableView->update();



    connect( pModel, SIGNAL(dataChanged(QModelIndex,QModelIndex)), this, SLOT(OnYxValueChanged(QModelIndex,QModelIndex)) );



    QHeaderView *pHV = pTableView->horizontalHeader();

    if( pHV != NULL )

    {

        pHV->setContextMenuPolicy( Qt::CustomContextMenu );

        connect( pHV, SIGNAL(customContextMenuRequested(QPoint)), this, SLOT(clicked_paraRightMenu(QPoint)) );

    }



}



void TermParamWindow::OnYxValueChanged( const QModelIndex &topLeft, const QModelIndex &bottomRight )

{

    if( topLeft != bottomRight ) return;



    int cur_row = topLeft.row();

    int cur_col = topLeft.column();



    if( cur_col != PARA_COL_VALUE ) return;



    QStandardItemModel *pModel = (QStandardItemModel *)ui->tableView_Para1->model();

    if( pModel == NULL ) return;



    QStandardItem *pValueItem = pModel->itemFromIndex( topLeft );

    if( pValueItem == NULL ) return;



    QString strText = pValueItem->text();

    if( strText.indexOf( tr("退出") ) >= 0 )

        pValueItem->setData( QColor(128,0,0), Qt::TextColorRole );

    else if( strText.indexOf( tr("投入") ) >= 0 )

        pValueItem->setData( QColor(0,0,128), Qt::TextColorRole );

    else

        pValueItem->setData( QColor(128,128,128), Qt::TextColorRole );

}



void TermParamWindow::createParaRightMenu()

{

    m_paraRightMenu = new QMenu();



    m_actionParaSelectAll       = new QAction( QIcon( tr(":/Resources/Zchecked.png") ), tr("全选"), this );

    m_actionParaSelectNone      = new QAction( QIcon( tr(":/Resources/Zuncheck.png") ), tr("不选"), this );

    m_actionParaSelectReverse   = new QAction( QIcon( tr(":/Resources/Zcheck15.png") ), tr("反选"), this );



    m_actionParaSelectAll_allPage       = new QAction( QIcon( tr(":/Resources/Zchecked.png") ), tr("全选所有页"), this );

    m_actionParaSelectNone_allPage      = new QAction( QIcon( tr(":/Resources/Zuncheck.png") ), tr("不选所有页"), this );

    m_actionParaSelectReverse_allPage   = new QAction( QIcon( tr(":/Resources/Zcheck15.png") ), tr("反选所有页"), this );



    m_paraRightMenu->addAction( m_actionParaSelectAll );

    m_paraRightMenu->addAction( m_actionParaSelectNone );

    m_paraRightMenu->addAction( m_actionParaSelectReverse );



    m_paraRightMenu->addAction( m_actionParaSelectAll_allPage );

    m_paraRightMenu->addAction( m_actionParaSelectNone_allPage );

    m_paraRightMenu->addAction( m_actionParaSelectReverse_allPage );



    connect( m_actionParaSelectAll,     SIGNAL(triggered(bool)), this, SLOT(on_paraSelectAll(bool)) );

    connect( m_actionParaSelectNone,    SIGNAL(triggered(bool)), this, SLOT(on_paraSelectNone(bool)) );

    connect( m_actionParaSelectReverse, SIGNAL(triggered(bool)), this, SLOT(on_paraSelectReverse(bool)) );



    connect( m_actionParaSelectAll_allPage,     SIGNAL(triggered(bool)), this, SLOT(on_paraSelectAll_allPage(bool)) );

    connect( m_actionParaSelectNone_allPage,    SIGNAL(triggered(bool)), this, SLOT(on_paraSelectNone_allPage(bool)) );

    connect( m_actionParaSelectReverse_allPage, SIGNAL(triggered(bool)), this, SLOT(on_paraSelectReverse_allPage(bool)) );

}



void TermParamWindow::createParaRightMenu_mul()

{

    m_paraRightMenu_mul = new QMenu();



    m_actionPutInto_mul      = new QAction( QIcon( tr(":/Resources/Zchecked.png") ), tr("全选投入"), this );

    m_actionParaSelectAll_mul       = new QAction( QIcon( tr(":/Resources/Zchecked.png") ), tr("全选"), this );

    m_actionParaSelectNone_mul      = new QAction( QIcon( tr(":/Resources/Zuncheck.png") ), tr("不选"), this );

    m_actionParaSelectReverse_mul   = new QAction( QIcon( tr(":/Resources/Zcheck15.png") ), tr("反选"), this );



    m_paraRightMenu_mul->addAction( m_actionPutInto_mul );

    m_paraRightMenu_mul->addAction( m_actionParaSelectAll_mul );

    m_paraRightMenu_mul->addAction( m_actionParaSelectNone_mul );

    m_paraRightMenu_mul->addAction( m_actionParaSelectReverse_mul );



    connect( m_actionPutInto_mul,     SIGNAL(triggered(bool)), this, SLOT(on_paraSelectPutInto_mul(bool)) );

    connect( m_actionParaSelectAll_mul,     SIGNAL(triggered(bool)), this, SLOT(on_paraSelectAll_mul(bool)) );

    connect( m_actionParaSelectNone_mul,    SIGNAL(triggered(bool)), this, SLOT(on_paraSelectNone_mul(bool)) );

    connect( m_actionParaSelectReverse_mul, SIGNAL(triggered(bool)), this, SLOT(on_paraSelectReverse_mul(bool)) );

}



bool TermParamWindow::load_param_mul(const qint64 &term_dev_id)

{





    DMS_TERMINAL_INFO_STRUCT term_info;

    if( ! m_rtdbOper.ReadTerminalInfo( term_info, term_dev_id ) )

    {





        ZMessageBox::warning( this, tr("id = %1").arg(term_dev_id), tr("查询配网终端信息失败...") );

        return false;

    }



    ui->lineEdit_TermName->setText( term_info.name );



    qint64 term_xh_id = term_info.term_xh;







    if( term_xh_id <= 0 )

    {

        ZMessageBox::warning( this, tr("ID=%1 %2").arg(term_dev_id).arg(D5000_KEYID(term_dev_id).ToString().c_str()), tr("配网终端 %1 \r\n未设置有效的终端型号，请检查...").arg(term_info.name) );

        return false;

    }



    QListDmsTermParamDict   term_param_rec;

    term_param_rec.clear();

    if( ! m_rtdbOper.GetTermParam( term_xh_id, term_param_rec ) )

    {

        ZMessageBox::warning( this, tr("id = %1").arg(m_term_xh_id), tr("装载终端参数字典失败...") );

        return false;

    }



#if 0

    if(m_xhId_codeStrLst_map.find(term_xh_id)==m_xhId_codeStrLst_map.end())

    {



        ZMessageBox::warning( this, tr("id = %1").arg(m_term_xh_id), tr("获取型号配置失败...") );

        return false;

    }

    QStringList codeStr_lst=m_xhId_codeStrLst_map[term_xh_id];

#endif

    for(int ct=0;ct<term_param_rec.count();)

    {

        // if(term_param_rec[ct].dev_flag!=1||term_param_rec[ct].if_modify!=1||codeStr_lst.indexOf(term_param_rec[ct].param_code)==-1)

        if(term_param_rec[ct].dev_flag!=1||term_param_rec[ct].if_modify!=1)

        {

            term_param_rec.removeAt(ct);

            continue;

        }

        ++ct;

    }



#if 0

    if(term_param_rec.count()!=codeStr_lst.count())

    {

        cout<<"term_param_rec ct==="<<term_param_rec.count()<<" code ct=="<<codeStr_lst.count()<<endl;

        ZMessageBox::warning( this, tr("id = %1").arg(m_term_xh_id), tr("配网参数字典表与配置文件不一致 终端名称:%1 型号=%2").arg(term_info.name).arg(term_xh_id) );

        return false;

    }



    for(int ct=0;ct<codeStr_lst.count();++ct)

    {

        bool bFlag=false;

        for(int zz=0;zz<term_param_rec.count();++zz)

        {

            if(term_param_rec[zz].param_code==codeStr_lst[ct])

            {

                bFlag=true;

                break;

            }

        }

        if(!bFlag)

        {

            ZMessageBox::warning( this, tr("id = %1").arg(m_term_xh_id), tr("配网参数字典表与配置文件不一致 终端名称:%1 型号=%2 编码＝%3").arg(term_info.name).arg(term_xh_id).arg(codeStr_lst[ct]) );

            return false;

        }



    }

#endif

#if 0  //同一个型号终端参数一样  不需要这段逻辑

    if(m_param_lst.count()>0)

    {

        if(m_param_lst.count()!=term_param_rec.count())

        {



        }

        else

        {

            for(int jj=0;jj<m_param_lst.count();++jj)

            {



                for(int ii=0;ii<term_param_rec.count();++ii)

                {

                    if(QString(m_param_lst[jj].param_code)==QString(term_param_rec[ii].param_code))

                    {

                        if(m_param_lst[jj].data_type!=term_param_rec[ii].data_type||m_param_lst[jj].param_type!=term_param_rec[ii].param_type)

                        {

                            ZMessageBox::warning( this, tr("id = %1").arg(m_term_xh_id), tr("各终端配网参数字典表不一致 终端名称:%1 型号=%2 编码＝%3").arg(term_info.name).arg(term_xh_id).arg(term_param_rec[ii].param_code) );

                            return false;

                        }

                    }

                }

            }

        }



    }

    else

    {

        m_param_lst=term_param_rec;

    }

#endif

    m_param_lst=term_param_rec;

    return true;



}



void TermParamWindow::on_paraSelectAll( bool b )

{

    QWidget * pCurPage = ui->tabWidget->currentWidget();



    QStandardItemModel *pModel = NULL;

    QMap<qint32,QTableView* >::iterator it=m_paramType_widget_map.begin();

    for(;it!=m_paramType_widget_map.end();++it)

    {

        QTableView *pTableView=it.value();

        if(pTableView->parentWidget()==pCurPage)

            pModel = (QStandardItemModel *)pTableView->model();



    }



    /*if( ui->tableView_Para0->parentWidget() == pCurPage )

           pModel = (QStandardItemModel *)ui->tableView_Para0->model();

       else if( ui->tableView_Para1->parentWidget() == pCurPage )

           pModel = (QStandardItemModel *)ui->tableView_Para1->model();

       else if( ui->tableView_Para2->parentWidget() == pCurPage )

           pModel = (QStandardItemModel *)ui->tableView_Para2->model();

       */

    if( pModel == NULL ) return;



    for( int row = 0; row < pModel->rowCount(); row++ )

    {

        QStandardItem *pItem = pModel->item( row, PARA_COL_NAME );

        if( pItem == NULL ) continue;



        if( pItem->checkState() != Qt::Checked )

        {

            pItem->setCheckState( Qt::Checked );

        }

    }

}



void TermParamWindow::on_paraSelectNone( bool b )

{

    QWidget * pCurPage = ui->tabWidget->currentWidget();



    QStandardItemModel *pModel = NULL;

    QMap<qint32,QTableView* >::iterator it=m_paramType_widget_map.begin();

    for(;it!=m_paramType_widget_map.end();++it)

    {

        QTableView *pTableView=it.value();

        if(pTableView->parentWidget()==pCurPage)

            pModel = (QStandardItemModel *)pTableView->model();



    }

    /*if( ui->tableView_Para0->parentWidget() == pCurPage )

           pModel = (QStandardItemModel *)ui->tableView_Para0->model();

       else if( ui->tableView_Para1->parentWidget() == pCurPage )

           pModel = (QStandardItemModel *)ui->tableView_Para1->model();

       else if( ui->tableView_Para2->parentWidget() == pCurPage )

           pModel = (QStandardItemModel *)ui->tableView_Para2->model();

      */

    if( pModel == NULL ) return;



    for( int row = 0; row < pModel->rowCount(); row++ )

    {

        QStandardItem *pItem = pModel->item( row, PARA_COL_NAME );

        if( pItem == NULL ) continue;



        if( pItem->checkState() != Qt::Unchecked )

        {

            pItem->setCheckState( Qt::Unchecked );

        }

    }

}



void TermParamWindow::on_paraSelectReverse( bool b )

{

    QWidget * pCurPage = ui->tabWidget->currentWidget();



    QStandardItemModel *pModel = NULL;

    QMap<qint32,QTableView* >::iterator it=m_paramType_widget_map.begin();

    for(;it!=m_paramType_widget_map.end();++it)

    {

        QTableView *pTableView=it.value();

        if(pTableView->parentWidget()==pCurPage)

            pModel = (QStandardItemModel *)pTableView->model();



    }

    /* if( ui->tableView_Para0->parentWidget() == pCurPage )

            pModel = (QStandardItemModel *)ui->tableView_Para0->model();

        else if( ui->tableView_Para1->parentWidget() == pCurPage )

            pModel = (QStandardItemModel *)ui->tableView_Para1->model();

        else if( ui->tableView_Para2->parentWidget() == pCurPage )

            pModel = (QStandardItemModel *)ui->tableView_Para2->model();

    */

    if( pModel == NULL ) return;



    for( int row = 0; row < pModel->rowCount(); row++ )

    {

        QStandardItem *pItem = pModel->item( row, PARA_COL_NAME );

        if( pItem == NULL ) continue;



        if( pItem->checkState() == Qt::Unchecked )

            pItem->setCheckState( Qt::Checked );

        else

            pItem->setCheckState( Qt::Unchecked );

    }



}

void TermParamWindow::on_paraSelectAll_allPage( bool b )

{







    QMap<qint32,QTableView* >::iterator it=m_paramType_widget_map.begin();

    for(;it!=m_paramType_widget_map.end();++it)

    {

        QStandardItemModel *pModel = NULL;

        QTableView *pTableView=it.value();



        pModel = (QStandardItemModel *)pTableView->model();



        if( pModel == NULL ) return;



        for( int row = 0; row < pModel->rowCount(); row++ )

        {

            QStandardItem *pItem = pModel->item( row, PARA_COL_NAME );

            if( pItem == NULL ) continue;



            if( pItem->checkState() != Qt::Checked )

            {

                pItem->setCheckState( Qt::Checked );

            }

        }

    }

}



void TermParamWindow::on_paraSelectNone_allPage( bool b )

{







    QMap<qint32,QTableView* >::iterator it=m_paramType_widget_map.begin();

    for(;it!=m_paramType_widget_map.end();++it)

    {

        QStandardItemModel *pModel = NULL;

        QTableView *pTableView=it.value();



        pModel = (QStandardItemModel *)pTableView->model();



        if( pModel == NULL ) return;



        for( int row = 0; row < pModel->rowCount(); row++ )

        {

            QStandardItem *pItem = pModel->item( row, PARA_COL_NAME );

            if( pItem == NULL ) continue;



            if( pItem->checkState() != Qt::Unchecked )

            {

                pItem->setCheckState( Qt::Unchecked );

            }

        }

    }

}



void TermParamWindow::on_paraSelectReverse_allPage(bool b )

{





    QMap<qint32,QTableView* >::iterator it=m_paramType_widget_map.begin();

    for(;it!=m_paramType_widget_map.end();++it)

    {

        QStandardItemModel *pModel = NULL;

        QTableView *pTableView=it.value();



        pModel = (QStandardItemModel *)pTableView->model();





        if( pModel == NULL ) return;



        for( int row = 0; row < pModel->rowCount(); row++ )

        {

            QStandardItem *pItem = pModel->item( row, PARA_COL_NAME );

            if( pItem == NULL ) continue;



            if( pItem->checkState() == Qt::Unchecked )

                pItem->setCheckState( Qt::Checked );

            else

                pItem->setCheckState( Qt::Unchecked );

        }

    }



}

void TermParamWindow::clicked_paraRightMenu( const QPoint &pos )

{

    QWidget * pCurPage = ui->tabWidget->currentWidget();



    QTableView *pTableView = NULL;



    QMap<qint32,QTableView* >::iterator it=m_paramType_widget_map.begin();

    for(;it!=m_paramType_widget_map.end();++it)

    {

        QTableView *tmppTableView=it.value();

        if(tmppTableView->parentWidget()==pCurPage)

            pTableView=tmppTableView;



    }

    /* if( ui->tableView_Para0->parentWidget() == pCurPage )

            pTableView = ui->tableView_Para0;

        else if( ui->tableView_Para1->parentWidget() == pCurPage )

            pTableView = ui->tableView_Para1;

        else if( ui->tableView_Para2->parentWidget() == pCurPage )

            pTableView = ui->tableView_Para2;

    */

    if( pTableView == NULL ) return;



    int col = pTableView->horizontalHeader()->logicalIndexAt( pos );



    if( col == PARA_COL_NAME )

    {

        if( m_paraRightMenu != NULL )

        {

            m_paraRightMenu->exec( QCursor::pos() );

        }

    }

}



void TermParamWindow::clear_param_rec()

{

    QList< QStandardItemModel * >   lstModel;

    /* lstModel.append( (QStandardItemModel *)ui->tableView_Para0->model() );

        lstModel.append( (QStandardItemModel *)ui->tableView_Para1->model() );

        lstModel.append( (QStandardItemModel *)ui->tableView_Para2->model() );*/

    QMap<qint32,QTableView* >::iterator it=m_paramType_widget_map.begin();

    for(;it!=m_paramType_widget_map.end();++it)

    {

        lstModel.append((QStandardItemModel *)it.value()->model());

    }



    QStandardItemModel *pModel;

    foreach( pModel, lstModel ) if( pModel != NULL ) pModel->removeRows( 0, pModel->rowCount() );



    /*ui->tableView_Para0->clearSelection();

        ui->tableView_Para0->resizeColumnsToContents();

        ui->tableView_Para0->update();



        ui->tableView_Para1->clearSelection();

        ui->tableView_Para1->resizeColumnsToContents();

        ui->tableView_Para1->update();



        ui->tableView_Para2->clearSelection();

        ui->tableView_Para2->resizeColumnsToContents();

        ui->tableView_Para2->update();*/

    QMap<qint32,QTableView* >::iterator it2=m_paramType_widget_map.begin();

    for(;it2!=m_paramType_widget_map.end();++it2)

    {

        QTableView *pTableView=it2.value();

        pTableView->clearSelection();

        updateWidget(pTableView);

    }



    ui->lineEdit_TermName->setText( tr("") );





}



void TermParamWindow::load_term( qint64 feeder_dev_id )

{

    bool bFlag=judgeTermPriv(PRIV_TERM_PARAM_SHOW,g_strLoginUserName);



    QListDevBaseInfo lstTermInfo;

    m_rtdbOper.GetTerminalUnitInfo( lstTermInfo, feeder_dev_id );



    int term_count = lstTermInfo.size();



    m_lstTermInfo.clear();



    for( int i = 0; i < lstTermInfo.size(); i++ )

    {

        DEV_BASE_INFO &info = lstTermInfo[i];



        DMS_TERMINAL_UNIT_INFO_STRUCT term = { 0 };

        bool bExist = m_rtdbOper.GetTerminalUnitInfo( term, info.id );

        if( bExist )

        {

            Q_ASSERT( term.struTerminalInfo.id == info.id );

            //if(m_localresptermId_lst.indexOf(term.struTerminalInfo.id)!=-1)//add by lxf

            if(bFlag)

            {

              m_lstTermInfo.append( term.struTerminalInfo );

            }

            else

            {

                if(term.struTerminalInfo.terminal_status!=0)

                {

                    m_lstTermInfo.append( term.struTerminalInfo );

                }

            }

        }

        else

        {

            Q_ASSERT( 0 );

        }

    }

}

void TermParamWindow::load_cb_mul( qint64  xh_id,qint64 feeder_dev_id )

{

    QList<qint64> termLst;

    if(m_xhId_termIdLst_map.find(xh_id)==m_xhId_termIdLst_map.end())

        return;

    termLst=m_xhId_termIdLst_map[xh_id];



    QListCbDevice lstCbDevice;

    m_rtdbOper.GetCbDevice( lstCbDevice );

    m_lstCbDevice.clear();

#if 0

    for( int i = 0; i < lstCbDevice.size(); i++ )

    {

        if(lstCbDevice[i].feeder_id==feeder_dev_id&&lstCbDevice[i].line_no>0&&lstCbDevice[i].term_id>0)

        {

            m_lstCbDevice.push_back(lstCbDevice[i]);

        }

    }

#endif



    for( int i = 0; i < lstCbDevice.size(); i++ )

    {

        if(feeder_dev_id==lstCbDevice[i].feeder_id&&termLst.indexOf(lstCbDevice[i].term_id)!=-1&&lstCbDevice[i].line_no>0&&lstCbDevice[i].term_id>0)

        {

            m_lstCbDevice.push_back(lstCbDevice[i]);

        }

    }

    cout<<"m_lstCbDevice ct=="<<m_lstCbDevice.count()<<" zz="<<termLst.count()<<endl;

    /* for(int zz=0;zz<termLst.count();++zz)

    {

        cout<<"term_id ct=="<<termLst[zz]<<endl;

    }*/



}

void TermParamWindow::load_term_mul( qint64 feeder_dev_id )

{

    m_lstTermInfo_mul.clear();

    m_rtdbOper.GetTerminalInfo(m_lstTermInfo_mul);

#if 0

    QListDevBaseInfo lstTermInfo;

    m_rtdbOper.GetTerminalUnitInfo( lstTermInfo, feeder_dev_id );



    int term_count = lstTermInfo.size();







    for( int i = 0; i < lstTermInfo.size(); i++ )

    {

        DEV_BASE_INFO &info = lstTermInfo[i];



        DMS_TERMINAL_UNIT_INFO_STRUCT term = { 0 };

        bool bExist = m_rtdbOper.GetTerminalUnitInfo( term, info.id );

        if( bExist )

        {

            Q_ASSERT( term.struTerminalInfo.id == info.id );



            m_lstTermInfo_mul.append( term.struTerminalInfo );

        }

        else

        {

            Q_ASSERT( 0 );

        }

    }

#endif

}

QString getTermStatusStr(const qint32 &status)

{

    switch( status )

    {

    case 1://投入



        return QString("投入");



    case 9://封锁投入

        return QString("封锁投入");

    case 3://保信厂站正常

        return QString("保信厂站正常");

    case 10://保信厂站封锁投入

        return QString("保信厂站封锁投入");

    case 4://退出

        return QString("退出");

    case 2://故障

        return QString("故障");

    case 12://封锁退出

        return QString("封锁退出");

    case 5://保信厂站故障

        return QString("保信厂站故障");

    case 11://保信厂站封锁退出

        return QString("保信厂站封锁退出");



    default:

        return QString("未知状态");



    }

}

void TermParamWindow::show_term_mul()

{

    QListInt32 lstStatus;

    get_checked_term_status( lstStatus );



    QListBigInt lstTermId;

    DMS_TERMINAL_INFO_STRUCT t;

    foreach( t, m_lstTermInfo_mul )

    {

        lstTermId.append( t.id );

    }

    QMap<qint64, quint8> mapStatus;

    m_rtdbOper.GetTermStatus( lstTermId, mapStatus );



    QMap<qint64,DMS_TERMINAL_INFO_STRUCT> termId_info_map;

    for( int z = 0; z < m_lstTermInfo_mul.size(); z++ )

    {

        DMS_TERMINAL_INFO_STRUCT &term = m_lstTermInfo_mul[z];

        //term.terminal_status不可用 这里的工况从13565表里取

        if( mapStatus.contains( term.id ) ) term.terminal_status = mapStatus.value( term.id );

        termId_info_map[term.id]=term;

    }











    HzToPy help;



    QStandardItemModel *pModel = NULL;

    QTableView  *pTableView=ui->tableView_term_mul;

    pModel=(QStandardItemModel *)pTableView->model();



    if( pModel != NULL ) pModel->removeRows(0,pModel->rowCount());



    for( int i = 0; i < m_lstCbDevice.size(); i++ )

    {

        DMS_CB_DEVICE_STRUCT  cb_info=m_lstCbDevice[i];

        if(termId_info_map.find(cb_info.term_id)==termId_info_map.end()) continue;

        const DMS_TERMINAL_INFO_STRUCT &term = termId_info_map[cb_info.term_id];







        int rowIdx = i;

        int colIdx = 0;



        QStandardItem *pItem = new QStandardItem( cb_info.name/*term.name*/ );

        pItem->setEditable(false);

        switch( term.terminal_status )

        {

        case 1://投入



            pItem->setData( QColor(0,150,0), Qt::BackgroundColorRole );

            pItem->setCheckable(true);

            break;

        case 9://封锁投入

        case 3://保信厂站正常

        case 10://保信厂站封锁投入



        case 4://退出

        case 2://故障

        case 12://封锁退出

        case 5://保信厂站故障

        case 11://保信厂站封锁退出

            pItem->setData(  QColor( 150, 0, 0 ) , Qt::BackgroundColorRole );

            break;

        default:

            pItem->setData( QColor( 255, 0, 255 ), Qt::BackgroundColorRole );

            break;

        }

        //pItem->setTextColor( QColor( 0, 0, 128 ) );

        pItem->setData( QVariant(term.id),Qt::UserRole+10 );

        pItem->setData(QVariant(term.terminal_status),Qt::UserRole+11);



        pItem->setData(QVariant(cb_info.line_no),Qt::UserRole+12);

        pItem->setToolTip( tr("终端名称=%1 line_no=%2 开关名称=%3 终端状态=%4").arg( term.name ).arg(cb_info.line_no).arg(cb_info.name).arg(getTermStatusStr(term.terminal_status)) );



        pItem->setData(QVariant(term.term_xh),Qt::UserRole+13);

        pItem->setData(QVariant(0),Qt::UserRole+14);//0 代表未召唤定值区  1代表已召唤 召唤成功 －1已召唤 召唤失败



        pModel->setItem( rowIdx, colIdx++, pItem );





        QStandardItem *pCallAreaValItem = new QStandardItem();

        pCallAreaValItem->setEditable(false);

        pModel->setItem( rowIdx, colIdx++, pCallAreaValItem );





        QStandardItem *pCallAreaItem = new QStandardItem();

        pCallAreaItem->setEditable(false);

        pCallAreaItem->setData(QVariant(-1),Qt::UserRole+10);//未召唤定值区

        pModel->setItem( rowIdx, colIdx++, pCallAreaItem );



#ifdef SHOW_MUL_CALL_RESULT

        QStandardItem *pCallItem = new QStandardItem();

        pCallItem->setEditable(false);

        pModel->setItem( rowIdx, colIdx++, pCallItem );

#endif



        QStandardItem *pDownloadItem = new QStandardItem();

        pDownloadItem->setEditable(false);

        pModel->setItem( rowIdx, colIdx++, pDownloadItem );



        QStandardItem *pActItem = new QStandardItem();

        pActItem->setEditable(false);

        pActItem->setData(QVariant(0),Qt::UserRole+14);//0 代表未激活  1代表已激活

        pModel->setItem( rowIdx, colIdx++, pActItem );





        QStandardItem *pStatusItem = new QStandardItem();

        pStatusItem->setEditable(false);

        pStatusItem->setToolTip(tr("%1").arg(term.terminal_status));

        pModel->setItem( rowIdx, colIdx++, pStatusItem );







    }



    // ui->tableView_term_mul->resizeColumnsToContents();

    //ui->tableView_term_mul->update();

}



void TermParamWindow::show_term()

{

    clear_line();

    m_termId_appNo.clear();

    QAbstractItemModel *pTermModel = ui->tableWidget_Term->model();

    if( pTermModel != NULL ) pTermModel->removeRows( 0, pTermModel->rowCount() );



    ui->tableWidget_Term->clearSelection();



    QListInt32 lstBrand;

    get_checked_term_brand( lstBrand );



    QListInt32 lstType;

    get_checked_term_type( lstType );



    QListInt32 lstStatus;

    get_checked_term_status( lstStatus );



    QListBigInt lstTermId;

    DMS_TERMINAL_INFO_STRUCT t;

    foreach( t, m_lstTermInfo )

    {

        lstTermId.append( t.id );

    }

    QMap<qint64, quint8> mapStatus;

    m_rtdbOper.GetTermStatus( lstTermId, mapStatus );



    for( int z = 0; z < m_lstTermInfo.size(); z++ )

    {

        DMS_TERMINAL_INFO_STRUCT &term = m_lstTermInfo[z];

        //term.terminal_status不可用 这里的工况从13565表里取

        if( mapStatus.contains( term.id ) ) term.terminal_status = mapStatus.value( term.id );

    }



    QListDmsTerminalInfo        lstShowTerm;



    DMS_TERMINAL_INFO_STRUCT term;

    foreach( term, m_lstTermInfo )

    {

        if( ! lstBrand.contains( (qint32)(term.factory) ) )     continue;

        if( ! lstType.contains( (qint32)(term.term_type) ) )    continue;

        if( ! lstStatus.contains( term.terminal_status ) )      continue;



        lstShowTerm.append( term );

    }



    m_TermLocate.Clear();



    HzToPy help;



    ui->tableWidget_Term->setRowCount( lstShowTerm.size() );



    for( int i = 0; i < lstShowTerm.size(); i++ )

    {

        const DMS_TERMINAL_INFO_STRUCT &term = lstShowTerm.at( i );



        QListCbDevice lstCB;

        load_CB_by_TermId( term.id, lstCB );



        int rowIdx = i;

        int colIdx = 0;



        QTableWidgetItem *pItem = new QTableWidgetItem( term.name );

        pItem->setTextColor( QColor( 0, 0, 128 ) );

        pItem->setData( Qt::UserRole+10, QVariant(term.id) );

        pItem->setToolTip( tr("终端%1").arg( D5000_KEYID( term.id ).ToString().c_str() ) );

        ui->tableWidget_Term->setItem( rowIdx, colIdx++, pItem );



        {

            SMatchItem mi;

            strcpy( mi.hz_code, term.name );

            help.TransHZIntoPY( mi.hz_code, mi.py_code );

            mi.order    = i;

            mi.userData = pItem;



            m_TermLocate.m_lstCache.append( mi );

        }



        pItem = new QTableWidgetItem( m_rtdbOper.GetSysMenuItemDisplayName2( TABLE_NO_DMS_TERMINAL_INFO, TABLE_NO_DMS_TERMINAL_INFO_FIELD_NAME_FACTORY, term.factory ) );

        ui->tableWidget_Term->setItem( rowIdx, colIdx++, pItem );



        QString strType = m_rtdbOper.GetSysMenuItemDisplayName2(TABLE_NO_DMS_TERMINAL_INFO, TABLE_NO_DMS_TERMINAL_INFO_FIELD_NAME_TERM_TYPE, term.term_type);

        //if( strType.isEmpty() ) strType = m_rtdbOper.GetSysMenuItemDisplayName2(TABLE_NO_DMS_TERMINAL_INFO, TABLE_NO_DMS_TERMINAL_INFO_FIELD_NAME_TERMINAL_TYPE, term.terminal_type);

        pItem = new QTableWidgetItem( strType );

        if( lstCB.size() == 1 )

        {

            DMS_CB_DEVICE_STRUCT &cb_rec = lstCB[0];

            strType = m_rtdbOper.GetSysMenuItemDisplayName2(TABLE_NO_DMS_CB_DEVICE, "brk_type", cb_rec.brk_type);

            if( ! strType.isEmpty() ) pItem->setToolTip( tr("下属开关类型：%1").arg(strType) );

        }

        ui->tableWidget_Term->setItem( rowIdx, colIdx++, pItem );



        QString strStausName = m_rtdbOper.GetSysMenuItemDisplayName2( TABLE_NO_DMS_COM_TERMINAL, 46/*通讯终端工况*/, term.terminal_status );

        pItem = new QTableWidgetItem( strStausName );



        switch( term.terminal_status )

        {

        case 1://投入

        case 9://封锁投入

        case 3://保信厂站正常

        case 10://保信厂站封锁投入

            pItem->setTextColor( QColor( 0, 64, 0 ) );

            break;

        case 4://退出

        case 2://故障

        case 12://封锁退出

        case 5://保信厂站故障

        case 11://保信厂站封锁退出

            pItem->setTextColor( QColor( 128, 0, 0 ) );

            break;

        default:

            pItem->setTextColor( QColor( 255, 0, 255 ) );

            break;

        }



        ui->tableWidget_Term->setItem( rowIdx, colIdx++, pItem );



#if 1

        //终端状态

        pItem = new QTableWidgetItem( term.para_6==1?tr("红图"):tr("黑图") );

        ui->tableWidget_Term->setItem( rowIdx, colIdx++, pItem );

        m_termId_appNo[term.id]=term.para_6;

#endif

    }



    ui->tableWidget_Term->resizeColumnsToContents();

    ui->tableWidget_Term->update();

}



void TermParamWindow::OnTermLocateTextChanged( const QString &strText )

{

    DoTermLocateTextChanged();

}



void TermParamWindow::DoTermLocateTextChanged()

{

    MyWaitCursor mwc;



    QString strLocate = ui->lineEdit_TermLocate->text().toLower();

    if( strLocate.isEmpty() || strLocate.length() == 0 ) return;







    TLocate &locate = m_TermLocate.m_locate;



    locate.Clear();



    SMatchItem item;

    foreach( item, m_TermLocate.m_lstCache )

    {

        QString strHzCode = QString::fromAscii( item.hz_code ).toLower();

        QString strPyCode = QString::fromAscii( item.py_code ).toLower();



        int idx_hz = strHzCode.indexOf( strLocate );

        int idx_py = strPyCode.indexOf( strLocate );



        if( idx_hz >= 0 || idx_py >= 0 )

        {

            if( idx_hz < 0 ) idx_hz = 0x7fffffff;

            if( idx_py < 0 ) idx_py = 0x7fffffff;



            SMatchItem mi( item );

            mi.index = ( ( idx_hz < idx_py ) ? idx_hz : idx_py );



            locate.Append( mi );

        }

    }



    locate.Sort();



    locate.ResetIndex();

}



void TermParamWindow::OnTermLocateReturnPressed()

{

    on_pushButton_TermGo_clicked();

}



void TermParamWindow::on_pushButton_TermGo_clicked()

{

    MyWaitCursor mwc;



    TLocate &locate = m_TermLocate.m_locate;



    void *userData = locate.UserData();

    if( userData != NULL )

    {

        locate.IncreaseIndex();



        //ui->tableWidget_Term->clearSelection();



        QTableWidgetItem *pItem = (QTableWidgetItem *)userData;

        if( pItem != NULL )

        {

            //pItem->setSelected( true );



            QItemSelectionModel::SelectionFlags command = QItemSelectionModel::Rows | QItemSelectionModel::ClearAndSelect;

            ui->tableWidget_Term->setCurrentItem( pItem, command );



            ui->tableWidget_Term->scrollToItem( pItem );

        }

    }

}



void TermParamWindow::on_splitterTree_splitterMoved(int pos, int index)

{

    m_treeWidget_width = ui->widgetTree->width();

}



void TermParamWindow::resizeEvent(QResizeEvent * e)

{

    if( e->size().width() > 1000 )

    {

        ui->splitter->setSizes( QList<int>() << m_treeWidget_width << e->size().width() - m_treeWidget_width );

    }



    if( e->size().width() > 1000 )

    {

        ui->splitter_3->setSizes( QList<int>() << m_treeWidget_width_mul << e->size().width() - m_treeWidget_width_mul );

    }

}



void TermParamWindow::OnFilterChange( int index, bool bChecked )

{

    show_term();



    DoTermLocateTextChanged();

}



void TermParamWindow::on_TermDoubleClicked( QTableWidgetItem *pItem )

{

    if( pItem == NULL ) return;



    int row = pItem->row();



    QTableWidgetItem *pDataItem = ui->tableWidget_Term->item( row, 0 );

    if( pDataItem == NULL ) return;



    qint64 term_dev_id = pDataItem->data( Qt::UserRole+10 ).toLongLong();



    load_line( term_dev_id );

}



void TermParamWindow::OnLineIndexChanged( int index )

{

    int line_no = get_cur_line_no();



    show_param_rec( m_term_dev_id, line_no );

}



void TermParamWindow::OnAreaIndexChanged( int index )

{

    clear_param();



    StartThread_ReadParamRec( m_term_dev_id, m_term_xh_id, get_cur_line_no(), get_cur_area_no() );

}



void TermParamWindow::load_line( qint64 term_dev_id )

{

    m_term_dev_id = term_dev_id;



    disconnect( ui->comboBox_Line, SIGNAL(currentIndexChanged(int)), this, SLOT(OnLineIndexChanged(int)) );



    m_lineNo_cbId_map.clear();

    ui->comboBox_Line->clear();

    ui->comboBox_Line->setCurrentIndex( -1 );



    QIcon term_icon = QIcon( tr(":/Resources/x13510.bmp") );

    QIcon cb_icon   = QIcon( tr(":/Resources/x13502.bmp") );



    int line_no = 1;

#if 0

    ui->comboBox_Line->addItem( term_icon, tr("终端本体"), QVariant( line_no ) );



    //法1 从“配网前置遥信定义表”找出终端下的遥信 再从“配网开关里”取“微机保护线路号”

    {

        QListBigInt lstCBId;

        load_CB_by_Term( term_dev_id, lstCBId );



        qint64 cb_id;

        foreach( cb_id, lstCBId )

        {

            DMS_CB_DEVICE_STRUCT cb_rec;

            //if( m_rtdbOper.GetCbDevice( cb_rec, cb_id ) )

            if( GetCbDevice( cb_rec, cb_id,m_termId_appNo[term_dev_id] ) )

            {

                if( cb_rec.line_no > 0 )    //线路号>0

                {

                    line_no = cb_rec.line_no + TERM_PARAM_LINE_NO_BASE;

                    ui->comboBox_Line->addItem( cb_icon, tr("%1 (%2)").arg(cb_rec.name).arg(cb_rec.line_no), QVariant( line_no ) );

                    m_lineNo_cbId_map[line_no]=cb_rec.id;

                }

            }

        }

    }

#endif

    int index=0;

    ui->comboBox_Line->insertItem(index, term_icon, tr("终端本体(1)"), QVariant( line_no ) );

    ui->comboBox_Line->setItemData(index, QVariant(m_term_dev_id), Qt::UserRole+10);



    //法1 从“配网前置遥信定义表”找出终端下的遥信 再从“配网开关里”取“微机保护线路号”

    {

        QListBigInt lstCBId;

        load_CB_by_Term( term_dev_id, lstCBId );



        qint64 cb_id;

        foreach( cb_id, lstCBId )

        {

            DMS_CB_DEVICE_STRUCT cb_rec;

            //if( m_rtdbOper.GetCbDevice( cb_rec, cb_id ) )

            if( GetCbDevice( cb_rec, cb_id,m_termId_appNo[term_dev_id] ) )

            {

#ifdef INCREASE_LINE_NO

                if( cb_rec.line_no >= 0 )    //线路号>0

#else

                if( cb_rec.line_no > 0 )    //线路号>0

#endif

                {

                    ++index;

#ifdef INCREASE_LINE_NO

                    line_no = cb_rec.line_no + TERM_PARAM_LINE_NO_BASE+1;

#else

                    line_no = cb_rec.line_no + TERM_PARAM_LINE_NO_BASE;

#endif

                    ui->comboBox_Line->insertItem(index, cb_icon, tr("%1 (%2)").arg(cb_rec.name).arg(cb_rec.line_no), QVariant( line_no ) );

                    ui->comboBox_Line->setItemData(index, QVariant(cb_rec.id), Qt::UserRole+10);

                    //m_lineNo_cbId_map[line_no]=cb_rec.id;

                }

            }

        }

    }



    //法2 直接从“配网开关表”中过滤 终端ID

    /*  {

        QListCbDevice lstCbDevice;

        load_CB_by_TermId( term_dev_id, lstCbDevice );



        DMS_CB_DEVICE_STRUCT cb_rec;

        foreach( cb_rec, lstCbDevice )

        {

            if( cb_rec.line_no > 0 )        //线路号>0

            {

                line_no = cb_rec.line_no + TERM_PARAM_LINE_NO_BASE;

                ui->comboBox_Line->addItem( cb_icon, tr("%1 (%2)").arg(cb_rec.name).arg(cb_rec.line_no), QVariant( line_no ) );

            }

        }

    }*/



    ui->comboBox_Line->setCurrentIndex( -1 );

    connect( ui->comboBox_Line, SIGNAL(currentIndexChanged(int)), this, SLOT(OnLineIndexChanged(int)) );



    if( ui->comboBox_Line->count() > 0 ) ui->comboBox_Line->setCurrentIndex( 0 );

}



void TermParamWindow::clear_line()

{

    ui->comboBox_Line->clear();

}



void TermParamWindow::load_CB_by_Term( qint64 term_dev_id, QListBigInt &lstCBId )

{

    lstCBId.clear();



    QListDmsChannelInfo lstDmsChannelInfo;

    m_rtdbOper.GetChannelInfo( lstDmsChannelInfo );



    DMS_CHANNEL_INFO_STRUCT info;

    QSet<qint64> cb_set;

    foreach( info, lstDmsChannelInfo )

    {

        if( info.terminal_id == term_dev_id )

        {

            qint64 chn_id = info.id;



            QListDmsFesYxDefine lstDmsFesYxDefine;

            m_rtdbOper.GetDmsFesYxDefine( lstDmsFesYxDefine );



            DMS_FES_YX_DEFINE_STRUCT yx_def;

            foreach( yx_def, lstDmsFesYxDefine )

            {

                if( yx_def.chan_id1 == chn_id )

                {

                    D5000_KEYID kid( yx_def.yx_id );

                    if( kid.table_no == TABLE_NO_DMS_CB_DEVICE )

                    {

                        kid.column_id = 0;

                        if(cb_set.find(kid.toValue())==cb_set.end())

                        {

                            cb_set.insert(kid.toValue());

                            lstCBId.append( kid.toValue() );

                        }

                    }

                }

            }



            break;

        }

    }

}



void TermParamWindow::load_CB_by_TermId( qint64 term_dev_id, QListCbDevice &lst )

{

    lst.clear();



    QListCbDevice lstCbDevice;

    m_rtdbOper.GetCbDevice( lstCbDevice );



    DMS_CB_DEVICE_STRUCT rec;

    foreach( rec, lstCbDevice )

    {

        if( rec.term_id == term_dev_id )

        {

            lst.append( rec );

        }

    }

}



void TermParamWindow::show_param_rec( qint64 term_dev_id, qint32 line_no )

{

    m_term_dev_id = term_dev_id;



    BeforeCallArea();



    DMS_TERMINAL_INFO_STRUCT term_info;

    if( ! m_rtdbOper.ReadTerminalInfo( term_info, m_term_dev_id ) )

    {

        m_term_dev_id = 0L;

        ui->lineEdit_TermName->setText( tr("") );



        ZMessageBox::warning( this, tr("id = %1").arg(term_dev_id), tr("查询配网终端信息失败...") );

        return;

    }



    ui->lineEdit_TermName->setText( term_info.name );



    m_term_xh_id = term_info.term_xh;



    QPalette palette;

    palette.setColor( QPalette::Text, ( m_term_xh_id > 0 ) ? QColor(0,64,0) : QColor(128,128,128) );

    ui->lineEdit_TermName->setPalette( palette );



    if( m_term_xh_id <= 0 )

    {

        ZMessageBox::warning( this, tr("ID=%1 %2").arg(term_dev_id).arg(D5000_KEYID(term_dev_id).ToString().c_str()), tr("配网终端 %1 \r\n未设置有效的终端型号，请检查...").arg(term_info.name) );

        return;

    }



    m_term_param_rec.clear();

    m_call_code_set.clear();

    if( ! m_rtdbOper.GetTermParam( m_term_xh_id, m_term_param_rec ) )

    {

        ZMessageBox::warning( this, tr("id = %1").arg(m_term_xh_id), tr("装载终端参数字典失败...") );

        return;

    }



    m_param_type_lst.clear();



    DMS_TERM_PARAM_DICT_STRUCT param_rec;

    foreach( param_rec, m_term_param_rec )

    {

        if(m_param_type_lst.indexOf(param_rec.param_type)==-1)

        {

            m_param_type_lst.push_back(param_rec.param_type);

        }

    }

    qSort(m_param_type_lst.begin(),m_param_type_lst.end());

    ui->tabWidget->clear();

    m_paramType_widget_map.clear();

    for(int ct=0;ct<m_param_type_lst.count();++ct)

    {

        QWidget *tab=new QWidget;

        QTableView *tabView=new QTableView;

        QVBoxLayout  *layout=new QVBoxLayout(tab);

        layout->addWidget(tabView);

        ui->tabWidget->addTab(tab,getParamTypeStr(m_param_type_lst[ct]));

        /* if(m_param_type_lst[ct]==2)

           {

               inittableView_Para( tabView, true );

           }

           else if(m_param_type_lst[ct]==1)

           {

               inittableView_YxPara( tabView);

           }

           else

          */

        {

            inittableView_Para( tabView, false );

        }

        m_paramType_widget_map[m_param_type_lst[ct]]=tabView;

    }



    int param_count = m_term_param_rec.size();



    //DMS_TERM_PARAM_DICT_STRUCT param_rec;

    m_FixedLocate.Clear();

    int iIndex=-1;

    HzToPy help;

    foreach( param_rec, m_term_param_rec )

    {

        if( line_no < TERM_PARAM_LINE_NO_BASE/*终端本体*/ )

        {

            if( /*param_rec.dev_flag != -1 &&*/ param_rec.dev_flag != 0 ) continue;

        }

        else/*配网开关.微机保护线路号 从1起始*/

        {

            if( /*param_rec.dev_flag != -1 &&*/ param_rec.dev_flag != 1 ) continue;

        }



        QStandardItemModel *pModel = NULL;

        QTableView  *pTableView=getWidgetByParamType(param_rec.param_type);

        pModel=(QStandardItemModel *)pTableView->model();

#if 0

        switch( param_rec.param_type )

        {

        case 0: //运行参数

            pModel = (QStandardItemModel *)ui->tableView_Para0->model();

            break;

        case 1: //动作定值

            pModel = (QStandardItemModel *)ui->tableView_Para1->model();;

            break;

        case 2: //固有参数

            pModel = (QStandardItemModel *)ui->tableView_Para2->model();

            break;

        default:

            ZMessageBox::warning( this, tr("终端参数类别").arg(m_term_xh_id), tr("未定义的终端参数类别 [%1] ...").arg(param_rec.param_type) );

            break;

        }

#endif

        if( pModel != NULL )

        {

            int row_idx = pModel->rowCount();

            int color_value=230;



#ifdef _DEBUG

            QStandardItem *pNameItem = new QStandardItem( tr("%1").arg( param_rec.name ) );

#else

            QStandardItem *pNameItem = new QStandardItem( tr("%1  ").arg( param_rec.name ) );

#endif

            pNameItem->setEditable( false );

            pNameItem->setCheckable( true );

            pNameItem->setData( QColor(232,232,232), Qt::BackgroundColorRole );

            if(param_rec.if_light)

                pNameItem->setData( QColor(0,color_value,0), Qt::BackgroundColorRole );

            int code = QString(param_rec.param_code).toInt( NULL, 16 );

            pNameItem->setData( QVariant( code ), Qt::UserRole+10 );

            pModel->setItem( row_idx, PARA_COL_NAME, pNameItem );



            SMatchItem mi;

            strcpy( mi.hz_code, param_rec.name );

            help.TransHZIntoPY( mi.hz_code, mi.py_code );

            mi.order    = ++iIndex;

            mi.userData = pNameItem;



            m_FixedLocate.m_lstCache.append( mi );









            QStandardItem *pCodeItem = new QStandardItem( tr("%1").arg(param_rec.param_code) );

            pCodeItem->setEditable( false );

            pCodeItem->setData( QColor(0,0,64), Qt::TextColorRole );

            if(param_rec.if_light)

                pCodeItem->setData( QColor(0,color_value,0), Qt::BackgroundColorRole );

            pModel->setItem( row_idx, PARA_COL_CODE, pCodeItem );



            QStandardItem *pValueItem = new QStandardItem( tr("") );

            pValueItem->setData( QVariant( param_rec.param_type ), Qt::UserRole+10 );

            if( param_rec.param_type == 0 || param_rec.param_type == 1 )

                pValueItem->setData( QColor(215,230,230), Qt::BackgroundColorRole );



            if(param_rec.if_light)

           pValueItem->setData( QColor(0,color_value,0), Qt::BackgroundColorRole );



            pModel->setItem( row_idx, PARA_COL_VALUE, pValueItem );



            QStandardItem *pUnitItem = new QStandardItem( param_rec.unit );

            pUnitItem->setEditable( false );

            if(param_rec.if_light)

                pUnitItem->setData( QColor(0,color_value,0), Qt::BackgroundColorRole );

            pModel->setItem( row_idx, PARA_COL_UNIT,pUnitItem );

#if 0

#define PARA_COL_LOCAL_VALUE_TYPE       4

#define PARA_COL_CAPP_VALUE_TYPE       5

#define PARA_COL_VALUE_TYPE_COMP       6

#endif



            QStandardItem *pLocalValTypeItem = new QStandardItem( getDataName(param_rec.data_type) );

            pLocalValTypeItem->setEditable( false );

            if(param_rec.if_light)

                pLocalValTypeItem->setData( QColor(0,color_value,0), Qt::BackgroundColorRole );

            pModel->setItem( row_idx, PARA_COL_LOCAL_VALUE_TYPE ,pLocalValTypeItem );



            QStandardItem *pCallValTypeItem = new QStandardItem(QString("") );

            pCallValTypeItem->setEditable( false );

            if(param_rec.if_light)

                pCallValTypeItem->setData( QColor(0,color_value,0), Qt::BackgroundColorRole );

            pModel->setItem( row_idx, PARA_COL_CALL_VALUE_TYPE,pCallValTypeItem );



            QStandardItem *pCompItem = new QStandardItem( QString("") );

            pCompItem->setEditable( false );

            if(param_rec.if_light)

                pCompItem->setData( QColor(0,color_value,0), Qt::BackgroundColorRole );

            pModel->setItem( row_idx, PARA_COL_VALUE_TYPE_COMP,pCompItem );



            QStandardItem *pRangeItem = new QStandardItem( param_rec.param_range );

            pRangeItem->setEditable( false );

            if(param_rec.if_light)

                pRangeItem->setData( QColor(0,color_value,0), Qt::BackgroundColorRole );

            pModel->setItem( row_idx, PARA_COL_RANGE, pRangeItem );





            cout<<"param_rec.step_len "<<param_rec.step_len<<endl;

            QStandardItem *pStepItem = new QStandardItem( tr("%1").arg(param_rec.step_len) );

            pStepItem->setEditable( false );

            if(param_rec.if_light)

                pStepItem->setData( QColor(0,color_value,0), Qt::BackgroundColorRole );

            pModel->setItem( row_idx, PARA_COL_STEP, pStepItem);



            QStandardItem *pModifyItem = new QStandardItem(param_rec.if_modify==1?tr("是"):tr("否"));

            pModifyItem->setEditable( false );

            pModifyItem->setData(QVariant( param_rec.if_modify ), Qt::UserRole+10 );

            if(param_rec.if_light)

                pModifyItem->setData( QColor(0,color_value,0), Qt::BackgroundColorRole );

            pModel->setItem( row_idx, PARA_COL_IF_MODIFY, pModifyItem );



            QStandardItem *pCallItem = new QStandardItem( tr("") );

            pCallItem->setEditable( false );

            if(param_rec.if_light)

                pCallItem->setData( QColor(0,color_value,0), Qt::BackgroundColorRole );

            pModel->setItem( row_idx, PARA_COL_TIME, pCallItem );



            QStandardItem *pResultItem = new QStandardItem( tr("") );

            pResultItem->setEditable( false );

            if(param_rec.if_light)

                pResultItem->setData( QColor(0,color_value,0), Qt::BackgroundColorRole );

            pModel->setItem( row_idx, PARA_COL_RESULT, pResultItem );



            QStandardItem *pCheckValueItem = new QStandardItem( tr("") );

            pCheckValueItem->setData( QVariant( param_rec.param_type ), Qt::UserRole+10 );

            if( param_rec.param_type == 0 || param_rec.param_type == 1 )

                pCheckValueItem->setData( QColor(215,230,230), Qt::BackgroundColorRole );

            if(param_rec.if_light)

                pCheckValueItem->setData( QColor(0,color_value,0), Qt::BackgroundColorRole );

            pModel->setItem( row_idx, PARA_COL_CHECK_VALUE, pCheckValueItem );



            QStandardItem *pCheckResultItem = new QStandardItem( tr("") );

            pCheckResultItem->setEditable( false );

            if(param_rec.if_light)

                pCheckResultItem->setData( QColor(0,color_value,0), Qt::BackgroundColorRole );

            pModel->setItem( row_idx, PARA_COL_CHECK_RESULT, pCheckResultItem );

        }

    }



    set_result_state( true );

#if 0

    ui->tableView_Para0->resizeColumnsToContents();

    ui->tableView_Para0->update();



    ui->tableView_Para1->resizeColumnsToContents();

    ui->tableView_Para1->update();



    ui->tableView_Para2->resizeColumnsToContents();

    ui->tableView_Para2->update();

#endif

    QMap<qint32,QTableView* >::iterator it=m_paramType_widget_map.begin();

    for(;it!=m_paramType_widget_map.end();++it)

    {

        updateWidget(it.value());

    }

    //开启一线程 读取参数值和上次召测时间

    StartThread_ReadParamRec( m_term_dev_id, m_term_xh_id, get_cur_line_no(), get_cur_area_no() );

}



void TermParamWindow::StartThread_ReadParamRec( qint64 term_dev_id, qint64 term_xh_id, qint32 line_no, qint32 area_no, qint32 delay_milsec )

{

    TermParam_Read_Thread *pReadThread = new TermParam_Read_Thread( this );

    this->moveToThread(pReadThread);

    pReadThread->init( term_dev_id, term_xh_id, line_no, area_no, delay_milsec );

    connect( pReadThread, SIGNAL(finished()), pReadThread, SLOT( deleteLater() ) );

    connect( pReadThread, SIGNAL(ReadFinished(QListTermParamRec &)), this, SLOT( OnParamReadFinished(QListTermParamRec &) ), Qt::DirectConnection );

    pReadThread->start();



    /*  QListTermParamRec lstRec;

    TermParam_Read_Thread::ReadParam( term_dev_id, term_xh_id, line_no, area_no, lstRec );

    OnParamReadFinished( lstRec );*/

}



void TermParamWindow::OnParamReadFinished( QListTermParamRec &lstRec )

{

    update_param( lstRec );

    update_oper_label( 121 );

}



void TermParamWindow::StartThread_WriteParamRec( QListTermParamRec &lstParamRec )

{

    TermParam_Write_Thread *pWriteThread = new TermParam_Write_Thread( this );

    pWriteThread->setUserName(g_strLoginUserName);

    connect( pWriteThread, SIGNAL(finished()), pWriteThread, SLOT( deleteLater() ) );

    connect( pWriteThread, SIGNAL(WriteFinished(int)), this, SLOT( OnParamWriteFinished(int) ), Qt::DirectConnection );

    pWriteThread->init( lstParamRec );

    pWriteThread->start();

}



void TermParamWindow::OnParamWriteFinished( int update_count )

{

    update_oper_label( 120 );



    StartThread_ReadParamRec( m_term_dev_id, m_term_xh_id, get_cur_line_no(), get_cur_area_no(), 1500 * 3 );

}



void TermParamWindow::StartProgressStep( short event_id )

{

    if( m_pProgressTimer != NULL ) return;



    m_start_time = time( NULL );

    m_reply_event = event_id;



    m_pProgressTimer = new QTimer( this );

    m_pProgressTimer->start( 200 );



    connect( m_pProgressTimer, SIGNAL( timeout() ), this, SLOT( OnProgressStep() ) );



    EnableCtrls( false );

    //add by lxf at 200509



    //if(!g_strLoginUserName.contains("操作员"))

#if 0

    if(m_op_user_set.find(g_strLoginUserName)==m_op_user_set.end())

    {

        //     QMessageBox::warning(this,"11111111111","22222222222222");

        ui->pushBtn_Write->setEnabled(false);;

        ui->pushBtn_Act->setEnabled(false);

    }

#endif

    //add end

    if( m_pProgressBar != NULL ) m_pProgressBar->setVisible( true );



    update_oper_label( 104 );



    update_oper_result( 104 );

}



void TermParamWindow::OnProgressStep()

{

    time_t cur_t = time( NULL );

    if( m_start_time > 0 && cur_t - m_start_time > m_oper_timeout_slice )   //超时

    {

        if( m_pProgressBar != NULL ) m_pProgressBar->setValue( m_oper_timeout_slice );

        update_progress_dlg( m_oper_timeout_slice );



        update_oper_label( 103 );

        update_oper_result( 103 );



        show_tip_finish( 103 );



        StopProgressStep();

        return;

    }

    else if( m_pProgressDlg != NULL && m_pProgressDlg->wasCanceled() )      //中止

    {

        if( m_pProgressBar != NULL ) m_pProgressBar->setValue( m_oper_timeout_slice );

        update_progress_dlg( m_oper_timeout_slice );



        update_oper_label( 106 );

        update_oper_result( 106 );



        show_tip_finish( 106 );



        StopProgressStep();

        return;

    }

    else

    {

        int step = ( time(NULL) - m_start_time );

        if( m_pProgressBar != NULL ) m_pProgressBar->setValue( step );

        update_progress_dlg( step );

    }

}



void TermParamWindow::StopProgressStep()

{

    m_reply_event   = 0;



    if( m_pProgressTimer != NULL )

    {

        m_pProgressTimer->stop();



        disconnect( m_pProgressTimer, SIGNAL( timeout() ), this, SLOT( OnProgressStep() ) );



        delete m_pProgressTimer;

        m_pProgressTimer = NULL;

    }



    EnableCtrls( true );

    //add by lxf at 200509



    //if(!g_strLoginUserName.contains("操作员"))

#if 0

    if(m_op_user_set.find(g_strLoginUserName)==m_op_user_set.end())

    {

        //     QMessageBox::warning(this,"11111111111","22222222222222");

        ui->pushBtn_Write->setEnabled(false);;

        ui->pushBtn_Act->setEnabled(false);

    }

#endif

    //add end

    if( m_pOperLabel != NULL ) m_pOperLabel->setText( tr("空闲") );

    if( m_pProgressBar != NULL ) m_pProgressBar->setVisible( false );



    if( m_pProgressDlg != NULL )

    {

        m_pProgressDlg->close();



        delete m_pProgressDlg;

        m_pProgressDlg = NULL;



        m_progress_idx = 0;

    }

}



void TermParamWindow::EnableCtrls( bool bEnable )

{

    this->setEnabled( bEnable );



    //if( m_pDevCtrl != NULL ) m_pDevCtrl->setEnabled( bEnable );

    ui->treeWidget->setEnabled( bEnable );



    ui->comboBox_Brand->setEnabled( bEnable );

    ui->comboBox_Type->setEnabled( bEnable );

    ui->comboBox_Status->setEnabled( bEnable );

    ui->comboBox_Line->setEnabled( bEnable );



    ui->tableWidget_Term->setEnabled( bEnable );

    ui->tableView_Para0->setEnabled( bEnable );

    ui->tableView_Para1->setEnabled( bEnable );

    ui->tableView_Para2->setEnabled( bEnable );



    ui->tabWidget->setEnabled( bEnable );



    ui->pushBtn_CallArea->setEnabled( bEnable );

    //ui->pushBtn_check->setEnabled(bEnable);

    //ui->pushBtn_AlterArea->setEnabled( bEnable );



    //ui->pushBtn_Read->setEnabled( bEnable );

    //ui->pushBtn_Write->setEnabled( bEnable );

    //ui->pushBtn_Act->setEnabled( bEnable );

}



void TermParamWindow::set_result_state( bool bUpdatable )

{

    QList< QStandardItemModel * >   lstModel;

    /*lstModel.append( (QStandardItemModel *)ui->tableView_Para0->model() );

        lstModel.append( (QStandardItemModel *)ui->tableView_Para1->model() );

        lstModel.append( (QStandardItemModel *)ui->tableView_Para2->model() );*/

    QMap<qint32,QTableView* >::iterator it=m_paramType_widget_map.begin();

    for(;it!=m_paramType_widget_map.end();++it)

    {

        lstModel.append((QStandardItemModel *)it.value()->model());

    }







    QStandardItemModel *pModel;

    foreach( pModel, lstModel )

    {

        if( pModel == NULL ) continue;



        for( int row = 0; row < pModel->rowCount(); row++ )

        {

            QStandardItem *pItem = pModel->item( row, PARA_COL_RESULT );

            if( pItem != NULL )

            {

                pItem->setData( QVariant( bUpdatable ), Qt::UserRole+10 );



                if( ! bUpdatable ) pItem->setText( tr("") );

            }

        }

    }

}



void TermParamWindow::set_check_result_state(bool bUpdatable)

{

    QList< QStandardItemModel * >   lstModel;

    /*lstModel.append( (QStandardItemModel *)ui->tableView_Para0->model() );

        lstModel.append( (QStandardItemModel *)ui->tableView_Para1->model() );

        lstModel.append( (QStandardItemModel *)ui->tableView_Para2->model() );*/

    QMap<qint32,QTableView* >::iterator it=m_paramType_widget_map.begin();

    for(;it!=m_paramType_widget_map.end();++it)

    {

        lstModel.append((QStandardItemModel *)it.value()->model());

    }







    QStandardItemModel *pModel;

    foreach( pModel, lstModel )

    {

        if( pModel == NULL ) continue;



        for( int row = 0; row < pModel->rowCount(); row++ )

        {

            QStandardItem *pItem = pModel->item( row, PARA_COL_CHECK_RESULT );

            if( pItem != NULL )

            {

                pItem->setData( QVariant( bUpdatable ), Qt::UserRole+10 );



                if( ! bUpdatable ) pItem->setText( tr("") );

            }



            QStandardItem *pCheckItem = pModel->item( row, PARA_COL_CHECK_VALUE );

            if(pCheckItem!=NULL)

            {

                pCheckItem->setText(tr(""));

            }

        }

    }

}



int TermParamWindow::get_cur_area_no()

{

#ifdef _SOUTHGRID_PROJECT_

    /*Q_ASSERT( ! ui->comboBox_Area->isVisible() );

    return 1;   //默认为1*/

#endif



    int area_no = -1;



    int idx = ui->comboBox_Area->currentIndex();

    if( idx >= 0 )

    {

        area_no = ui->comboBox_Area->itemData( idx ).toInt();

    }



    return area_no;

}



int TermParamWindow::get_cur_line_no()

{

#ifdef _SOUTHGRID_PROJECT_

    //return 1;   //默认为1

#endif



    int line_no = 0;



    int idx = ui->comboBox_Line->currentIndex();

    if( idx >= 0 )

    {

        line_no = ui->comboBox_Line->itemData( idx ).toInt();

    }



    return line_no;

}

QString TermParamWindow::get_cb_name()

{





    QString  str_name=tr("未找到");



    int idx = ui->comboBox_Line->currentIndex();

    if( idx >= 0 )

    {

        str_name = ui->comboBox_Line->itemText(idx);

    }



    qDebug()<<"str_name====="<<str_name;

    return str_name;

}

qint64 TermParamWindow::get_cb_id()

{





    qint64  cb_id=0L;



    int idx = ui->comboBox_Line->currentIndex();

    if( idx >= 0 )

    {

        cb_id = ui->comboBox_Line->itemData(idx,Qt::UserRole+10).toLongLong();

    }





    qDebug()<<"cb_id====="<<cb_id;

    return cb_id;

}

void TermParamWindow::BeforeCallArea()

{

    clear_area();

    clear_param_rec();



    QList< QWidget * >  lstCtrls;

    lstCtrls.append( ui->comboBox_Area );

    //lstCtrls.append( ui->pushBtn_AlterArea );//0725





    lstCtrls.append( ui->pushBtn_Read );

    lstCtrls.append( ui->pushBtn_Write );

    //lstCtrls.append( ui->pushBtn_Act );

    // lstCtrls.append(ui->pushBtn_check);



    QWidget *pW;

    foreach( pW, lstCtrls )

    {

        pW->setEnabled( false );

    }

}



void TermParamWindow::AfterCallArea()

{

    QList< QWidget * >  lstCtrls;

    lstCtrls.append( ui->comboBox_Area );

   // lstCtrls.append( ui->pushBtn_AlterArea );//0725





    lstCtrls.append( ui->pushBtn_Read );

    lstCtrls.append( ui->pushBtn_Write );

    // lstCtrls.append( ui->pushBtn_Act );

    //  lstCtrls.append(ui->pushBtn_check);



    QWidget *pW;

    foreach( pW, lstCtrls )

    {

        pW->setEnabled( true );

    }

}



bool TermParamWindow::init_term_read_para( T_Terminal_Para &para )

{

    int area_no = get_cur_area_no();

    int line_no = get_cur_line_no();



    if( line_no >= TERM_PARAM_LINE_NO_BASE ) line_no -= TERM_PARAM_LINE_NO_BASE;



    QList< QStandardItemModel * >   lstModel;

    /*lstModel.append( (QStandardItemModel *)ui->tableView_Para0->model() );

        lstModel.append( (QStandardItemModel *)ui->tableView_Para1->model() );

        lstModel.append( (QStandardItemModel *)ui->tableView_Para2->model() );*/

    QMap<qint32,QTableView* >::iterator it=m_paramType_widget_map.begin();

    for(;it!=m_paramType_widget_map.end();++it)

    {

        lstModel.append((QStandardItemModel *)it.value()->model());

    }





    cout<<CLogManager::Instance()->WriteLineT("召唤参数:");

    QStandardItemModel *pModel;

    foreach( pModel, lstModel )

    {

        if( pModel == NULL ) continue;



        for( int row = 0; row < pModel->rowCount(); row++ )

        {

            QStandardItem *pItem = pModel->item( row, PARA_COL_NAME );

            if( pItem == NULL ) continue;



            if( pItem->checkState() != Qt::Unchecked )

            {

                int code = pItem->data( Qt::UserRole+10 ).toInt();



                DMS_TERM_PARAM_DICT_STRUCT param_rec;

                foreach( param_rec, m_term_param_rec )

                {

                    if( QString(param_rec.param_code).toInt( NULL, 16 ) == code )

                    {

                        Terminal_Para_Info pi = { 0 };

                        pi.comm_fac_id  = m_term_dev_id;

                        pi.line_no      = line_no;

                        pi.serial_num   = area_no;

                        pi.para_type    = param_rec.param_type;

                        pi.value_type   = param_rec.data_type;

                        pi.data_type    = code;

                        pi.result       = 104;



                        para.terminal_para_seq.push_back( pi );

                        sprintf(g_log_buf,"comm_fac_id=%ld line_no=%d area_no=%d para_type=%d value_type=%d data_type=%d result=%d",

                                pi.comm_fac_id,pi.line_no,pi.serial_num,pi.para_type,pi.value_type,pi.data_type,pi.result);

                        cout<<CLogManager::Instance()->WriteLineT(g_log_buf);

                        //cg2018

                        //char szTip[64];

                        //sprintf( szTip, "选择%d    遍历%d %s", code, QString(param_rec.param_code).toInt( NULL, 16 ), param_rec.param_code );

                        //ZMessageBox::information( this, "", szTip );



                        break;

                    }

                }

            }

            else

            {

                QStandardItem *pResultItem = pModel->item( row, PARA_COL_RESULT );

                if( pResultItem != NULL && pResultItem->data( Qt::UserRole+10 ).toBool() ) pResultItem->setText( tr("") );

            }

        }

    }



    para.num = para.terminal_para_seq.size();



    return true;

}

bool TermParamWindow::readRecordHis(qint64 term_dev_id, qint64 term_xh_id, qint32 line_no, qint32 area_no,QMap<qint32,TermParamRec> &code_rec_map)

{

    QListTermParamRec lstRec;

    if(!TermParam_Read_Thread::ReadParam(term_dev_id,term_xh_id,line_no,area_no,lstRec))

    {

        return false;

    }

    for(int ct=0;ct<lstRec.count();++ct)

    {

        code_rec_map[lstRec[ct].m_code_10]=lstRec[ct];

    }

    return true;

}

qint32 TermParamWindow::init_term_write_para( guard_para_info_stru &para_info,T_Terminal_Para &para,int write_type,QList<TWarnData> &warn_data_lst )

{

    int area_no = get_cur_area_no();

    int line_no = get_cur_line_no();

    QString str_name=get_cb_name();

    para_info.cb_id=get_cb_id();

    QMap<qint32,TermParamRec> code_rec_map;

    readRecordHis(m_term_dev_id, m_term_xh_id, line_no, area_no,code_rec_map);//line_no 本体终端为1  开关为TERM_PARAM_LINE_NO_BASE+



    if( line_no >= TERM_PARAM_LINE_NO_BASE ) line_no -= TERM_PARAM_LINE_NO_BASE;



    QList< QStandardItemModel * >   lstModel;

    /*lstModel.append( (QStandardItemModel *)ui->tableView_Para0->model() );

       lstModel.append( (QStandardItemModel *)ui->tableView_Para1->model() );

       lstModel.append( (QStandardItemModel *)ui->tableView_Para2->model() );*/

    QMap<qint32,QTableView* >::iterator it=m_paramType_widget_map.begin();

    for(;it!=m_paramType_widget_map.end();++it)

    {

        lstModel.append((QStandardItemModel *)it.value()->model());

    }





    DMS_TERMINAL_INFO_STRUCT struDmsTerminalInfo;

    m_rtdbOper.ReadTerminalInfo(struDmsTerminalInfo,m_term_dev_id);

    CLogManager::Instance()->WriteLineT("下装参数:");

    QStandardItemModel *pModel;

    foreach( pModel, lstModel )

    {

        if( pModel == NULL ) continue;



        for( int row = 0; row < pModel->rowCount(); row++ )

        {

            QStandardItem *pItem = pModel->item( row, PARA_COL_NAME );

            if( pItem == NULL ) continue;



            if( pItem->checkState() != Qt::Unchecked )

            {

                int code = pItem->data( Qt::UserRole+10 ).toInt();



                QStandardItem *pModifyItem = pModel->item( row, PARA_COL_IF_MODIFY );

                if( pModifyItem == NULL ) continue;

                int if_modify=pModifyItem->data(Qt::UserRole+10).toInt();

                if(if_modify!=1)

                {

                    return -2;

                }



                DMS_TERM_PARAM_DICT_STRUCT param_rec;

                foreach( param_rec, m_term_param_rec )

                {

                    if( QString(param_rec.param_code).toInt( NULL, 16 ) == code )

                    {

                        Terminal_Para_Info pi = { 0 };

                        pi.comm_fac_id  = m_term_dev_id;

                        pi.line_no      = line_no;

                        pi.serial_num   = area_no;

                        pi.para_type    = param_rec.param_type;

                        pi.value_type   = param_rec.data_type;

                        pi.data_type    = code;

                        pi.result       = 104;



                        if(m_call_code_set.find(QString(param_rec.param_code).toInt( NULL, 16 ))==m_call_code_set.end())

                        {

                            return -5;

                        }

                        if(code_rec_map.find(code)==code_rec_map.end())

                        {

                            return -3;

                        }

                        if(write_type==1)

                        {

                            if(pModel->item( row, PARA_COL_ZD_SYS_VALUE )==NULL)

                            {

                                return -1;

                            }

                            if(!string_to_param_value( pModel->item( row, PARA_COL_ZD_SYS_VALUE )->text(), pi ))

                            {

                                return -1;

                            }

                        }

                        else if(write_type==2)

                        {

                            if(!string_to_param_value( pModel->item( row, PARA_COL_VALUE )->text(), pi ))

                            {

                                return -1;

                            }



                        }

                        if(param_rec.limit_num>0)

                        {

                            QString tmp_val_str=pModel->item( row, write_type==2?PARA_COL_VALUE:PARA_COL_ZD_SYS_VALUE )->text();

                            bool bFlag=true;

                            float tmp_val=tmp_val_str.trimmed().toFloat(&bFlag);

                            if(!bFlag) return -4;

                            if(param_rec.limit_num==1)

                            {

                                if(!(tmp_val>param_rec.limit_down_1&&tmp_val<param_rec.limit_up_1))

                                {

                                    sprintf(g_log_buf,"参数值不在区间范围内 不允许下装 当前值=%f 上限值1=%f 下限值1=%f ",

                                            tmp_val,param_rec.limit_up_1,param_rec.limit_down_1);

                                    CLogManager::Instance()->WriteLineT(g_log_buf);

                                    return -4;

                                }

                            }

                            else if(param_rec.limit_num==2)

                            {

                                bool bFlag1=true,bFlag2=true;

                                if(!(tmp_val>param_rec.limit_down_1&&tmp_val<param_rec.limit_up_1))

                                {

                                    bFlag1=false;

                                }



                                if(!(tmp_val>param_rec.limit_down_2&&tmp_val<param_rec.limit_up_2))

                                {

                                    bFlag2=false;

                                }

                                if(!bFlag1&&!bFlag2)

                                {

                                    sprintf(g_log_buf,"参数值不在区间范围内 不允许下装 当前值=%f 上限值1=%f 下限值1=%f 上限值2=%f 下限值2=%f",

                                            tmp_val,param_rec.limit_up_1,param_rec.limit_down_1,param_rec.limit_up_2,param_rec.limit_down_2);

                                    CLogManager::Instance()->WriteLineT(g_log_buf);

                                    return -4;

                                }

                            }

                            else if(param_rec.limit_num==3)

                            {

                                bool bFlag1=true,bFlag2=true,bFlag3=true;

                                if(!(tmp_val>param_rec.limit_down_1&&tmp_val<param_rec.limit_up_1))

                                {

                                    bFlag1=false;

                                }



                                if(!(tmp_val>param_rec.limit_down_2&&tmp_val<param_rec.limit_up_2))

                                {

                                    bFlag2=false;

                                }



                                if(!(tmp_val>param_rec.limit_down_3&&tmp_val<param_rec.limit_up_3))

                                {

                                    bFlag3=false;

                                }

                                if(!bFlag1&&!bFlag2&&!bFlag3)

                                {

                                    sprintf(g_log_buf,"参数值不在区间范围内 不允许下装 当前值=%f 上限值1=%f 下限值1=%f 上限值2=%f 下限值2=%f 上限值3=%f 下限值3=%f",

                                            tmp_val,param_rec.limit_up_1,param_rec.limit_down_1,param_rec.limit_up_2,param_rec.limit_down_2,param_rec.limit_up_3,param_rec.limit_down_3);

                                    CLogManager::Instance()->WriteLineT(g_log_buf);

                                    return -4;

                                }

                            }

                            else

                            {

                                sprintf(g_log_buf,"当前值转换失败 val=%s",

                                        tmp_val_str.toStdString().c_str());

                                CLogManager::Instance()->WriteLineT(g_log_buf);

                                return -4;

                            }

                        }





                        para.terminal_para_seq.push_back( pi );







                        TWarnData data;



                        //data.combined_id=struDmsTerminalInfo.feeder_id;

                        data.term_id=struDmsTerminalInfo.id;

                        data.feeder_id=struDmsTerminalInfo.feeder_id;

                        data.warn_type=MENU_OPT_PARA_DOWNLOAD;

                        data.dev_name=str_name;

                        data.code_10=pi.data_type;

                        QString  param_name_str=pItem->text();

                        QString  param_cur_value=write_type==1?pModel->item( row, PARA_COL_ZD_SYS_VALUE )->text():pModel->item( row, PARA_COL_VALUE)->text();

                        QString param_change_value=code_rec_map[code].m_value;



                        para_info_stru tmp_para;

                        tmp_para.param_name=param_name_str;

                        tmp_para.ori_value=param_change_value;

                        tmp_para.modify_value=param_cur_value;

                        para_info.lstPara.push_back(tmp_para);



                        sprintf(g_log_buf,"comm_fac_id=%ld line_no=%d area_no=%d para_type=%d value_type=%d data_type=%d result=%d value=%s",

                                pi.comm_fac_id,pi.line_no,pi.serial_num,pi.para_type,pi.value_type,pi.data_type,pi.result,param_cur_value.toStdString().c_str());

                        CLogManager::Instance()->WriteLineT(g_log_buf);

                        QString op_str=QString(" 参数名称:%1 信息体:%2 由原值:%3  改为:%4").arg(param_name_str).arg(code).arg(param_change_value).arg(param_cur_value);

                        data.op_str+=(write_type==1?"整定系统":"");

                        data.op_str+="下装参数 []"+op_str.toStdString();

                        // sendWarn(data);

                        warn_data_lst.push_back(data);

                        break;

                    }

                }

            }

            else

            {

                QStandardItem *pResultItem = pModel->item( row, PARA_COL_RESULT );

                if( pResultItem != NULL && pResultItem->data( Qt::UserRole+10 ).toBool() ) pResultItem->setText( tr("") );

            }

        }

    }



    para.num = para.terminal_para_seq.size();



    return 1;

}



bool TermParamWindow::init_term_op_para( T_Terminal_Op &para )

{

    int area_no = get_cur_area_no();

    int line_no = get_cur_line_no();

    if( line_no >= TERM_PARAM_LINE_NO_BASE ) line_no -= TERM_PARAM_LINE_NO_BASE;



    Terminal_Para_Op item = { 0 };

    item.comm_fac_id    = m_term_dev_id;

    item.line_no        = line_no;

    item.serial_num     = area_no;

    item.para_type      = 0;    //0，运行参数 1，动作定值参数，

    //文档中有此说明，但前置未处理，即同时激活运行参数和动作定值



    para.terminal_op_seq.push_back( item );

    para.num = para.terminal_op_seq.size();



    return 0;

}



bool TermParamWindow::init_term_act_para(const QSet<int> &code_set,T_Terminal_Para &para,QList<TWarnData>  &warn_data_lst)//用于激活时结果显示

{

    int area_no = get_cur_area_no();

    int line_no = get_cur_line_no();

    QString str_name=get_cb_name();



    QMap<qint32,TermParamRec> code_rec_map;

    readRecordHis(m_term_dev_id, m_term_xh_id, line_no, area_no,code_rec_map);//line_no 本体终端为1  开关为TERM_PARAM_LINE_NO_BASE+



    if( line_no >= TERM_PARAM_LINE_NO_BASE ) line_no -= TERM_PARAM_LINE_NO_BASE;



    QList< QStandardItemModel * >   lstModel;

    /*lstModel.append( (QStandardItemModel *)ui->tableView_Para0->model() );

           lstModel.append( (QStandardItemModel *)ui->tableView_Para1->model() );

           lstModel.append( (QStandardItemModel *)ui->tableView_Para2->model() );*/

    QMap<qint32,QTableView* >::iterator it=m_paramType_widget_map.begin();

    for(;it!=m_paramType_widget_map.end();++it)

    {

        lstModel.append((QStandardItemModel *)it.value()->model());

    }





    DMS_TERMINAL_INFO_STRUCT struDmsTerminalInfo;

    m_rtdbOper.ReadTerminalInfo(struDmsTerminalInfo,m_term_dev_id);

    CLogManager::Instance()->WriteLineT("激活参数:");

    QStandardItemModel *pModel;

    foreach( pModel, lstModel )

    {

        if( pModel == NULL ) continue;



        for( int row = 0; row < pModel->rowCount(); row++ )

        {

            QStandardItem *pItem = pModel->item( row, PARA_COL_NAME );

            if( pItem == NULL ) continue;



            if( pItem->checkState() != Qt::Unchecked )

            {

                int code = pItem->data( Qt::UserRole+10 ).toInt();

                if(code_set.find(code)==code_set.end())

                    continue;



                QStandardItem *pModifyItem = pModel->item( row, PARA_COL_IF_MODIFY );

                if( pModifyItem == NULL ) continue;

                int if_modify=pModifyItem->data(Qt::UserRole+10).toInt();

                if(if_modify!=1)

                {

                    sprintf(g_log_buf,"激活失败包含有不允许修改的参数...");

                    CLogManager::Instance()->WriteLineT(g_log_buf);

                    QMessageBox::warning( this, tr("id = %1").arg(m_term_dev_id), tr("激活失败包含有不允许修改的参数...") );

                    return false;

                }



                DMS_TERM_PARAM_DICT_STRUCT param_rec;

                foreach( param_rec, m_term_param_rec )

                {

                    if( QString(param_rec.param_code).toInt( NULL, 16 ) == code )

                    {

                        Terminal_Para_Info pi = { 0 };

                        pi.comm_fac_id  = m_term_dev_id;

                        pi.line_no      = line_no;

                        pi.serial_num   = area_no;

                        pi.para_type    = param_rec.param_type;

                        pi.value_type   = param_rec.data_type;

                        pi.data_type    = code;

                        pi.result       = 104;



                        if(code_rec_map.find(code)==code_rec_map.end())

                        {

                            sprintf(g_log_buf,"获取当前定值数据失败...");

                            CLogManager::Instance()->WriteLineT(g_log_buf);

                            QMessageBox::warning( this, tr("id = %1").arg(m_term_dev_id), tr("获取当前定值数据失败...") );

                            return false;

                        }

                        if(m_write_type==1)

                        {

                            if(!string_to_param_value( pModel->item( row, PARA_COL_ZD_SYS_VALUE )->text(), pi ))

                            {

                                sprintf(g_log_buf,"参数转换失败 不允许激活...");

                                CLogManager::Instance()->WriteLineT(g_log_buf);

                                QMessageBox::warning( this, tr("id = %1").arg(m_term_dev_id), tr("参数转换失败 不允许激活...") );

                                return false;

                            }

                        }

                        else if(m_write_type==2)

                        {

                            if(!string_to_param_value( pModel->item( row, PARA_COL_VALUE )->text(), pi ))

                            {

                                sprintf(g_log_buf,"参数转换失败 不允许激活...");

                                CLogManager::Instance()->WriteLineT(g_log_buf);

                                QMessageBox::warning( this, tr("id = %1").arg(m_term_dev_id), tr("参数转换失败 不允许激活...") );

                                return false;

                            }



                        }





                        para.terminal_para_seq.push_back( pi );



                        TWarnData data;

                        data.dev_name=str_name;

                        //data.combined_id=struDmsTerminalInfo.feeder_id;

                        data.term_id=struDmsTerminalInfo.id;

                        data.feeder_id=struDmsTerminalInfo.feeder_id;

                        data.warn_type=MENU_OPT_PARA_DOWNLOAD;

                        QString  param_name_str=pItem->text();

                        QString  param_cur_value=m_write_type==1?pModel->item( row, PARA_COL_ZD_SYS_VALUE )->text():pModel->item( row, PARA_COL_VALUE)->text();

                        QString param_change_value=code_rec_map[code].m_value;



                        sprintf(g_log_buf,"comm_fac_id=%ld line_no=%d area_no=%d para_type=%d value_type=%d data_type=%d result=%d value=%s",

                                pi.comm_fac_id,pi.line_no,pi.serial_num,pi.para_type,pi.value_type,pi.data_type,pi.result,param_cur_value.toStdString().c_str());

                        CLogManager::Instance()->WriteLineT(g_log_buf);

                        QString op_str=QString(" 参数名称:%1 信息体:%2 由原值:%3  改为:%4").arg(param_name_str).arg(code).arg(param_change_value).arg(param_cur_value);

                        data.op_str+=(m_write_type==1?"整定系统":"");

                        data.op_str+="激活参数 []"+op_str.toStdString();

                        // sendWarn(data);

                        warn_data_lst.push_back(data);

                        break;

                    }

                }

            }

            /*else

            {

                QStandardItem *pResultItem = pModel->item( row, PARA_COL_RESULT );

                if( pResultItem != NULL && pResultItem->data( Qt::UserRole+10 ).toBool() ) pResultItem->setText( tr("") );

            }*/

        }

    }



    para.num = para.terminal_para_seq.size();



    return true;

#if 0

    int area_no = get_cur_area_no();

    int line_no = get_cur_line_no();

    //  if( line_no >= TERM_PARAM_LINE_NO_BASE ) line_no -= TERM_PARAM_LINE_NO_BASE;



    QList< QStandardItemModel * >   lstModel;

    /*lstModel.append( (QStandardItemModel *)ui->tableView_Para0->model() );

       lstModel.append( (QStandardItemModel *)ui->tableView_Para1->model() );

       lstModel.append( (QStandardItemModel *)ui->tableView_Para2->model() );*/

    QMap<qint32,QTableView* >::iterator it=m_paramType_widget_map.begin();

    for(;it!=m_paramType_widget_map.end();++it)

    {

        lstModel.append((QStandardItemModel *)it.value()->model());

    }







    QStandardItemModel *pModel;

    foreach( pModel, lstModel )

    {

        if( pModel == NULL ) continue;



        for( int row = 0; row < pModel->rowCount(); row++ )

        {

            QStandardItem *pItem = pModel->item( row, PARA_COL_NAME );

            if( pItem == NULL ) continue;



            if( pItem->checkState() != Qt::Unchecked )

            {

                int code = pItem->data( Qt::UserRole+10 ).toInt();



                DMS_TERM_PARAM_DICT_STRUCT param_rec;

                foreach( param_rec, m_term_param_rec )

                {

                    if( QString(param_rec.param_code).toInt( NULL, 16 ) == code )

                    {

                        Terminal_Para_Info pi = { 0 };

                        pi.comm_fac_id  = m_term_dev_id;

                        pi.line_no      = line_no;

                        pi.serial_num   = area_no;

                        pi.para_type    = param_rec.param_type;

                        pi.value_type   = param_rec.data_type;

                        pi.data_type    = code;

                        pi.result       = 104;

                        string_to_param_value( pModel->item( row, PARA_COL_VALUE )->text(), pi );

                        para.terminal_para_seq.push_back( pi );

                        break;

                    }

                }

            }



        }

    }



    para.num = para.terminal_para_seq.size();

#endif

    return true;

}



bool TermParamWindow::init_term_check_para(const QSet<int> &code_set,T_Terminal_Para &para)

{

    int area_no = get_cur_area_no();

    int line_no = get_cur_line_no();



    if( line_no >= TERM_PARAM_LINE_NO_BASE ) line_no -= TERM_PARAM_LINE_NO_BASE;



    QList< QStandardItemModel * >   lstModel;

    /*lstModel.append( (QStandardItemModel *)ui->tableView_Para0->model() );

       lstModel.append( (QStandardItemModel *)ui->tableView_Para1->model() );

       lstModel.append( (QStandardItemModel *)ui->tableView_Para2->model() );*/

    QMap<qint32,QTableView* >::iterator it=m_paramType_widget_map.begin();

    for(;it!=m_paramType_widget_map.end();++it)

    {

        lstModel.append((QStandardItemModel *)it.value()->model());

    }



    CLogManager::Instance()->WriteLineT("校验参数:");

    QStandardItemModel *pModel;

    foreach( pModel, lstModel )

    {

        if( pModel == NULL ) continue;



        for( int row = 0; row < pModel->rowCount(); row++ )

        {

            QStandardItem *pItem = pModel->item( row, PARA_COL_NAME );

            if( pItem == NULL ) continue;



            if( pItem->checkState() != Qt::Unchecked )

            {

                int code = pItem->data( Qt::UserRole+10 ).toInt();

                if(code_set.find(code)==code_set.end())

                    continue;



                DMS_TERM_PARAM_DICT_STRUCT param_rec;

                foreach( param_rec, m_term_param_rec )

                {

                    if( QString(param_rec.param_code).toInt( NULL, 16 ) == code )

                    {

                        Terminal_Para_Info pi = { 0 };

                        pi.comm_fac_id  = m_term_dev_id;

                        pi.line_no      = line_no;

                        pi.serial_num   = area_no;

                        pi.para_type    = param_rec.param_type;

                        pi.value_type   = param_rec.data_type;

                        pi.data_type    = code;

                        pi.result       = 104;

                        para.terminal_para_seq.push_back( pi );

                        sprintf(g_log_buf,"comm_fac_id=%ld line_no=%d area_no=%d para_type=%d value_type=%d data_type=%d result=%d ",

                                pi.comm_fac_id,pi.line_no,pi.serial_num,pi.para_type,pi.value_type,pi.data_type,pi.result);

                        CLogManager::Instance()->WriteLineT(g_log_buf);

                        break;

                    }

                }

            }



        }

    }



    para.num = para.terminal_para_seq.size();



    return true;

}



void TermParamWindow::show_reply_para( T_Terminal_Para &para )

{

    update_oper_result( 103 );  //将当前选中的参数统一设为“超时”，召唤成功的再逐个设为“成功”。

    //有可能召唤10条，返回8条，则另2条应显示为“超时”

    //cg20190508



    int count = para.terminal_para_seq.size();

    Q_ASSERT( para.num == count );



    for( int i = 0; i < para.terminal_para_seq.size(); i++ )

    {

        Terminal_Para_Info &info = para.terminal_para_seq[i];



        show_para_info( info );



        update_para_info( info );



        update_oper_result( info.data_type, 10/*info.result*/ );  //召唤的返回结果不可用

    }

}



void TermParamWindow::update_reply_para( T_Terminal_Para &para )

{

    int count = para.terminal_para_seq.size();

    //Q_ASSERT( para.num == count );



    int area_no = get_cur_area_no();

    int line_no = get_cur_line_no();



    QListTermParamRec lstParamRec;



    for( int m = 0; m < para.terminal_para_seq.size(); m++ )

    {

        Terminal_Para_Info &info = para.terminal_para_seq[m];



        for( int n = 0; n < m_term_param_rec.size(); n++ )

        {

            DMS_TERM_PARAM_DICT_STRUCT &param_rec = m_term_param_rec[n];

            if( QString(param_rec.param_code).toInt( NULL, 16 ) == info.data_type )

            {

                Q_ASSERT( param_rec.xh_id == m_term_xh_id );



                TermParamRec update_rec;

                update_rec.m_term_id        = this->m_term_dev_id;                              //终端ID

                update_rec.m_xh_id          = this->m_term_xh_id;                               //型号ID

                update_rec.m_line_no        = line_no;                                          //微机保护线路号

                update_rec.m_area           = area_no;//info.serial_num;                        //定值区 这里取召唤的定值区号 因为上送的有问题

                update_rec.m_code           = QString::fromStdString( param_rec.param_code );   //参数代码(16进制不带0x)

                update_rec.m_code_10        = update_rec.m_code.toInt( NULL, 16 );              //10进制参数代码

                update_rec.m_name           = QString::fromStdString(param_rec.name );          //参数名称

                update_rec.m_type           = param_rec.param_type;                             //参数类别(0-运行参数;1-动作定值;2-固有参数)

                info.para_type              = param_rec.param_type;     //上送的参数类别不可信 以定义的为准

                update_rec.m_value          = param_value_to_string( info );                    //参数值

                update_rec.m_data_type      = param_rec.data_type;                              //参数值数据类型(1-类型不确定;2-int;3-float;4-short;5-unsigned short;6-unsigned int;7-string)

                update_rec.m_unit           = QString::fromStdString( param_rec.unit );         //参数单位

                update_rec.m_coe            = param_rec.coe;                                    //参数系数

                update_rec.m_data_source    = 1;                                                //数据来源(1-1区;2-四区;3-其他)

                update_rec.m_update_time    = QDateTime::currentDateTime();                     //最近更新时间



                lstParamRec.append( update_rec );



                break;

            }

        }

    }



    StartThread_WriteParamRec( lstParamRec );

}



void TermParamWindow::update_sheet_reply_para( T_Terminal_Para &para )//工单召唤校验都用这个函数

{

    int count = para.terminal_para_seq.size();

    //Q_ASSERT( para.num == count );





    QListTermParamRec lstParamRec;



    for( int m = 0; m < para.terminal_para_seq.size(); m++ )

    {

        Terminal_Para_Info &info = para.terminal_para_seq[m];



        for( int n = 0; n < m_sheet_term_param_rec.size(); n++ )

        {

            DMS_TERM_PARAM_DICT_STRUCT &param_rec = m_sheet_term_param_rec[n];

            if( QString(param_rec.param_code).toInt( NULL, 16 ) == info.data_type )

            {

                Q_ASSERT( param_rec.xh_id == m_sheet_term_xh_id );



                TermParamRec update_rec;

                update_rec.m_term_id        = this->m_sheet_term_dev_id;                              //终端ID

                update_rec.m_xh_id          = this->m_sheet_term_xh_id;                               //型号ID

                update_rec.m_line_no        = m_sheet_line_no;                                          //微机保护线路号

                update_rec.m_area           = m_sheet_area_no;//info.serial_num;                        //定值区 这里取召唤的定值区号 因为上送的有问题

                update_rec.m_code           = QString::fromStdString( param_rec.param_code );   //参数代码(16进制不带0x)

                update_rec.m_code_10        = update_rec.m_code.toInt( NULL, 16 );              //10进制参数代码

                update_rec.m_name           = QString::fromStdString(param_rec.name );          //参数名称

                update_rec.m_type           = param_rec.param_type;                             //参数类别(0-运行参数;1-动作定值;2-固有参数)

                info.para_type              = param_rec.param_type;     //上送的参数类别不可信 以定义的为准

                update_rec.m_value          = param_value_to_string( info );                    //参数值

                update_rec.m_data_type      = param_rec.data_type;                              //参数值数据类型(1-类型不确定;2-int;3-float;4-short;5-unsigned short;6-unsigned int;7-string)

                update_rec.m_unit           = QString::fromStdString( param_rec.unit );         //参数单位

                update_rec.m_coe            = param_rec.coe;                                    //参数系数

                update_rec.m_data_source    = 2;                                                //数据来源(1-1区;2-四区;3-其他)

                update_rec.m_update_time    = QDateTime::currentDateTime();                     //最近更新时间



                lstParamRec.append( update_rec );



                break;

            }

        }

    }



    //校验召唤上来的参数写库 但不更新界面

    TermParam_Write_Thread *pWriteThread = new TermParam_Write_Thread( this );

    pWriteThread->setUserName(g_strLoginUserName);

    connect( pWriteThread, SIGNAL(finished()), pWriteThread, SLOT( deleteLater() ) );

    // connect( pWriteThread, SIGNAL(WriteFinished(int)), this, SLOT( OnParamWriteFinished(int) ), Qt::DirectConnection );

    pWriteThread->init( lstParamRec );

    pWriteThread->start();

}

void TermParamWindow::update_reply_check_para( T_Terminal_Para &para )

{

    int count = para.terminal_para_seq.size();

    //Q_ASSERT( para.num == count );



    int area_no = get_cur_area_no();

    int line_no = get_cur_line_no();



    QListTermParamRec lstParamRec;



    for( int m = 0; m < para.terminal_para_seq.size(); m++ )

    {

        Terminal_Para_Info &info = para.terminal_para_seq[m];



        for( int n = 0; n < m_term_param_rec.size(); n++ )

        {

            DMS_TERM_PARAM_DICT_STRUCT &param_rec = m_term_param_rec[n];

            if( QString(param_rec.param_code).toInt( NULL, 16 ) == info.data_type )

            {

                Q_ASSERT( param_rec.xh_id == m_term_xh_id );



                TermParamRec update_rec;

                update_rec.m_term_id        = this->m_term_dev_id;                              //终端ID

                update_rec.m_xh_id          = this->m_term_xh_id;                               //型号ID

                update_rec.m_line_no        = line_no;                                          //微机保护线路号

                update_rec.m_area           = area_no;//info.serial_num;                        //定值区 这里取召唤的定值区号 因为上送的有问题

                update_rec.m_code           = QString::fromStdString( param_rec.param_code );   //参数代码(16进制不带0x)

                update_rec.m_code_10        = update_rec.m_code.toInt( NULL, 16 );              //10进制参数代码

                update_rec.m_name           = QString::fromStdString(param_rec.name );          //参数名称

                update_rec.m_type           = param_rec.param_type;                             //参数类别(0-运行参数;1-动作定值;2-固有参数)

                info.para_type              = param_rec.param_type;     //上送的参数类别不可信 以定义的为准

                update_rec.m_value          = param_value_to_string( info );                    //参数值

                update_rec.m_data_type      = param_rec.data_type;                              //参数值数据类型(1-类型不确定;2-int;3-float;4-short;5-unsigned short;6-unsigned int;7-string)

                update_rec.m_unit           = QString::fromStdString( param_rec.unit );         //参数单位

                update_rec.m_coe            = param_rec.coe;                                    //参数系数

                update_rec.m_data_source    = 1;                                                //数据来源(1-1区;2-四区;3-其他)

                update_rec.m_update_time    = QDateTime::currentDateTime();                     //最近更新时间



                lstParamRec.append( update_rec );



                break;

            }

        }

    }

    //校验召唤上来的参数写库 但不更新界面

    TermParam_Write_Thread *pWriteThread = new TermParam_Write_Thread( this );

    pWriteThread->setUserName(g_strLoginUserName);

    connect( pWriteThread, SIGNAL(finished()), pWriteThread, SLOT( deleteLater() ) );

    // connect( pWriteThread, SIGNAL(WriteFinished(int)), this, SLOT( OnParamWriteFinished(int) ), Qt::DirectConnection );

    pWriteThread->init( lstParamRec );

    pWriteThread->start();

}

void TermParamWindow::update_reply_act_para( T_Terminal_Para &para )

{

    int count = para.terminal_para_seq.size();

    //Q_ASSERT( para.num == count );



    int area_no = get_cur_area_no();

    int line_no = get_cur_line_no();



    QListTermParamRec lstParamRec;



    for( int m = 0; m < para.terminal_para_seq.size(); m++ )

    {

        Terminal_Para_Info &info = para.terminal_para_seq[m];



        for( int n = 0; n < m_term_param_rec.size(); n++ )

        {

            DMS_TERM_PARAM_DICT_STRUCT &param_rec = m_term_param_rec[n];

            if( QString(param_rec.param_code).toInt( NULL, 16 ) == info.data_type )

            {

                Q_ASSERT( param_rec.xh_id == m_term_xh_id );



                TermParamRec update_rec;

                update_rec.m_term_id        = this->m_term_dev_id;                              //终端ID

                update_rec.m_xh_id          = this->m_term_xh_id;                               //型号ID

                update_rec.m_line_no        = line_no;                                          //微机保护线路号

                update_rec.m_area           = area_no;//info.serial_num;                        //定值区 这里取召唤的定值区号 因为上送的有问题

                update_rec.m_code           = QString::fromStdString( param_rec.param_code );   //参数代码(16进制不带0x)

                update_rec.m_code_10        = update_rec.m_code.toInt( NULL, 16 );              //10进制参数代码

                update_rec.m_name           = QString::fromStdString(param_rec.name );          //参数名称

                update_rec.m_type           = param_rec.param_type;                             //参数类别(0-运行参数;1-动作定值;2-固有参数)

                info.para_type              = param_rec.param_type;     //上送的参数类别不可信 以定义的为准

                update_rec.m_value          = param_value_to_string( info );                    //参数值

                update_rec.m_data_type      = param_rec.data_type;                              //参数值数据类型(1-类型不确定;2-int;3-float;4-short;5-unsigned short;6-unsigned int;7-string)

                update_rec.m_unit           = QString::fromStdString( param_rec.unit );         //参数单位

                update_rec.m_coe            = param_rec.coe;                                    //参数系数

                update_rec.m_data_source    = 1;                                                //数据来源(1-1区;2-四区;3-其他)

                update_rec.m_update_time    = QDateTime::currentDateTime();                     //最近更新时间



                lstParamRec.append( update_rec );



                break;

            }

        }

    }

    //激活写入历史库



    TermParam_Write_Thread::InsertParamHis(lstParamRec,g_strLoginUserName);



}

void TermParamWindow::insertParamHisByPara(Terminal_Para_Info info,const int &op_type,const int& op_result, const int& data_source)

{

    int area_no = get_cur_area_no();

    int line_no = get_cur_line_no();



    QListTermParamRec lstParamRec;

    for( int n = 0; n < m_term_param_rec.size(); n++ )

    {

        DMS_TERM_PARAM_DICT_STRUCT &param_rec = m_term_param_rec[n];

        if( QString(param_rec.param_code).toInt( NULL, 16 ) == info.data_type )

        {

            Q_ASSERT( param_rec.xh_id == m_term_xh_id );



            TermParamRec update_rec;

            update_rec.m_term_id        = this->m_term_dev_id;                              //终端ID

            update_rec.m_xh_id          = this->m_term_xh_id;                               //型号ID

            update_rec.m_line_no        = line_no;                                          //微机保护线路号

            update_rec.m_area           = area_no;                        //定值区 这里取召唤的定值区号 因为上送的有问题

            update_rec.m_code           = QString::fromStdString( param_rec.param_code );   //参数代码(16进制不带0x)

            update_rec.m_code_10        = update_rec.m_code.toInt( NULL, 16 );              //10进制参数代码

            update_rec.m_name           = QString::fromStdString(param_rec.name );          //参数名称

            update_rec.m_type           = param_rec.param_type;                             //参数类别(0-运行参数;1-动作定值;2-固有参数)

            info.para_type              = param_rec.param_type;     //上送的参数类别不可信 以定义的为准

            update_rec.m_value          = param_value_to_string( info );                    //参数值

            update_rec.m_data_type      = param_rec.data_type;                              //参数值数据类型(1-类型不确定;2-int;3-float;4-short;5-unsigned short;6-unsigned int;7-string)

            update_rec.m_unit           = QString::fromStdString( param_rec.unit );         //参数单位

            update_rec.m_coe            = param_rec.coe;                                    //参数系数

            update_rec.m_data_source    = data_source;                                                //数据来源(1-1区;2-四区;3-其他)

            update_rec.m_update_time    = QDateTime::currentDateTime();                     //最近更新时间

            lstParamRec.append( update_rec );



            break;

        }

    }



    //激活写入历史库



    TermParam_Write_Thread::InsertParamHis(lstParamRec,g_strLoginUserName,op_type,op_result);



}



void TermParamWindow::insertParamHisByPara_mul(const qint32 & line_no,const qint64& term_id,Terminal_Para_Info info,const int &op_type,const int& op_result, const int& data_source)

{

    //qint32 line_no=get_line_no_mul(term_id);

    qint32 area_no=get_area_no_mul(term_id,line_no);

    qint64 xh_id=get_xh_id_mul(term_id);



    QListTermParamRec lstParamRec;

    //QString::number(hexnum,16);

    DMS_TERM_PARAM_DICT_STRUCT param_rec = m_code_paramDict_map[QString::number(info.data_type,16)];





    TermParamRec update_rec;

    update_rec.m_term_id        = term_id;                              //终端ID

    update_rec.m_xh_id          = xh_id;                               //型号ID

    update_rec.m_line_no        = line_no+TERM_PARAM_LINE_NO_BASE;                                          //微机保护线路号

    update_rec.m_area           = area_no;                        //定值区 这里取召唤的定值区号 因为上送的有问题

    update_rec.m_code           = QString::fromStdString( param_rec.param_code );   //参数代码(16进制不带0x)

    update_rec.m_code_10        = update_rec.m_code.toInt( NULL, 16 );              //10进制参数代码

    update_rec.m_name           = QString::fromStdString(param_rec.name );          //参数名称

    update_rec.m_type           = param_rec.param_type;                             //参数类别(0-运行参数;1-动作定值;2-固有参数)

    info.para_type              = param_rec.param_type;     //上送的参数类别不可信 以定义的为准

    update_rec.m_value          = param_value_to_string( info );                    //参数值

    update_rec.m_data_type      = param_rec.data_type;                              //参数值数据类型(1-类型不确定;2-int;3-float;4-short;5-unsigned short;6-unsigned int;7-string)

    update_rec.m_unit           = QString::fromStdString( param_rec.unit );         //参数单位

    update_rec.m_coe            = param_rec.coe;                                    //参数系数

    update_rec.m_data_source    = data_source;                                                //数据来源(1-1区;2-四区;3-其他)

    update_rec.m_update_time    = QDateTime::currentDateTime();                     //最近更新时间

    lstParamRec.append( update_rec );





    //激活写入历史库

    TermParam_Write_Thread::InsertParamHis(lstParamRec,g_strLoginUserName,op_type,op_result);



}



void TermParamWindow::clear_callArea()

{

    QStandardItemModel *pModel = NULL;



    pModel = (QStandardItemModel *)ui->tableView_term_mul->model();

    if( pModel == NULL ) return;



    for( int row = 0; row < pModel->rowCount(); row++ )

    {





        QStandardItem *pItemName = pModel->item( row, MUL_TERM_COL_NAME );

        if( pItemName == NULL ) continue;



        if(pItemName->checkState()==Qt::Checked)

        {





            QStandardItem *pCallAreaValItem = pModel->item( row, MUL_TERM_COL_CALL_AREA_VAL);

            if( pCallAreaValItem == NULL ) continue;

            pCallAreaValItem->setText("");



            QStandardItem *pCallAreaItem = pModel->item( row, MUL_TERM_COL_CALL_AREA);

            if( pCallAreaItem == NULL ) continue;

            pCallAreaItem->setText("");





#ifdef SHOW_MUL_CALL_RESULT



            QStandardItem *pCallParaItem = pModel->item( row, MUL_TERM_COL_CALL_PARA );

            if( pCallParaItem == NULL ) continue;

            pCallParaItem->setText("");

#endif



            QStandardItem *pDownloadItem = pModel->item( row, MUL_TERM_COL_PARAM_DOWNLOAD);

            if( pDownloadItem == NULL ) continue;

            pDownloadItem->setText("");



            QStandardItem *pActItem = pModel->item( row, MUL_TERM_COL_PARAM_ACT);

            if( pActItem == NULL ) continue;

            pActItem->setText("");



            QStandardItem *pStatusItem = pModel->item( row, MUL_TERM_COL_STATUS);

            if( pStatusItem == NULL ) continue;

            pStatusItem->setText("");

        }



    }

}



void TermParamWindow::clear_callParam()

{

    QStandardItemModel *pModel = NULL;



    pModel = (QStandardItemModel *)ui->tableView_term_mul->model();

    if( pModel == NULL ) return;



    for( int row = 0; row < pModel->rowCount(); row++ )

    {





        QStandardItem *pItemName = pModel->item( row, MUL_TERM_COL_NAME );

        if( pItemName == NULL ) continue;



        if(pItemName->checkState()==Qt::Checked)

        {



#ifdef SHOW_MUL_CALL_RESULT

            QStandardItem *pCallParaItem = pModel->item( row, MUL_TERM_COL_CALL_PARA );

            if( pCallParaItem == NULL ) continue;

            pCallParaItem->setText("");

#endif



            QStandardItem *pDownloadItem = pModel->item( row, MUL_TERM_COL_PARAM_DOWNLOAD);

            if( pDownloadItem == NULL ) continue;

            pDownloadItem->setText("");



            QStandardItem *pActItem = pModel->item( row, MUL_TERM_COL_PARAM_ACT);

            if( pActItem == NULL ) continue;

            pActItem->setText("");



            QStandardItem *pStatusItem = pModel->item( row, MUL_TERM_COL_STATUS);

            if( pStatusItem == NULL ) continue;

            pStatusItem->setText("");

        }



    }

}



void TermParamWindow::clear_downloadParam()

{

    QStandardItemModel *pModel = NULL;



    pModel = (QStandardItemModel *)ui->tableView_term_mul->model();

    if( pModel == NULL ) return;



    for( int row = 0; row < pModel->rowCount(); row++ )

    {





        QStandardItem *pItemName = pModel->item( row, MUL_TERM_COL_NAME );

        if( pItemName == NULL ) continue;



        if(pItemName->checkState()==Qt::Checked)

        {





            QStandardItem *pDownloadItem = pModel->item( row, MUL_TERM_COL_PARAM_DOWNLOAD);

            if( pDownloadItem == NULL ) continue;

            pDownloadItem->setText("");



            QStandardItem *pActItem = pModel->item( row, MUL_TERM_COL_PARAM_ACT);

            if( pActItem == NULL ) continue;

            pActItem->setText("");



            QStandardItem *pStatusItem = pModel->item( row, MUL_TERM_COL_STATUS);

            if( pStatusItem == NULL ) continue;

            pStatusItem->setText("");

        }



    }

}



void TermParamWindow::clear_actParam()

{

    QStandardItemModel *pModel = NULL;



    pModel = (QStandardItemModel *)ui->tableView_term_mul->model();

    if( pModel == NULL ) return;



    for( int row = 0; row < pModel->rowCount(); row++ )

    {





        QStandardItem *pItemName = pModel->item( row, MUL_TERM_COL_NAME );

        if( pItemName == NULL ) continue;



        if(pItemName->checkState()==Qt::Checked)

        {





            QStandardItem *pActItem = pModel->item( row, MUL_TERM_COL_PARAM_ACT);

            if( pActItem == NULL ) continue;

            pActItem->setText("");



            QStandardItem *pStatusItem = pModel->item( row, MUL_TERM_COL_STATUS);

            if( pStatusItem == NULL ) continue;

            pStatusItem->setText("");

        }



    }

}

void TermParamWindow::update_sheet_reply_act_para(const  T_Terminal_Para &para )

{

    int count = para.terminal_para_seq.size();

    //Q_ASSERT( para.num == count );





    QListTermParamRec lstParamRec;



    for( int m = 0; m < para.terminal_para_seq.size(); m++ )

    {

        Terminal_Para_Info info = para.terminal_para_seq[m];



        for( int n = 0; n < m_sheet_term_param_rec.size(); n++ )

        {

            DMS_TERM_PARAM_DICT_STRUCT &param_rec = m_sheet_term_param_rec[n];

            if( QString(param_rec.param_code).toInt( NULL, 16 ) == info.data_type )

            {

                Q_ASSERT( param_rec.xh_id == m_sheet_term_xh_id );



                TermParamRec update_rec;

                update_rec.m_term_id        = this->m_sheet_term_dev_id;                              //终端ID

                update_rec.m_xh_id          = this->m_sheet_term_xh_id;                               //型号ID

                update_rec.m_line_no        = m_sheet_line_no;                                          //微机保护线路号

                update_rec.m_area           = m_sheet_area_no;//info.serial_num;                        //定值区 这里取召唤的定值区号 因为上送的有问题

                update_rec.m_code           = QString::fromStdString( param_rec.param_code );   //参数代码(16进制不带0x)

                update_rec.m_code_10        = update_rec.m_code.toInt( NULL, 16 );              //10进制参数代码

                update_rec.m_name           = QString::fromStdString(param_rec.name );          //参数名称

                update_rec.m_type           = param_rec.param_type;                             //参数类别(0-运行参数;1-动作定值;2-固有参数)

                info.para_type              = param_rec.param_type;     //上送的参数类别不可信 以定义的为准

                update_rec.m_value          = param_value_to_string( info );                    //参数值

                update_rec.m_data_type      = param_rec.data_type;                              //参数值数据类型(1-类型不确定;2-int;3-float;4-short;5-unsigned short;6-unsigned int;7-string)

                update_rec.m_unit           = QString::fromStdString( param_rec.unit );         //参数单位

                update_rec.m_coe            = param_rec.coe;                                    //参数系数

                update_rec.m_data_source    = 2;                                                //数据来源(1-1区;2-四区;3-其他)

                update_rec.m_update_time    = QDateTime::currentDateTime();                     //最近更新时间

                lstParamRec.append( update_rec );



                break;

            }

        }

    }

    //激活写入历史库



    TermParam_Write_Thread::InsertParamHis(lstParamRec,g_strLoginUserName);

}

QString TermParamWindow::param_value_to_string( const Terminal_Para_Info &info )

{

    QString strValue;



    switch( info.value_type )

    {

    case 2: //int

    case 4: //short

    case 5: //unsigned short

    case 6: //unsigned int

    case 8: //BOOL

        strValue = tr("%1").arg( info.value_i );

        break;

    case 7:

        if( strlen( info.value_s ) == 0 )

            strValue = tr("null");

        else

#ifdef USE_UTF8_CODE

            strValue = tr("%1").arg( QString::fromUtf8(info.value_s) );

#else

            strValue = tr("%1").arg( info.value_s );

#endif

        break;

    case 1: //类型不确定

    case 3: //float

    default:

    {

        char szText[32];

        sprintf( szText, "%.4f", info.value_f );



        TermParamRec::TrimRightDotZero( szText );



        strValue = tr("%1").arg( szText );

    }

        break;

    }



    if( info.para_type == 1/*动作定值*/ )

    {

        strValue =strValue;// ( strValue == tr("1") ? tr("投入") : tr("退出") );

    }



    return strValue;

}



bool TermParamWindow::string_to_param_value( QString strText, Terminal_Para_Info &info )

{

    bool bFlag=true;

#if 0

    if( info.para_type == 1/*动作定值*/ )

    {

        info.value_i = ( ( strText.indexOf( tr("退出") ) >= 0 ) ? 0 : 1 );

    }

    else

#endif

    {

        switch( info.value_type )

        {

        case 2: //int

        case 4: //short

        case 5: //unsigned short

        case 6: //unsigned int

        case 8: //BOOL

            info.value_i = strText.toInt(&bFlag);

            break;

        case 7:

            strcpy( info.value_s, strText.toStdString().c_str() );

            break;

        case 1: //类型不确定

        case 3: //float

        default:

            info.value_f = strText.toFloat(&bFlag);

            break;

        }

    }



    //cg2018

    /*	{

        char szDataType[32];

        sprintf( szDataType, "%04x", info.data_type );

        QString strTip = tr("[%1] code=%2 serial_num=%3 值类型=%4 整数值=%5 浮点值=%6 字符串=%7")

                            .arg(QDateTime::currentDateTime().toString("yyyy-MM-dd hh:mm:ss"))

                            .arg(szDataType).arg(info.serial_num)

                            .arg(info.value_type).arg(info.value_i).arg(info.value_f).arg(info.value_s));

        ZMessageBox::information( this, tr("write_param"), strTip );

    }*/



    return bFlag;

}



void TermParamWindow::show_para_info( Terminal_Para_Info &info )

{

    QList< QStandardItemModel * >   lstModel;

    /*lstModel.append( (QStandardItemModel *)ui->tableView_Para0->model() );

       lstModel.append( (QStandardItemModel *)ui->tableView_Para1->model() );

       lstModel.append( (QStandardItemModel *)ui->tableView_Para2->model() );*/

    QMap<qint32,QTableView* >::iterator it=m_paramType_widget_map.begin();

    for(;it!=m_paramType_widget_map.end();++it)

    {

        lstModel.append((QStandardItemModel *)it.value()->model());

    }







    QStandardItemModel *pModel;

    foreach( pModel, lstModel )

    {

        if( pModel == NULL ) continue;



        for( int row = 0; row < pModel->rowCount(); row++ )

        {

            QStandardItem *pNameItem = pModel->item( row, PARA_COL_NAME );

            if( pNameItem == NULL ) continue;



            int code = pNameItem->data( Qt::UserRole+10 ).toInt();

            if( code == info.data_type )

            {

                QStandardItem *pValueItem = pModel->item( row, PARA_COL_VALUE );

                if( pValueItem != NULL )

                {

                    info.para_type = pValueItem->data( Qt::UserRole+10 ).toInt();

                    pValueItem->setText( param_value_to_string( info ) );

                }





                QStandardItem *pValueTypeItem = pModel->item( row, PARA_COL_CALL_VALUE_TYPE );

                if( pValueTypeItem != NULL )

                {



                    pValueTypeItem->setText( getDataName((qint32)info.value_type) );

                }

                //cg2018

                //处理 BOOL 型 cmbbox False/True



                //召唤的返回结果不可用

                //QString strResult = get_result_desc( info.result );

                //pModel->item( row, PARA_COL_RESULT )->setText( strResult );



                break;

            }

        }

    }

}



void TermParamWindow::update_para_info( Terminal_Para_Info &info )

{

    for( int i = 0; i < m_term_param_rec.size(); i++ )

    {

        DMS_TERM_PARAM_DICT_STRUCT &param_rec = m_term_param_rec[i];



        if( QString(param_rec.param_code).toInt( NULL, 16 ) == info.data_type )

        {

            param_rec.data_type = info.value_type;

            m_call_code_set.insert(info.data_type);

            break;

        }

    }

}



void TermParamWindow::show_check_para_info(Terminal_Para_Info &info)

{

    QList< QStandardItemModel * >   lstModel;

    /*lstModel.append( (QStandardItemModel *)ui->tableView_Para0->model() );

       lstModel.append( (QStandardItemModel *)ui->tableView_Para1->model() );

       lstModel.append( (QStandardItemModel *)ui->tableView_Para2->model() );*/

    QMap<qint32,QTableView* >::iterator it=m_paramType_widget_map.begin();

    for(;it!=m_paramType_widget_map.end();++it)

    {

        lstModel.append((QStandardItemModel *)it.value()->model());

    }







    QStandardItemModel *pModel;

    foreach( pModel, lstModel )

    {

        if( pModel == NULL ) continue;



        for( int row = 0; row < pModel->rowCount(); row++ )

        {

            QStandardItem *pNameItem = pModel->item( row, PARA_COL_NAME );

            if( pNameItem == NULL ) continue;



            int code = pNameItem->data( Qt::UserRole+10 ).toInt();

            if( code == info.data_type )

            {

                QStandardItem *pValueItem = pModel->item( row, PARA_COL_CHECK_VALUE );

                if( pValueItem != NULL )

                {

                    info.para_type = pValueItem->data( Qt::UserRole+10 ).toInt();

                    pValueItem->setText( param_value_to_string( info ) );

                }

                break;

            }

        }

    }

}



void TermParamWindow::show_reply_result( T_Terminal_Para &para )

{

    int count = para.terminal_para_seq.size();

    Q_ASSERT( para.num == count );

    Q_ASSERT( count == 1 );



    for( int i = 0; i < para.terminal_para_seq.size(); i++ )

    {

        Terminal_Para_Info &info = para.terminal_para_seq[i];



        //update_oper_result( info.data_type, info.result );



        //下装参数只返回一个结构体 表示返校结果

        update_oper_result( info.result );

    }

}



void TermParamWindow::show_oper_result( QString strOpDesc, T_Terminal_Op &para )

{

    for( int i = 0; i < para.terminal_op_seq.size(); i++ )

    {

        Terminal_Para_Op &item = para.terminal_op_seq[i];



        QString strText = tr("返回值=%1 ").arg(item.result);



        switch( item.result )

        {

        case 0:

            strText = tr("默认(%1)").arg(item.result);

            strText = tr("默认");

            ZMessageBox::warning( this, strOpDesc, strText );

            break;

        case 1:

            strText = tr("否定确认(%1)").arg(item.result);

            strText = tr("否定确认");

            ZMessageBox::critical( this, strOpDesc, strText );

            break;

        case 2:

            strText = tr("肯定确认(%1)").arg(item.result);

            strText = tr("肯定确认");

            ZMessageBox::information( this, strOpDesc, strText );

            break;

        default:

            ZMessageBox::warning( this, strOpDesc, strText );

            break;

        }

    }

}



void TermParamWindow::show_area( Setting_Area &area )

{

    clear_area();



#ifdef _DEBUG //cg2019

    if( QDateTime::currentDateTime() < QDateTime( QDate(2019,2,18), QTime(0,0,0) ) )

    {

        area.cur_serial_num = 1;

        area.max_serial_num += 3;

    }

#endif



    QIcon cur_icon = QIcon( tr(":/Resources/skin/images/radio_selected.png") );

    QIcon the_icon = QIcon( tr(":/Resources/skin/images/radio_normal.png") );

    m_cur_area=-1;

    for( int i = area.min_serial_num; i <= area.max_serial_num; i++ )

    {

        ui->comboBox_Area->addItem( ( i == area.cur_serial_num ) ? cur_icon : the_icon, tr("定值区%1").arg( i ), QVariant( i ) );

    }

    m_cur_area=area.cur_serial_num;

    for( int idx = 0; idx < ui->comboBox_Area->count(); idx++ )

    {

        if( ui->comboBox_Area->itemData( idx ).toInt() == (int)area.cur_serial_num )

        {

            ui->comboBox_Area->setCurrentIndex( idx );

            modifyTermCurFixedArea(m_term_dev_id,(int)area.cur_serial_num);



            break;

        }

    }

    sprintf(g_log_buf,"召唤定值区返回结果 comm_fac_id=%ld cur_area=%d min_area=%d max_area=%d",

            area.comm_fac_id,area.cur_serial_num,area.min_serial_num,area.max_serial_num);

    cout<<CLogManager::Instance()->WriteLineT(g_log_buf);

    AfterCallArea();

}

bool TermParamWindow::modifyTermCurFixedArea(const long & term_id,const int &cur_area)

{

    CTableNet tabNet;

    int ret=tabNet.Open(AP_DMS_SCADA,TABLE_NO_DMS_TERMINAL_INFO);

    if(ret<0)

    {

        cout<<"open table 13510 is error  ret="<<ret<<endl;

        return false;

    }

    ret=tabNet.TableModifyByKey((char*)&term_id,"cur_fixed_area",(char*)&cur_area,sizeof(cur_area));

    if(ret<0)

    {

        cout<<"TableModifyByKey is error  ret=="<<ret<<endl;

        return false;

    }

    return true;

}

void TermParamWindow::show_check_area( Setting_Area &area )

{

    clear_area();





    QIcon cur_icon = QIcon( tr(":/Resources/skin/images/radio_selected.png") );

    QIcon the_icon = QIcon( tr(":/Resources/skin/images/radio_normal.png") );

    m_cur_area=-1;

    for( int i = area.min_serial_num; i <= area.max_serial_num; i++ )

    {

        ui->comboBox_Area->addItem( ( i == area.cur_serial_num ) ? cur_icon : the_icon, tr("定值区%1").arg( i ), QVariant( i ) );

    }

    m_cur_area=area.cur_serial_num;

    for( int idx = 0; idx < ui->comboBox_Area->count(); idx++ )

    {

        if( ui->comboBox_Area->itemData( idx ).toInt() == (int)area.cur_serial_num )

        {

            ui->comboBox_Area->setCurrentIndex( idx );

            break;

        }

    }





}

void TermParamWindow::clear_area()

{

    ui->comboBox_Area->clear();

}



void TermParamWindow::show_area_alter( Setting_Area &area,const qint32 &alter_area )

{

    int result = area.result;



#ifdef _DEBUG //cg2019

    if( QDateTime::currentDateTime() < QDateTime( QDate(2019,2,18), QTime(0,0,0) ) )

    {

        if( QTime::currentTime().second() % 2 == 0 )

            result = 1;

        else

            result = 2;

    }

#endif



    QString strOpDesc = tr("切换定值区");

    QString strText = tr("返回值=%1 ").arg(result);

    sprintf(g_log_buf,"切换定值区返回结果 comm_fac_id=%ld result=%d",

            area.comm_fac_id,area.result);

    cout<<CLogManager::Instance()->WriteLineT(g_log_buf);



    TWarnData data;

    DMS_TERMINAL_INFO_STRUCT struDmsTerminalInfo;

    m_rtdbOper.ReadTerminalInfo(struDmsTerminalInfo,m_term_dev_id);

    //data.combined_id=struDmsTerminalInfo.feeder_id;

    data.term_id=struDmsTerminalInfo.id;

    data.warn_type=MENU_OPT_AREA_ALTER;

    data.feeder_id=struDmsTerminalInfo.feeder_id;



    if(result==1)

    {

        char sta_buf[512];







        sprintf(sta_buf,"切换定值区 由定值区%d切换至定值区%d 切换成功(%d)",m_cur_area,alter_area,result);

        data.op_str=sta_buf;



    }

    else

    {

        char sta_buf[512];

        sprintf(sta_buf,"切换定值区 由定值区%d切换至定值区%d 切换定值区号失败(%d)",m_cur_area,alter_area,result);

        data.op_str=sta_buf;

    }

    sendWarn(data);

    switch( result )

    {

    case 0:

        strText = tr("默认值(%1)").arg(result);

        strText = tr("默认值");

        ZMessageBox::warning( this, strOpDesc, strText );

        break;

    case 1:

    {

#if 0

        int max_area=-1;

        int min_area=-1;

        m_cur_area=-1;



        int index=ui->comboBox_Area->currentIndex();

        m_cur_area=ui->comboBox_Area->itemData( index ).toInt();

        QList<int> area_lst;

        for( int idx = 0; idx < ui->comboBox_Area->count(); idx++ )

        {

            int tmp_area=ui->comboBox_Area->itemData( idx ).toInt();

            area_lst.push_back(tmp_area);



        }



        clear_area();

        QIcon cur_icon = QIcon( tr(":/Resources/skin/images/radio_selected.png") );

        QIcon the_icon = QIcon( tr(":/Resources/skin/images/radio_normal.png") );



        for( int i = 0; i < area_lst.count(); i++ )

        {

            ui->comboBox_Area->addItem( ( m_cur_area == area_lst[i] ) ? cur_icon : the_icon, tr("定值区%1").arg( area_lst[i]), QVariant( area_lst[i] ) );

        }





        for( int idx = 0; idx < ui->comboBox_Area->count(); idx++ )

        {

            if( ui->comboBox_Area->itemData( idx ).toInt() == m_cur_area )

            {

                ui->comboBox_Area->setCurrentIndex( idx );

                break;

            }

        }

#endif

        check_area(alter_area);

        strText = tr("激活确认(%1)").arg(result);



        strText = tr("激活确认");

        // ZMessageBox::information( this, strOpDesc, strText );

        break;

    }

    case 2:

        strText = tr("激活中止(%1)").arg(result);

        strText = tr("激活中止");

        ZMessageBox::critical( this, strOpDesc, strText );

        break;

    default:

        ZMessageBox::warning( this, strOpDesc, strText );

        break;

    }



    //if( result == 1 ) result = 111; //激活确认

    //if( result == 2 ) result = 112; //激活中止



    //update_oper_label( result );

}



void TermParamWindow::update_param( QListTermParamRec &lstRec )

{

    QList< QStandardItemModel * >   lstModel;

    /*lstModel.append( (QStandardItemModel *)ui->tableView_Para0->model() );

       lstModel.append( (QStandardItemModel *)ui->tableView_Para1->model() );

       lstModel.append( (QStandardItemModel *)ui->tableView_Para2->model() );*/

    QMap<qint32,QTableView* >::iterator it=m_paramType_widget_map.begin();

    for(;it!=m_paramType_widget_map.end();++it)

    {

        lstModel.append((QStandardItemModel *)it.value()->model());

    }





    TermParamRec rec;

    foreach( rec, lstRec )

    {

        if( rec.m_xh_id != m_term_xh_id ) continue;



        qint32 code_v = QString(rec.m_code).toInt( NULL, 16 );



        //qDebug() << tr("\r\nrec.m_code = %1\r\n").arg(rec.m_code);



        QStandardItemModel * pModel;

        foreach( pModel, lstModel )

        {

            if( pModel == NULL ) continue;



            for( int row = 0; row < pModel->rowCount(); row++ )

            {

                QStandardItem *pItem0 = pModel->item( row, PARA_COL_NAME );

                if( pItem0 == NULL ) continue;



                if( code_v == pItem0->data( Qt::UserRole+10 ).toInt() )

                {

                    QStandardItem *pValueItem = pModel->item( row, PARA_COL_VALUE );

                    if( pValueItem != NULL )

                    {

                        pValueItem->setData( QColor(0,128,0), Qt::TextColorRole );

                        pValueItem->setText( rec.m_value );



                        if( rec.m_type == 1/*动作定值*/ )

                        {

                            pValueItem->setData( ( rec.m_value == tr("退出") ) ? QColor(128,0,0) : QColor(0,0,128), Qt::TextColorRole );

                        }



                        //根据数据类型设置编辑过滤器 setInputMask

                        switch( rec.m_data_type )

                        {

                        case 1: //类型不确定

                            break;

                        case 2: //int

                            break;

                        case 3: //float

                            break;

                        case 4: //short

                            break;

                        case 5: //unsigned short

                            break;

                        case 6: //unsigned int

                            break;

                        case 7: //string

                            break;

                        case 8: //BOOL

                            break;

                        default:

                            break;

                        }

                    }



                    QStandardItem *pTimeItem = pModel->item( row, PARA_COL_TIME );

                    if( pTimeItem != NULL )

                    {

                        pTimeItem->setData( QColor(0,128,128), Qt::TextColorRole );

                        pTimeItem->setText( rec.toString_UpdateTime() );

                    }

                }

            }

        }

    }



    ui->tableView_Para0->resizeColumnsToContents();

    ui->tableView_Para0->update();



    ui->tableView_Para1->resizeColumnsToContents();

    ui->tableView_Para1->update();



    ui->tableView_Para2->resizeColumnsToContents();

    ui->tableView_Para2->update();

}



void TermParamWindow::clear_param()

{

    QList< QStandardItemModel * >   lstModel;

    /*lstModel.append( (QStandardItemModel *)ui->tableView_Para0->model() );

       lstModel.append( (QStandardItemModel *)ui->tableView_Para1->model() );

       lstModel.append( (QStandardItemModel *)ui->tableView_Para2->model() );*/

    QMap<qint32,QTableView* >::iterator it=m_paramType_widget_map.begin();

    for(;it!=m_paramType_widget_map.end();++it)

    {

        lstModel.append((QStandardItemModel *)it.value()->model());

    }







    QListInt32 lstCol;

    lstCol << PARA_COL_VALUE << PARA_COL_TIME << PARA_COL_RESULT;



    QStandardItemModel * pModel;

    foreach( pModel, lstModel )

    {

        if( pModel == NULL ) continue;



        for( int row = 0; row < pModel->rowCount(); row++ )

        {

            qint32 col;

            foreach( col, lstCol )

            {

                QStandardItem *pItem = pModel->item( row, col );

                if( pItem != NULL ) pItem->setText( tr("") );

            }

        }

    }



    ui->tableView_Para0->resizeColumnsToContents();

    ui->tableView_Para0->update();



    ui->tableView_Para1->resizeColumnsToContents();

    ui->tableView_Para1->update();



    ui->tableView_Para2->resizeColumnsToContents();

    ui->tableView_Para2->update();

}



QString TermParamWindow::get_event_desc( short event_id )

{

    QString strDesc;



    switch( event_id )

    {

    case MT_DMS_TERM_PARA_REPLY:

        strDesc = tr("召唤参数");

        break;

    case MT_DMS_TERM_PARA_MODIFY_REPLY:

        strDesc = tr("下装参数");

        break;

    case MT_DMS_TERM_PARA_MODIFY_CONF:

        strDesc = tr("激活参数");

        break;



    case MT_DMS_SETTING_AREA_REPLY:

        strDesc = tr("读定值区号");

        break;

    case MT_DMS_ALTER_SETTING_AREA_REPLY:

        strDesc = tr("切换定值区");

        break;



    default:

        strDesc = tr("未知操作(%1)").arg(event_id);

        break;

    }



    return strDesc;

}



QString TermParamWindow::get_result_desc( short result )

{

    QString strDesc;



    switch( result )

    {

    case 0:

        strDesc = tr("默认(0)");

        break;

    case 1:

        strDesc = tr("返校错误");

        break;

    case 2:

        strDesc = tr("返校成功");

        break;

    case 10:

        strDesc = tr("召唤成功");

        break;

    case 103:

        strDesc = tr("超时");

        break;

    case 104:

        strDesc = tr("执行中...");

        break;

    case 105:

        strDesc = tr("消息回环");

        break;

    case 106:

        strDesc = tr("中止");

        break;

    case 111:

        strDesc = tr("激活确认");

        break;

    case 112:

        strDesc = tr("激活中止");

        break;

    case 120:

        strDesc = tr("更新商用库完成");

        break;

    case 121:

        strDesc = tr("读取商用库完成");

        break;

        //以下用于激活

    case 711:

        strDesc = tr("默认");

        break;

    case 712:

        // strDesc = tr("否定确认");

        strDesc = tr("激活失败");

        break;

    case 713:

        //strDesc = tr("肯定确认");

        strDesc = tr("激活成功");

        break;

    case 714:

        strDesc = tr("未定义");

        break;

    default:

        strDesc = tr("未定义(%1)").arg(result);

        break;

    }



    return strDesc;

}



void TermParamWindow::show_tip_doing()

{

    if( m_reply_event == 0 ) return;



    QString strTip;



    int left_seconds = m_oper_timeout_slice - ( time(NULL) - m_start_time );



    switch( m_reply_event )

    {

    case MT_DMS_TERM_PARA_REPLY:

        strTip = tr("正在执行 召唤参数 操作，等待 %1 秒后再操作。").arg(left_seconds);

        break;

    case MT_DMS_TERM_PARA_MODIFY_REPLY:

        strTip = tr("正在执行 下装参数 操作，等待 %1 秒后再操作。").arg(left_seconds);

        break;

    case MT_DMS_TERM_PARA_MODIFY_CONF:

        strTip = tr("正在执行 激活参数 操作，等待 %1 秒后再操作。").arg(left_seconds);

        break;

    default:

        break;

    }



    if( strTip.isEmpty() ) return;



    ZMessageBox::warning( this, tr("正在操作中..."), strTip );

}



void TermParamWindow::show_tip_finish( short result )

{

#if 0

#ifndef _DEBUG

    return;

#endif

#endif

    if( m_reply_event == 0 ) return;



    switch( result )

    {

    case 0:

        ZMessageBox::warning( this, get_event_desc(m_reply_event), QString(tr("操作 %1").arg(get_result_desc(result))) );

        break;

    case 1:

        ZMessageBox::warning( this, get_event_desc(m_reply_event), QString(tr("操作 %1").arg(get_result_desc(result))) );

        break;

    case 2:

        ZMessageBox::information( this, get_event_desc(m_reply_event), QString(tr("操作 %1").arg(get_result_desc(result))) );

        break;

    case 10:

        ZMessageBox::information( this, get_event_desc(m_reply_event), QString(tr("操作 %1").arg(get_result_desc(result))) );

        break;

    case 103:

        ZMessageBox::critical( this, get_event_desc(m_reply_event), QString(tr("操作 %1").arg(get_result_desc(result))) );

        break;

    case 104:

        ZMessageBox::information( this, get_event_desc(m_reply_event), QString(tr("操作 %1").arg(get_result_desc(result))) );

        break;

    case 105:

        ZMessageBox::information( this, get_event_desc(m_reply_event), QString(tr("操作 %1").arg(get_result_desc(result))) );

        break;

    case 106:

        ZMessageBox::information( this, get_event_desc(m_reply_event), QString(tr("操作 %1").arg(get_result_desc(result))) );

        break;

    case 111:

        ZMessageBox::information( this, get_event_desc(m_reply_event), QString(tr("操作 %1").arg(get_result_desc(result))) );

        break;

    case 112:

        ZMessageBox::warning( this, get_event_desc(m_reply_event), QString(tr("操作 %1").arg(get_result_desc(result))) );

        break;

    case 120:

    case 121:

        ZMessageBox::information( this, get_event_desc(m_reply_event), QString(tr("操作 %1").arg(get_result_desc(result))) );

        break;

    default:

        ZMessageBox::critical( this, get_event_desc(m_reply_event), QString(tr("操作 未定义(%1)").arg(result)) );

        break;

    }

}



void TermParamWindow::update_oper_result( short result )

{

    QString strResult = get_result_desc( result );

    QList< QStandardItemModel * >   lstModel;

    /*lstModel.append( (QStandardItemModel *)ui->tableView_Para0->model() );

       lstModel.append( (QStandardItemModel *)ui->tableView_Para1->model() );

       lstModel.append( (QStandardItemModel *)ui->tableView_Para2->model() );*/

    QMap<qint32,QTableView* >::iterator it=m_paramType_widget_map.begin();

    for(;it!=m_paramType_widget_map.end();++it)

    {

        lstModel.append((QStandardItemModel *)it.value()->model());

    }







    QStandardItemModel *pModel;

    foreach( pModel, lstModel )

    {

        if( pModel == NULL ) continue;



        for( int row = 0; row < pModel->rowCount(); row++ )

        {

            QStandardItem *pItem = pModel->item( row, PARA_COL_NAME );

            if( pItem == NULL ) continue;



            if( pItem->checkState() != Qt::Unchecked )

            {

                QStandardItem *pResultItem = pModel->item( row, PARA_COL_RESULT );

                if( pResultItem != NULL && pResultItem->data( Qt::UserRole+10 ).toBool() )

                {

                    pResultItem->setText( strResult );

                }

            }

        }

    }

}

void TermParamWindow::update_oper_result_download(int code, short result )

{

    QString strResult = get_result_desc( result );

    QList< QStandardItemModel * >   lstModel;

    /*lstModel.append( (QStandardItemModel *)ui->tableView_Para0->model() );

       lstModel.append( (QStandardItemModel *)ui->tableView_Para1->model() );

       lstModel.append( (QStandardItemModel *)ui->tableView_Para2->model() );*/

    QMap<qint32,QTableView* >::iterator it=m_paramType_widget_map.begin();

    for(;it!=m_paramType_widget_map.end();++it)

    {

        lstModel.append((QStandardItemModel *)it.value()->model());

    }







    QStandardItemModel *pModel;

    foreach( pModel, lstModel )

    {

        if( pModel == NULL ) continue;



        for( int row = 0; row < pModel->rowCount(); row++ )

        {

            QStandardItem *pItem = pModel->item( row, PARA_COL_NAME );

            if( pItem == NULL ) continue;



            if( pItem->checkState() != Qt::Unchecked )

            {

                int _code = pItem->data( Qt::UserRole+10 ).toInt();

                if(_code==code)

                {

                    QStandardItem *pResultItem = pModel->item( row, PARA_COL_RESULT );

                    if( pResultItem != NULL && pResultItem->data( Qt::UserRole+10 ).toBool() )

                    {

                        pResultItem->setText( strResult );

                    }

                }

            }

        }

    }

}

void TermParamWindow::update_oper_result( int code, short result )

{

    QString strResult = get_result_desc( result );



    QList< QStandardItemModel * >   lstModel;

    /*lstModel.append( (QStandardItemModel *)ui->tableView_Para0->model() );

       lstModel.append( (QStandardItemModel *)ui->tableView_Para1->model() );

       lstModel.append( (QStandardItemModel *)ui->tableView_Para2->model() );*/

    QMap<qint32,QTableView* >::iterator it=m_paramType_widget_map.begin();

    for(;it!=m_paramType_widget_map.end();++it)

    {

        lstModel.append((QStandardItemModel *)it.value()->model());

    }









    QStandardItemModel *pModel;

    foreach( pModel, lstModel )

    {

        if( pModel == NULL ) continue;



        for( int row = 0; row < pModel->rowCount(); row++ )

        {

            QStandardItem *pItem = pModel->item( row, PARA_COL_NAME );

            if( pItem == NULL ) continue;



            if( pItem->checkState() != Qt::Unchecked )

            {

                int _code = pItem->data( Qt::UserRole+10 ).toInt();

                if( _code == code )

                {

                    QStandardItem *pResultItem = pModel->item( row, PARA_COL_RESULT );

                    if( pResultItem != NULL && pResultItem->data( Qt::UserRole+10 ).toBool() )

                    {

                        pResultItem->setText( strResult );



                        pResultItem->setData( QVariant( false ), Qt::UserRole+10 );



                        QStandardItem *pLocalItem = pModel->item( row, PARA_COL_LOCAL_VALUE_TYPE );

                        QStandardItem *pCallItem = pModel->item( row, PARA_COL_CALL_VALUE_TYPE );

                        QStandardItem *pCompItem = pModel->item( row, PARA_COL_VALUE_TYPE_COMP );



                        QString comp_str;

                        QString local_str;

                        QString call_str;

                        if(pLocalItem!=NULL)

                        {

                         local_str=pLocalItem->text();



                        }

                        if(pCallItem!=NULL)

                        {

                           call_str=pCallItem->text();

                        }



                        if(call_str==local_str)

                        {

                           comp_str=tr("相同");

                        }

                        else

                        {

                           comp_str=tr("不相同");

                        }

                        if(pCompItem!=NULL)

                           pCompItem->setText(comp_str);

                    }

                }

            }

        }

    }

}



void TermParamWindow::update_oper_check_result(int code,short result)

{

    QString strResult = get_result_desc( result );

    QList< QStandardItemModel * >   lstModel;

    /*lstModel.append( (QStandardItemModel *)ui->tableView_Para0->model() );

       lstModel.append( (QStandardItemModel *)ui->tableView_Para1->model() );

       lstModel.append( (QStandardItemModel *)ui->tableView_Para2->model() );*/

    QMap<qint32,QTableView* >::iterator it=m_paramType_widget_map.begin();

    for(;it!=m_paramType_widget_map.end();++it)

    {

        lstModel.append((QStandardItemModel *)it.value()->model());

    }









    QStandardItemModel *pModel;

    foreach( pModel, lstModel )

    {

        if( pModel == NULL ) continue;



        for( int row = 0; row < pModel->rowCount(); row++ )

        {

            QStandardItem *pItem = pModel->item( row, PARA_COL_NAME );

            if( pItem == NULL ) continue;



            if( pItem->checkState() != Qt::Unchecked )

            {

                int _code = pItem->data( Qt::UserRole+10 ).toInt();

                if( _code == code )

                {

                    QStandardItem *pResultItem = pModel->item( row, PARA_COL_CHECK_RESULT );

                    if( pResultItem != NULL && pResultItem->data( Qt::UserRole+10 ).toBool() )

                    {

                        pResultItem->setText( strResult );

                    }

                }

            }

        }

    }

}



void TermParamWindow::update_oper_label( short result )

{

    if( m_pOperLabel == NULL ) return;

    if( ! m_pOperLabel->isVisible() ) return;



    QString strOper     = get_event_desc( m_reply_event );

    QString strResult   = get_result_desc( result );



    switch( result )

    {

    case 120:

    case 121:

        m_pOperLabel->setText( tr("%2").arg(strResult) );

        break;

    default:

        m_pOperLabel->setText( tr("%1 %2").arg(strOper).arg(strResult) );

        break;

    }

}



void TermParamWindow::StartProgressDlg()

{

    if( m_pProgressDlg != NULL )

    {

        m_pProgressDlg->close();



        delete m_pProgressDlg;

        m_pProgressDlg = NULL;



        m_progress_idx = 0;

    }



    m_pProgressDlg = new QProgressDialog( this );

    m_pProgressDlg->setFont( this->font() );



    int w = m_pProgressDlg->width();

    m_pProgressDlg->setMinimumWidth( w * 2 );



    //Qt::WindowFlags wf = m_pProgressDlg->windowFlags();

    //wf = wf & ~Qt::WindowMaximized;

    //m_pProgressDlg->setWindowFlags( wf );



    m_pProgressDlg->setWindowTitle( get_event_desc( m_reply_event ) );

    m_pProgressDlg->setLabelText( tr("正在操作中...") );

    m_pProgressDlg->setRange( 0, m_oper_timeout_slice );

    m_pProgressDlg->setModal( true );

    m_pProgressDlg->setCancelButtonText(tr("中止"));



    m_pProgressDlg->exec();

}



void TermParamWindow::update_progress_dlg( int step )

{

    if( m_pProgressDlg == NULL ) return;



    int idx = m_progress_idx++ % 4;



    switch( idx )

    {

    case 1:

        m_pProgressDlg->setLabelText( tr("正在操作中.  ") );

        break;

    case 2:

        m_pProgressDlg->setLabelText( tr("正在操作中.. ") );

        break;

    case 3:

        m_pProgressDlg->setLabelText( tr("正在操作中...") );

        break;

    case 0:

    default:

        m_pProgressDlg->setLabelText( tr("正在操作中   ") );

        break;

    }



    m_pProgressDlg->setValue( step-1 );//0-45   最大值应该为44

    cout<<"step value=="<<m_pProgressDlg->value()<<endl;

}



void TermParamWindow::On_MsgBus_CallArea( short reply_event, QByteArray buf )  //召唤定值区

{

    if( reply_event != m_reply_event ) return;



    Setting_Area area = { 0 };

    if( ! MsgBusDataIO::FromBuffer( buf, area ) ) return;



    if( area.comm_fac_id != m_term_dev_id ) return;



    show_area( area );



    //这里不能结束，因为有可能还有后继数据包　　应等待超时或Post_Finished信号

    //update_progress_dlg( m_oper_timeout_slice );

    //StopProgressStep();



    set_result_state( true );

}



void TermParamWindow::On_MsgBus_AlterArea( short reply_event, QByteArray buf ) //切换定值区

{

#if 0

    if( reply_event != m_reply_event ) return;



    Setting_Area area = { 0 };

    if( ! MsgBusDataIO::FromBuffer( buf, area ) ) return;



    if( area.comm_fac_id != m_term_dev_id ) return;



    show_area_alter( area );



    //这里不能结束，因为有可能还有后继数据包　　应等待超时或Post_Finished信号

    //update_progress_dlg( m_oper_timeout_slice );

    //StopProgressStep();



    set_result_state( true );

#endif

}



void TermParamWindow::On_MsgBus_ParaCall( short reply_event, QByteArray buf )      //召唤参数

{

    if( reply_event != m_reply_event ) return;



    T_Terminal_Para para;

    if( ! MsgBusDataIO::FromBuffer( buf, para ) ) return;



    show_reply_para( para );    //界面上显示

    update_reply_para( para );  //更新进商用数据库



    //这里不能结束，因为有可能还有后继数据包　　应等待超时或Post_Finished信号

    //update_progress_dlg( m_oper_timeout_slice );

    //StopProgressStep();

}



void TermParamWindow::On_MsgBus_ParaModifyPre( short reply_event, QByteArray buf ) //下装参数

{

    if( reply_event != m_reply_event ) return;



    T_Terminal_Para para;

    if( ! MsgBusDataIO::FromBuffer( buf, para ) ) return;



    show_reply_result( para );



    //这里不能结束，因为有可能还有后继数据包　　应等待超时或Post_Finished信号

    //update_progress_dlg( m_oper_timeout_slice );

    //StopProgressStep();

}



void TermParamWindow::On_MsgBus_ParaModify( short reply_event, QByteArray buf )    //激活参数

{

    if( reply_event != m_reply_event ) return;



    T_Terminal_Op para;

    if( ! MsgBusDataIO::FromBuffer( buf, para ) ) return;



    show_oper_result( tr("激活参数"), para );



    set_result_state( true );

}



void TermParamWindow::On_MsgBus_Finished( short reply_event, int flag )

{

    if( reply_event != m_reply_event ) return;



    update_progress_dlg( m_oper_timeout_slice );

    StopProgressStep();



    set_result_state( true );

}



void TermParamWindow::on_pushBtn_CallArea_clicked()

{

    QString strOper = tr("召唤定值区号");



#if  0

    Setting_Area tmpreply_area;

    tmpreply_area.comm_fac_id=m_term_dev_id;

    tmpreply_area.min_serial_num=0;

    tmpreply_area.max_serial_num=1;

    tmpreply_area.cur_serial_num=1;

    show_area(tmpreply_area);

    return;

#endif

    if( m_term_dev_id == 0L || m_term_xh_id == 0L )

    {

        ZMessageBox::warning( this, strOper, tr("无效的终端设备或未设置有效的终端型号...") );

        return;

    }







    if(m_pThread!=NULL)

    {

        delete m_pThread;

        m_pThread=NULL;

    }

    //激活参数

    short serv          = CH_DMS_TERMINAL_DATA;

    short event         = MT_DMS_CALL_SETTING_AREA;

    m_download_reply_event   = MT_DMS_SETTING_AREA_REPLY;

    m_sheet_result=0;

    m_oper_timeout_slice    = 45;   //操作超时间隔(秒)

    m_pThread=new MsgBus_RW_Thread();

    m_pThread->init( m_term_dev_id, m_oper_timeout_slice );





    m_pThread->setRunFlag(0);



    Setting_Area    area = { 0 };

    area.comm_fac_id    =m_term_dev_id;

    sprintf(g_log_buf,"召唤定值区 comm_fac_id=%ld ",

            area.comm_fac_id);

    cout<<CLogManager::Instance()->WriteLineT(g_log_buf);

    if( m_pThread->init_msg( serv, event, m_download_reply_event, area ) != 0 )

    {

        sprintf(g_log_buf,"召唤定值区 初始化失败...");

        CLogManager::Instance()->WriteLineT(g_log_buf);

        QMessageBox::warning( this, tr("召唤定值区:"), tr("召唤定值区 初始化失败...") );

        //初始化失败

        return ;

    }

    m_pThread->setSheetFlag(true);//是否为工单

    m_pThread->start();



    sheet_exec_dlg_start();





    Setting_Area reply_area;

    while(1)

    {



        usleep(5000);

        cout<<"while!!!!!!!!"<<endl;

        m_sheet_result=m_pThread->getAreaReply(reply_area);

        if(m_sheet_result==0)

            continue;

        else

            break;

    }

    if(m_pThread!=NULL)

    {

        delete m_pThread;

        m_pThread=NULL;

    }

    int ret=m_sheet_result;

    //reply_para=m_reply_para;

    if(ret==-1||ret==-2)

    {

        sprintf(g_log_buf,"定值区号召唤 消息总线注册失败...");

        CLogManager::Instance()->WriteLineT(g_log_buf);

        QMessageBox::warning( this, tr("定值区号召唤:"), tr("定值区号召唤 消息总线注册失败...") );

        return;

    }

    else if(ret==-3)

    {

        sprintf(g_log_buf,"定值区号召唤 消息请求发送失败...");

        CLogManager::Instance()->WriteLineT(g_log_buf);

        QMessageBox::warning( this, tr("定值区号召唤:"), tr("定值区号召唤 消息请求发送失败...") );

        return;

    }



    else if(ret==-5)

    {

        sprintf(g_log_buf,"定值区号召唤 收到的消息长度小于0...");

        CLogManager::Instance()->WriteLineT(g_log_buf);

        QMessageBox::warning( this, tr("定值区号召唤:"), tr("定值区号召唤 收到的消息长度小于0...") );



        return;

    }

    else if(ret==-6)//超时

    {

        sprintf(g_log_buf,"定值区号召唤 超时...");

        CLogManager::Instance()->WriteLineT(g_log_buf);

        QMessageBox::warning( this, tr("定值区号召唤:"), tr("定值区号召唤 超时...") );

        return;





    }

    else if(ret==-7)//中止

    {

        sprintf(g_log_buf,"定值区号召唤 中止...");

        CLogManager::Instance()->WriteLineT(g_log_buf);

        QMessageBox::warning( this, tr("定值区号召唤:"), tr("定值区号召唤 中止...") );

        return;

    }

    else//ret==1

    {

        update_progress_dlg(m_oper_timeout_slice);//接收完成

        SheetStopProgressStep();

        if( reply_area.comm_fac_id != m_term_dev_id )

        {

            QMessageBox::warning( this, tr("定值区号召唤:"), tr("定值区号召唤 终端id不一致 reply=%1 cur=%2...").arg(reply_area.comm_fac_id).arg(m_term_dev_id) );

            return;

        }

        show_area(reply_area);



        return ;



    }

    return  ;

#if 0

    short serv          = CH_DMS_TERMINAL_DATA;

    short event         = MT_DMS_CALL_SETTING_AREA;

    short reply_event   = MT_DMS_SETTING_AREA_REPLY;



    Setting_Area    area = { 0 };

    area.comm_fac_id    = m_term_dev_id;



    MsgBus_RW_Thread *pThread = new MsgBus_RW_Thread();

    pThread->init( m_term_dev_id, m_oper_timeout_slice );

    pThread->init_msg( serv, event, reply_event, area );

    connect( pThread, SIGNAL( finished() ), pThread, SLOT( deleteLater() ) );

    connect( pThread, SIGNAL( Post_CallArea(short,QByteArray) ), this, SLOT( On_MsgBus_CallArea(short,QByteArray) ) );

    connect( pThread, SIGNAL( Post_Finished(short,int) ), this, SLOT( On_MsgBus_Finished(short,int) ) );

    pThread->start();



    set_result_state( false );



    StartProgressStep( reply_event );

    StartProgressDlg();

#endif



}



void TermParamWindow::on_pushBtn_AlterArea_clicked()

{

    QString strOper = tr("切换定值区号");

    if( m_term_dev_id == 0 || m_term_xh_id == 0 )

    {

        ZMessageBox::warning( this, strOper, tr("无效的终端设备或未设置有效的终端型号...") );

        return;

    }

    if(m_pThread!=NULL)

    {

        delete m_pThread;

        m_pThread=NULL;

    }

    //用户权限验证









#ifdef USER_PRIV_CHECK

    if(QMessageBox::warning(this,tr("提示:"),tr("执行该操作需要监护用户登录进行允许操作,是否继续?"),QMessageBox::Yes|QMessageBox::No)==QMessageBox::Yes)

    {

        int expire_time = 0;

        QDateTime dtStartExeTime;

        QString userName;

        if (doLogin(userName, expire_time, dtStartExeTime)){





            if(!delJudgePriv(PRIV_TERM_PARAM_CUSTODY,userName))

            {

                return;

            }

            m_strCustodyName=userName;

        }

        else

        {

            return;

        }

    }

    else

    {

        return;

    }

    setWidText(g_strLoginUserName);

#else

    QString loginName=g_strLoginUserName;

    if(!delJudgePriv(PRIV_TERM_PARAM_OP,loginName,true))

    {

        return;

    }

    g_strLoginUserName=loginName;

    setWidText(g_strLoginUserName);



    QString userName=m_strCustodyName;

    if(!delJudgePriv(PRIV_TERM_PARAM_CUSTODY,userName))

    {

        return;

    }

    m_strCustodyName=userName;

    setWidText(g_strLoginUserName);

#endif











    //激活参数

    short serv          = CH_DMS_TERMINAL_DATA;

    short event         = MT_DMS_ALTER_SETTING_AREA;

    m_download_reply_event   = MT_DMS_ALTER_SETTING_AREA_REPLY;

    m_sheet_result=0;

    m_oper_timeout_slice    = 45;   //操作超时间隔(秒)

    m_pThread=new MsgBus_RW_Thread();

    m_pThread->init( m_term_dev_id, m_oper_timeout_slice );





    m_pThread->setRunFlag(0);



    Setting_Area    area = { 0 };

    area.comm_fac_id    =m_term_dev_id;

    area.cur_serial_num=get_cur_area_no();

    sprintf(g_log_buf,"切换定值区号 comm_fac_id=%ld area=%d",

            area.comm_fac_id,area.cur_serial_num);

    cout<<CLogManager::Instance()->WriteLineT(g_log_buf);

    if(area.cur_serial_num==-1)

    {

        QMessageBox::warning( this, strOper, tr("切换定值区号失败 定值区号不能为－1") );

        return ;

    }

    if( m_pThread->init_msg( serv, event, m_download_reply_event, area ) != 0 )

    {

        sprintf(g_log_buf,"切换定值区号 初始化失败...");

        CLogManager::Instance()->WriteLineT(g_log_buf);

        QMessageBox::warning( this, strOper, tr("切换定值区号 初始化失败...") );

        //初始化失败

        return ;

    }

#if 0

    TWarnData data;

    DMS_TERMINAL_INFO_STRUCT struDmsTerminalInfo;

    m_rtdbOper.ReadTerminalInfo(struDmsTerminalInfo,m_term_dev_id);

    //data.combined_id=struDmsTerminalInfo.feeder_id;

    data.term_id=struDmsTerminalInfo.id;

    data.warn_type=MENU_OPT_DEV_STAT_PARAM_CALL;

    data.feeder_id=struDmsTerminalInfo.feeder_id;

    QString op_str=tr("切换定值区号 由定值区%1切换至定值区%2").arg(m_cur_area).arg(area.cur_serial_num);

    data.op_str=op_str.toStdString();

    sendWarn(data);

#endif

    m_pThread->setSheetFlag(true);//是否为工单

    m_pThread->start();



    sheet_exec_dlg_start();





    Setting_Area reply_area;

    while(1)

    {



        usleep(5000);

        cout<<"while!!!!!!!!"<<endl;

        m_sheet_result=m_pThread->getAreaReply(reply_area);

        if(m_sheet_result==0)

            continue;

        else

            break;

    }

    if(m_pThread!=NULL)

    {

        delete m_pThread;

        m_pThread=NULL;

    }

    int ret=m_sheet_result;

    //reply_para=m_reply_para;

    if(ret==-1||ret==-2)

    {

        sprintf(g_log_buf,"切换定值区号失败 消息总线注册失败...");

        CLogManager::Instance()->WriteLineT(g_log_buf);

        QMessageBox::warning( this, strOper, tr("切换定值区号失败 消息总线注册失败...") );

        return;

    }

    else if(ret==-3)

    {

        sprintf(g_log_buf,"切换定值区号失败 消息请求发送失败...");

        CLogManager::Instance()->WriteLineT(g_log_buf);

        QMessageBox::warning( this, strOper, tr("切换定值区号失败 消息请求发送失败...") );

        return;

    }



    else if(ret==-5)

    {

        sprintf(g_log_buf,"切换定值区号失败 收到的消息长度小于0...");

        CLogManager::Instance()->WriteLineT(g_log_buf);

        QMessageBox::warning( this, strOper, tr("切换定值区号失败 收到的消息长度小于0...") );



        return;

    }

    else if(ret==-6)//超时

    {

        sprintf(g_log_buf,"切换定值区号失败 超时...");

        CLogManager::Instance()->WriteLineT(g_log_buf);

        QMessageBox::warning( this, strOper, tr("切换定值区号失败 超时...") );

        return;





    }

    else if(ret==-7)//中止

    {

        sprintf(g_log_buf,"切换定值区号失败 中止...");

        CLogManager::Instance()->WriteLineT(g_log_buf);

        QMessageBox::warning( this, strOper, tr("切换定值区号 中止...") );

        return;

    }

    else//ret==1

    {

        update_progress_dlg(m_oper_timeout_slice);//接收完成

        SheetStopProgressStep();

        if( reply_area.comm_fac_id != m_term_dev_id )

        {

            QMessageBox::warning( this, strOper, tr("切换定值区号失败 reply=%1 cur=%2...").arg(reply_area.comm_fac_id).arg(m_term_dev_id) );

            return;

        }

        show_area_alter( reply_area,area.cur_serial_num);

        return ;



    }

    return  ;

#if 0

    QString strOper = tr("切换定值分区");

#ifndef _SOUTHGRID_PROJECT_

    if( m_term_dev_id == 0 || m_term_xh_id == 0 )

    {

        ZMessageBox::warning( this, strOper, tr("无效的终端设备或未设置有效的终端型号...") );

        return;

    }



    if( m_reply_event != 0 )

    {

        show_tip_doing();

        return;

    }



    short serv          = CH_DMS_TERMINAL_DATA;

    short event         = MT_DMS_ALTER_SETTING_AREA;

    short reply_event   = MT_DMS_ALTER_SETTING_AREA_REPLY;



    Setting_Area    area = { 0 };

    area.comm_fac_id    = m_term_dev_id;

    area.cur_serial_num=get_cur_area_no();



    MsgBus_RW_Thread *pThread = new MsgBus_RW_Thread();

    pThread->init( m_term_dev_id, m_oper_timeout_slice );

    pThread->init_msg( serv, event, reply_event, area );

    connect( pThread, SIGNAL( finished() ), pThread, SLOT( deleteLater() ) );

    connect( pThread, SIGNAL( Post_AlterArea(short,QByteArray) ), this, SLOT( On_MsgBus_AlterArea(short,QByteArray) ) );

    connect( pThread, SIGNAL( Post_Finished(short,int) ), this, SLOT( On_MsgBus_Finished(short,int) ) );

    pThread->start();



    set_result_state( false );



    StartProgressStep( reply_event );

    StartProgressDlg();

#endif

#endif

}

void  TermParamWindow::check_area(const qint32 &alter_area)

{

    QString strOper = tr("校验定值区号");



#if  0

    Setting_Area tmpreply_area;

    tmpreply_area.comm_fac_id=m_term_dev_id;

    tmpreply_area.min_serial_num=0;

    tmpreply_area.max_serial_num=1;

    tmpreply_area.cur_serial_num=1;

    show_area(tmpreply_area);

    return;

#endif

    if( m_term_dev_id == 0L || m_term_xh_id == 0L )

    {

        ZMessageBox::warning( this, strOper, tr("无效的终端设备或未设置有效的终端型号...") );

        return;

    }







    if(m_pThread!=NULL)

    {

        delete m_pThread;

        m_pThread=NULL;

    }

    //激活参数

    short serv          = CH_DMS_TERMINAL_DATA;

    short event         = MT_DMS_CALL_SETTING_AREA;

    m_download_reply_event   = MT_DMS_SETTING_AREA_REPLY;

    m_sheet_result=0;

    m_pThread=new MsgBus_RW_Thread();

    m_pThread->init( m_term_dev_id, m_oper_timeout_slice );





    m_pThread->setRunFlag(0);



    Setting_Area    area = { 0 };

    area.comm_fac_id    =m_term_dev_id;

    sprintf(g_log_buf,"校验定值区 comm_fac_id=%ld ",

            area.comm_fac_id);

    cout<<CLogManager::Instance()->WriteLineT(g_log_buf);

    if( m_pThread->init_msg( serv, event, m_download_reply_event, area ) != 0 )

    {

        sprintf(g_log_buf,"校验定值区 初始化失败...");

        CLogManager::Instance()->WriteLineT(g_log_buf);

        QMessageBox::warning( this, tr("校验定值区:"), tr("校验定值区 初始化失败...") );

        //初始化失败

        return ;

    }

    m_pThread->setSheetFlag(true);//是否为工单

    m_pThread->start();



    sheet_exec_dlg_start();





    Setting_Area reply_area;

    while(1)

    {



        usleep(5000);

        cout<<"while!!!!!!!!"<<endl;

        m_sheet_result=m_pThread->getAreaReply(reply_area);

        if(m_sheet_result==0)

            continue;

        else

            break;

    }

    if(m_pThread!=NULL)

    {

        delete m_pThread;

        m_pThread=NULL;

    }

    int ret=m_sheet_result;

    sprintf(g_log_buf,"召唤校验定值区返回结果 comm_fac_id=%ld cur_area=%d min_area=%d max_area=%d",

            reply_area.comm_fac_id,reply_area.cur_serial_num,reply_area.min_serial_num,reply_area.max_serial_num);

    cout<<CLogManager::Instance()->WriteLineT(g_log_buf);

    //reply_para=m_reply_para;

    if(ret==-1||ret==-2)

    {

        sprintf(g_log_buf,"定值区校验 消息总线注册失败...");

        CLogManager::Instance()->WriteLineT(g_log_buf);

        QMessageBox::warning( this, tr("定值区校验:"), tr("定值区校验 消息总线注册失败...") );

        return;

    }

    else if(ret==-3)

    {

        sprintf(g_log_buf,"定值区校验 消息请求发送失败...");

        CLogManager::Instance()->WriteLineT(g_log_buf);

        QMessageBox::warning( this, tr("定值区校验:"), tr("定值区校验 消息请求发送失败...") );

        return;

    }



    else if(ret==-5)

    {

        sprintf(g_log_buf,"定值区校验 收到的消息长度小于0...");

        CLogManager::Instance()->WriteLineT(g_log_buf);

        QMessageBox::warning( this, tr("定值区校验:"), tr("定值区校验 收到的消息长度小于0...") );



        return;

    }

    else if(ret==-6)//超时

    {

        sprintf(g_log_buf,"定值区校验 超时...");

        CLogManager::Instance()->WriteLineT(g_log_buf);

        QMessageBox::warning( this, tr("定值区校验:"), tr("定值区校验 超时...") );

        return;





    }

    else if(ret==-7)//中止

    {

        sprintf(g_log_buf,"定值区校验 中止...");

        CLogManager::Instance()->WriteLineT(g_log_buf);

        QMessageBox::warning( this, tr("定值区校验:"), tr("定值区校验 中止...") );

        return;

    }

    else//ret==1

    {

        update_progress_dlg(m_oper_timeout_slice);//接收完成

        SheetStopProgressStep();

        if( reply_area.comm_fac_id != m_term_dev_id )

        {

            QMessageBox::warning( this, tr("定值区校验:"), tr("定值区校验 终端id不一致 reply=%1 cur=%2...").arg(reply_area.comm_fac_id).arg(m_term_dev_id) );

            return;

        }

        TWarnData data;

        DMS_TERMINAL_INFO_STRUCT struDmsTerminalInfo;

        m_rtdbOper.ReadTerminalInfo(struDmsTerminalInfo,m_term_dev_id);

        //data.combined_id=struDmsTerminalInfo.feeder_id;

        data.term_id=struDmsTerminalInfo.id;

        data.warn_type=MENU_OPT_AREA_CHECK;

        data.feeder_id=struDmsTerminalInfo.feeder_id;

        QString op_str;



        if(reply_area.cur_serial_num==alter_area)

        {



            sprintf(g_log_buf,"定值区校验成功 切换定值＝%d 终端当前定值＝%d ",

                    alter_area,reply_area.cur_serial_num);

            cout<<CLogManager::Instance()->WriteLineT(g_log_buf);



            op_str=tr("校验定值区 由定值区%1切换至定值区%2 校验成功").arg(m_cur_area).arg(alter_area);

            show_check_area(reply_area);

            data.op_str=op_str.toStdString();

            sendWarn(data);



            QString strOpDesc = tr("切换定值区");

            QString strText = tr("切换成功");

            ZMessageBox::information( this, strOpDesc, strText );





        }

        else

        {

            sprintf(g_log_buf,"定值区校验失败 切换定值＝%d 终端当前定值＝%d ",

                    alter_area,reply_area.cur_serial_num);



            cout<<CLogManager::Instance()->WriteLineT(g_log_buf);

            op_str=tr("校验定值区 由定值区%1切换至定值区%2 校验失败").arg(m_cur_area).arg(alter_area);

            data.op_str=op_str.toStdString();

            sendWarn(data);



            QString strOpDesc = tr("切换定值区");

            QString strText = tr("切换定值区号失败");

            ZMessageBox::information( this, strOpDesc, strText );



        }



        return ;



    }

    return  ;





}

void  TermParamWindow::GetFileInfo()

{

    int ret_code;

    SEQOutDataTypeStru out_data_type;

    CSqlResultAlignClient sql_align;

    SEQDBErrorStru seq_db_err;

    int record_num;

    TSelectResultStru result_ptr;

    VIndicator null_vec;

    sql_sp_client_base* sql_sp_client=new sql_sp_client_base();



    vec_file_info.clear();



    string sql;

    // sql+="select a.term_id,a.pms_id,a.line_no,b.fix_code,b.fix_value,b.update_time from dms_cb_device a join dms_terminal_fix b on a.pms_id=b.pms_id where a.pms_id is not null;";

    sql+="select b.id,a.term_id,a.id,a.line_no,b.fix_code,b.fix_value,b.update_time from dms_cb_device a join dms_terminal_fix b on a.id=b.dev_id ";



    ret_code = sql_sp_client->SelectSql(sql.c_str(), QUERY_ALL_RESULT, out_data_type, result_ptr, seq_db_err);

    if (ret_code == 0)

    {

        record_num = result_ptr.data_num;

        if(record_num>0)

        {

            null_vec.clear();

            FILE_INFO* ptr=new FILE_INFO[record_num];

            ret_code = sql_align.GetAlignResultClient(result_ptr.field_info,result_ptr.data_value_seq,null_vec,ptr,sizeof(FILE_INFO));

            if(ret_code>=0)

            {

                for(int i=0;i<record_num;i++)

                {

                    FILE_INFO tmp_info;

                    tmp_info.id=ptr[i].id;

                    tmp_info.term_id=ptr[i].term_id;

                    // sprintf(tmp_info.pms_id,ptr[i].pms_id);

                    tmp_info.dev_id=ptr[i].dev_id;

                    tmp_info.line_no=ptr[i].line_no;

                    sprintf(tmp_info.fix_code,ptr[i].fix_code);

                    sprintf(tmp_info.fix_value,ptr[i].fix_value);

                    sprintf(tmp_info.update_time,ptr[i].update_time);

                    vec_file_info.push_back(tmp_info);

                }

            }

            delete ptr;

        }

    }

    delete sql_sp_client;

    return ;

}



void TermParamWindow::on_pushBtn_Compare_clicked()

{

    QString strOper = tr("定值对比");

    int cur_row=ui->tableWidget_Term->currentRow();

    cout<<"cur_row======"<<cur_row<<endl;

    QTableWidgetItem *pDataItem = ui->tableWidget_Term->item( cur_row, 0 );

    if( pDataItem == NULL )

    {

        QMessageBox::warning( this, strOper, tr("无效的终端设备或未设置有效的终端型号...") );

        return;

    }

    qint64 term_dev_id = pDataItem->data( Qt::UserRole+10 ).toLongLong();

    cout<<"term_dev_id===="<<term_dev_id<<endl;



    GetFileInfo();



    cout<<"vec_file_info.size==="<<vec_file_info.size()<<endl;

    QList< QStandardItemModel * >   lstModel;

    /*lstModel.append( (QStandardItemModel *)ui->tableView_Para0->model() );

       lstModel.append( (QStandardItemModel *)ui->tableView_Para1->model() );

       lstModel.append( (QStandardItemModel *)ui->tableView_Para2->model() );*/

    QMap<qint32,QTableView* >::iterator it=m_paramType_widget_map.begin();

    for(;it!=m_paramType_widget_map.end();++it)

    {

        lstModel.append((QStandardItemModel *)it.value()->model());

    }







    m_code_fileInfo_map.clear();

    QStandardItemModel *pModel;

    foreach( pModel, lstModel )

    {

        if( pModel == NULL ) continue;

        int line_no=get_cur_line_no();

        cout<<"line_no===="<<line_no<<",pModel->rowCount==="<<pModel->rowCount()<<endl;

        for( int row = 0; row < pModel->rowCount(); row++ )

        {

            QStandardItem *code_Item = pModel->item( row, PARA_COL_CODE );

            QStandardItem *value_Item2 = pModel->item( row, PARA_COL_VALUE );

            QString fix_code=code_Item->data(0).toString();

            QString value_str2=value_Item2->data(0).toString();





            cout<<"value_str2==="<<value_str2.toStdString().c_str()<<endl;

            for(int j=0;j<vec_file_info.size();j++)

            {

                long term_id=vec_file_info[j].term_id;

                QString code_str=QString("%1").arg(vec_file_info[j].fix_code);

                int tmp_line=vec_file_info[j].line_no;

                cout<<"当前term_dev_id===="<<term_dev_id<<"，库里term_id==="<<term_id<<endl;

                cout<<"库里tmp_line====="<<tmp_line<<",当前选择line_no===="<<line_no<<endl;

                cout<<"fix_code==="<<fix_code.toStdString()<<",code_str===="<<code_str.toStdString()<<endl;

                if((term_id==term_dev_id)&&(code_str==fix_code)&&(tmp_line+TERM_PARAM_LINE_NO_BASE==line_no))



                {

                    m_code_fileInfo_map[QString(fix_code).toInt(NULL,16)]=vec_file_info[j];

                    cout<<"find one!!"<<endl<<endl;

                    QString value_str=QString("%1").arg(vec_file_info[j].fix_value);

                    QStandardItem *value_Item = new QStandardItem( tr("%1").arg(vec_file_info[j].fix_value) );

                    cout<<"vec_file_info[j].fix_value==="<<vec_file_info[j].fix_value<<endl;

                    value_Item->setEditable( false );

                    pModel->setItem( row, PARA_COL_ZD_SYS_VALUE, value_Item );





                    QStandardItem *time_Item = new QStandardItem( tr("%1").arg(vec_file_info[j].update_time) );

                    cout<<"vec_file_info[j].update_time==="<<vec_file_info[j].update_time<<endl;

                    time_Item->setEditable( false );

                    pModel->setItem( row, PARA_COL_UPDATE_TIME, time_Item );



                    string comp_str;

                    bool ok;

                    bool ok2;

                    float value_f=value_str.toFloat(&ok);

                    float value_f2=value_str2.toFloat(&ok2);



                    /*if(value_str==value_str2)

                        comp_str="是";

                    else */

                    if(ok&&ok2&&(value_f==value_f2))

                    {

                        comp_str="相同";

                        updateSysParamStatus(QString(fix_code).toInt(NULL,16),3);

                    }

                    else

                    {

                        comp_str="不相同";

                        updateSysParamStatus(QString(fix_code).toInt(NULL,16),1);

                    }



                    TWarnData data;

                    DMS_TERMINAL_INFO_STRUCT struDmsTerminalInfo;

                    m_rtdbOper.ReadTerminalInfo(struDmsTerminalInfo,m_term_dev_id);

                    //data.combined_id=struDmsTerminalInfo.feeder_id;

                    data.term_id=struDmsTerminalInfo.id;

                    data.warn_type=MENU_OPT_PARA_COMPARE;

                    data.feeder_id=struDmsTerminalInfo.feeder_id;

                    QString op_str;

                    op_str=tr("定值对比 召唤值＝%1 整定系统值＝%2 对比%3").arg(value_str2).arg(value_str).arg(comp_str.c_str());

                    data.op_str=op_str.toStdString();

                    sendWarn(data);

                    QStandardItem *compare_Item = new QStandardItem( tr("%1").arg(comp_str.c_str()) );

                    compare_Item->setEditable( false );

                    pModel->setItem( row, PARA_COL_ZD_SYS_CMPY, compare_Item );

                    continue;

                }

            }



        }

    }

}

void TermParamWindow::updateSysParamStatus(const qint32 & code,const qint32 & status)

{

    if(m_code_fileInfo_map.find(code)!=m_code_fileInfo_map.end())

    {

        QString strSql=QString("update d5000.dms_terminal_fix set op_status=%1,update_user='%3' where id=%2")

                .arg(status)

                .arg(m_code_fileInfo_map[code].id)

                .arg(g_strLoginUserName);

        m_hisdbOper.ExecSql(strSql);

    }

}

void TermParamWindow::on_pushBtn_Read_clicked()

{

    QString strOper = tr("召唤参数");



    if( m_term_dev_id == 0 || m_term_xh_id == 0 )

    {

        ZMessageBox::warning( this, strOper, tr("无效的终端设备或未设置有效的终端型号...") );

        return;

    }

    if(m_pThread!=NULL)

    {

        delete m_pThread;

        m_pThread=NULL;

    }



    T_Terminal_Para call_para;

    init_term_read_para( call_para );



    if( call_para.num == 0 )

    {

        ZMessageBox::warning( this, strOper, tr("请勾选需要召唤的参数记录...") );

        return;

    }

    //召唤参数

    short serv          = CH_DMS_TERMINAL_DATA;

    short event         = MT_DMS_TERM_PARA_CALL;;

    m_download_reply_event   = MT_DMS_TERM_PARA_REPLY;

    m_sheet_result=0;

    m_pThread=new MsgBus_RW_Thread();

    m_oper_timeout_slice=45*(ceil(call_para.num/(MAX_PARAM_COUNT*1.0)));//每20个  加45秒

    m_pThread->init( m_term_dev_id, m_oper_timeout_slice );



    m_pThread->setRunFlag(0);

    if( m_pThread->init_msg( serv, event, m_download_reply_event, call_para ) != 0 )

    {

        sprintf(g_log_buf,"召唤参数 初始化失败...");

        CLogManager::Instance()->WriteLineT(g_log_buf);

        QMessageBox::warning( this, tr("召唤参数:"), tr("召唤参数 初始化失败...") );

        //初始化失败

        return ;

    }

    m_pThread->setSheetFlag(true);

    //       connect( m_pThread, SIGNAL( finished() ), m_pThread, SLOT( deleteLater() ) );

    // connect( m_pThread, SIGNAL( Post_SheetParaCall(short,QByteArray,qint32)), this, SLOT( On_MsgBus_SheetParaCall(short,QByteArray,qint32)) );

    m_pThread->start();

    cout<<"zzzzzzzzzzzzzz"<<endl;

    set_result_state(false);

    sheet_exec_dlg_start();



    T_Terminal_Para reply_para;

    while(1)

    {



        usleep(5000);

        cout<<"while!!!!!!!!"<<endl;

        m_sheet_result=m_pThread->getParamReply(reply_para);

        if(m_sheet_result==0)

            continue;

        else

            break;

    }

    if(m_pThread!=NULL)

    {

        delete m_pThread;

        m_pThread=NULL;

    }

    int ret=m_sheet_result;

    //reply_para=m_reply_para;

    set_result_state(true);



    CLogManager::Instance()->WriteLineT("召唤参数返回结果:");

    for( int i = 0; i < reply_para.terminal_para_seq.size(); i++ )

    {

        Terminal_Para_Info pi = reply_para.terminal_para_seq[i];

        sprintf(g_log_buf,"comm_fac_id=%ld line_no=%d area_no=%d para_type=%d value_type=%d data_type=%d value_f=%f value_i=%d result=%d ",

                pi.comm_fac_id,pi.line_no,pi.serial_num,pi.para_type,pi.value_type,pi.data_type,pi.value_f,pi.value_i,pi.result);

        CLogManager::Instance()->WriteLineT(g_log_buf);

        //  CLogManager::Instance()->WriteLineT(QString::fromUtf8(pi.value_s).toStdString());

    }

    if(ret==-1||ret==-2)

    {

        sprintf(g_log_buf,"召唤参数 消息总线注册失败...");

        CLogManager::Instance()->WriteLineT(g_log_buf);

        QMessageBox::warning( this, tr("召唤参数:"), tr("召唤参数 消息总线注册失败...") );



    }

    else if(ret==-3)

    {

        sprintf(g_log_buf,"召唤参数 消息请求发送失败...");

        CLogManager::Instance()->WriteLineT(g_log_buf);

        QMessageBox::warning( this, tr("召唤参数:"), tr("召唤参数 消息请求发送失败...") );



    }

    /* else if(ret==-4)

    {



    }*/

    else if(ret==-5)

    {

        sprintf(g_log_buf,"召唤参数 收到的消息长度小于0...");

        CLogManager::Instance()->WriteLineT(g_log_buf);

        QMessageBox::warning( this, tr("召唤参数:"), tr("召唤参数 收到的消息长度小于0...") );



    }

    else if(ret==-6)//超时

    {

        QSet<qint32> recv_code;

        for( int i = 0; i < reply_para.terminal_para_seq.size(); i++ )

        {

            Terminal_Para_Info &info = reply_para.terminal_para_seq[i];



            recv_code.insert(info.data_type);

            show_para_info( info );



            update_para_info( info );



            update_oper_result( info.data_type, 10/*info.result*/ );  //召唤的返回结果不可用

            insertParamHisByPara(info,1,1,1);

        }

        update_reply_para( reply_para );  //更新进商用数据库

        for( int i = 0; i < call_para.terminal_para_seq.size(); i++ )

        {

            Terminal_Para_Info &info = call_para.terminal_para_seq[i];

            if(recv_code.find(info.data_type)==recv_code.end())

            {

                update_oper_result( info.data_type, 103/*info.result*/ );  //召唤的返回结果不可用

                insertParamHisByPara(info,1,-1,1);

            }

        }

        sprintf(g_log_buf,"召唤参数 超时...");

        CLogManager::Instance()->WriteLineT(g_log_buf);

        QMessageBox::warning( this, tr("召唤参数:"), tr("召唤参数 超时...") );







    }

    else if(ret==-7)//中止

    {



        QSet<qint32> recv_code;

        for( int i = 0; i < reply_para.terminal_para_seq.size(); i++ )

        {

            Terminal_Para_Info &info = reply_para.terminal_para_seq[i];



            recv_code.insert(info.data_type);

            show_para_info( info );



            update_para_info( info );



            update_oper_result( info.data_type, 10/*info.result*/ );  //召唤的返回结果不可用

            insertParamHisByPara(info,1,1,1);

        }



        update_reply_para( reply_para );  //更新进商用数据库

        for( int i = 0; i < call_para.terminal_para_seq.size(); i++ )

        {

            Terminal_Para_Info &info = call_para.terminal_para_seq[i];

            if(recv_code.find(info.data_type)==recv_code.end())

            {

                update_oper_result( info.data_type, 106/*info.result*/ );  //召唤的返回结果不可用

                insertParamHisByPara(info,1,-1,1);

            }

        }

        sprintf(g_log_buf,"召唤参数 中止...");

        CLogManager::Instance()->WriteLineT(g_log_buf);

        QMessageBox::warning( this, tr("召唤参数:"), tr("召唤参数 中止...") );



    }

    else//ret==1

    {



        update_progress_dlg(m_oper_timeout_slice);//接收完成

        SheetStopProgressStep();

        cout<<"reply_size=="<<reply_para.terminal_para_seq.size()<<endl;

        QSet<qint32> recv_code;

        for( int i = 0; i < reply_para.terminal_para_seq.size(); i++ )

        {

            Terminal_Para_Info &info = reply_para.terminal_para_seq[i];



            recv_code.insert(info.data_type);

            show_para_info( info );



            update_para_info( info );



            update_oper_result( info.data_type, 10/*info.result*/ );  //召唤的返回结果不可用

            insertParamHisByPara(info,1,1,1);

        }

        update_reply_para( reply_para );  //更新进商用数据库





    }

    return;



}



void TermParamWindow::on_pushBtn_Write_clicked()

{

    QString strOper = tr("下装参数");



    if( m_term_dev_id == 0 || m_term_xh_id == 0 )

    {

        ZMessageBox::warning( this, strOper, tr("无效的终端设备或未设置有效的终端型号...") );

        return;

    }



    if(QMessageBox::warning(this,tr("提示:"),tr("是否确认下装?"),QMessageBox::Yes|QMessageBox::No)==QMessageBox::No)

    {

      return;

    }



    guard_para_info_stru para_info;

    int write_type;

    m_write_type=2;

    if(m_is_allow_zd_op==1)

    {

        QPushButton *okbtn = new QPushButton(QString::fromLocal8Bit("整定系统值下装"));

        QPushButton *cancelbtn = new QPushButton(QString::fromLocal8Bit("召唤值下装"));

        QPushButton *quietbtn = new QPushButton(QString::fromLocal8Bit("取消"));

        QMessageBox *mymsgbox = new QMessageBox;





        mymsgbox->setWindowTitle(QString::fromLocal8Bit("下装参数"));

        mymsgbox->setText(QString::fromLocal8Bit("请选择下装类型"));

        mymsgbox->addButton(okbtn, QMessageBox::ApplyRole);

        mymsgbox->addButton(cancelbtn, QMessageBox::ApplyRole);

        mymsgbox->addButton(quietbtn, QMessageBox::RejectRole);

        mymsgbox->show();



        mymsgbox->exec();//阻塞等待用户输入

        if (mymsgbox->clickedButton() == okbtn)//点击了OK按钮

        {

            write_type = 1;

        }

        else if (mymsgbox->clickedButton() == cancelbtn)

        {

            write_type = 2;

        }

        else

        {

            return;

        }

    }

    else

    {

        write_type=2;

    }

    m_write_type=write_type;

    T_Terminal_Para modify_para;

    QList<TWarnData>  warn_data_lst;

    int result=init_term_write_para(para_info,modify_para,write_type,warn_data_lst);

    if(result==-1)

    {

        sprintf(g_log_buf,"参数转换失败 不允许下装...");

        CLogManager::Instance()->WriteLineT(g_log_buf);

        QMessageBox::warning( this, tr("id = %1").arg(m_term_dev_id), tr("参数转换失败 不允许下装...") );

        return ;

    }

    else if(result==-2)

    {

        sprintf(g_log_buf,"下装失败包含有不允许修改的参数...");

        CLogManager::Instance()->WriteLineT(g_log_buf);

        QMessageBox::warning( this, tr("id = %1").arg(m_term_dev_id), tr("下装失败包含有不允许修改的参数...") );

        return ;

    }

    else if(result==-3)

    {

        sprintf(g_log_buf,"获取当前定值数据失败...");

        CLogManager::Instance()->WriteLineT(g_log_buf);

        QMessageBox::warning( this, tr("id = %1").arg(m_term_dev_id), tr("获取当前定值数据失败...") );

        return ;

    }

    else if(result==-4)

    {



        QMessageBox::warning( this, tr("id = %1").arg(m_term_dev_id), tr("参数值有区间限制 不允许下装...") );

        return ;

    }

    else if(result==-5)

    {







        ZMessageBox::warning( this, strOper, tr("有未召唤的参数，请先召唤需要下装的参数...") );

        return;



    }



    if( modify_para.num == 0 )

    {

        ZMessageBox::warning( this, strOper, tr("请勾选需要下装的参数记录...") );

        return;

    }









#ifdef USER_PRIV_CHECK

    if(!delJudgePriv(PRIV_TERM_PARAM_OP,g_strLoginUserName))

    {

        return;

    }

    setWidText(g_strLoginUserName);

    if(QMessageBox::warning(this,tr("提示:"),tr("执行该操作需要监护用户登录进行允许操作,是否继续?"),QMessageBox::Yes|QMessageBox::No)==QMessageBox::Yes)

    {

        int expire_time = 0;

        QDateTime dtStartExeTime;

        QString userName;

        if (doLogin(userName, expire_time, dtStartExeTime)){





            if(!delJudgePriv(PRIV_TERM_PARAM_CUSTODY,userName))

            {

                return;

            }

            m_strCustodyName=userName;

        }

        else

        {

            return;

        }

    }

    else

    {

        return;

    }

    setWidText(g_strLoginUserName);

#elif USER_PRIV_GUARD

    QString loginName=g_strLoginUserName;

    if(!delJudgePriv(PRIV_TERM_PARAM_OP,loginName,true))

    {

        return;

    }

    g_strLoginUserName=loginName;

    setWidText(g_strLoginUserName);



    para_info.user_name=g_strLoginUserName;

    guardParaDlg  *guardDlg=new guardParaDlg(para_info,this);

    if(guardDlg->exec()!=QDialog::Accepted)

    {

        if(guardDlg!=NULL)

        {

            delete guardDlg;

            guardDlg=NULL;

        }

        return;

    }

    m_strCustodyName=guardDlg->getGuardUser();

    if(guardDlg!=NULL)

    {

        delete guardDlg;

        guardDlg=NULL;

    }

#else

    //用户权限验证

    QString loginName=g_strLoginUserName;

    if(!delJudgePriv(PRIV_TERM_PARAM_OP,loginName,true))

    {

        return;

    }

    g_strLoginUserName=loginName;

    setWidText(g_strLoginUserName);





    QString userName=m_strCustodyName;

    if(!delJudgePriv(PRIV_TERM_PARAM_CUSTODY,userName))

    {

        return;

    }

    m_strCustodyName=userName;

    setWidText(g_strLoginUserName);

#endif



    T_Terminal_Para modify_para_per;

    QSet<int> code_set;

    m_bContinue=true;



    set_result_state(false);

    set_result_state(true);



    set_check_result_state(false);

    set_check_result_state(true);

    for(int para_num=0;para_num<modify_para.terminal_para_seq.size();++para_num)

    {





        modify_para_per.terminal_para_seq.push_back(modify_para.terminal_para_seq[para_num]);

        code_set.insert(modify_para.terminal_para_seq[para_num].data_type);

        if(modify_para_per.terminal_para_seq.size()%MAX_PARAM_COUNT!=0)

        {

            if(modify_para.terminal_para_seq.size()!=para_num+1)

                continue;

        }



        modify_para_per.num=modify_para.terminal_para_seq.size();



        CLogManager::Instance()->WriteLineT("本次下装参数:");

        for(int now_ct=0;now_ct<modify_para_per.terminal_para_seq.size();++now_ct)

        {

            Terminal_Para_Info pi = { 0 };

            pi=modify_para_per.terminal_para_seq[now_ct];

            sprintf(g_log_buf,"comm_fac_id=%ld line_no=%d area_no=%d para_type=%d value_type=%d data_type=%d result=%d value=%s",

                    pi.comm_fac_id,pi.line_no,pi.serial_num,pi.para_type,pi.value_type,pi.data_type,pi.result,param_value_to_string(pi).toStdString().c_str());

            CLogManager::Instance()->WriteLineT(g_log_buf);



        }





        if(m_pThread!=NULL)

        {

            delete m_pThread;

            m_pThread=NULL;

        }





        cout<<"----------国网参数预下装-----------"<<endl;

        short serv          = CH_DMS_TERMINAL_DATA;

        short event         = MT_DMS_TERM_PARA_MODIFY_PRE;

        m_download_reply_event  = MT_DMS_TERM_PARA_MODIFY_REPLY;

        m_sheet_result=0;

        m_oper_timeout_slice    = 45;   //操作超时间隔(秒)

        m_pThread=new MsgBus_RW_Thread();

        m_pThread->init( m_term_dev_id, m_oper_timeout_slice );

        m_pThread->setRunFlag(0);

        if( m_pThread->init_msg( serv, event, m_download_reply_event, modify_para_per ) != 0 )

        {

            //初始化失败

            sprintf(g_log_buf,"下装参数 初始化失败...");

            CLogManager::Instance()->WriteLineT(g_log_buf);

            //QMessageBox::warning( this, tr("下装参数:"), tr("下装参数 初始化失败...") );

            // return ;

        }

        m_pThread->setSheetFlag(true);

        //connect( m_pThread, SIGNAL( Post_SheetParaCall(short,QByteArray,qint32)), this, SLOT( On_MsgBus_SheetParaCall(short,QByteArray,qint32)) );

        m_pThread->start();

        //set_result_state(false);

        sheet_exec_dlg_start();

        T_Terminal_Para reply_para;

        //set_result_state(true);

        while(1)

        {



            usleep(5000);

            cout<<"while!!!!!!!!"<<endl;

            m_sheet_result=m_pThread->getParamReply(reply_para);

            if(m_sheet_result==0)

                continue;

            else

                break;

        }



        if(m_pThread!=NULL)

        {

            delete m_pThread;

            m_pThread=NULL;

        }

        int ret=m_sheet_result;

        if(ret==-1||ret==-2)

        {

            sprintf(g_log_buf,"下装参数 消息总线注册失败...");

            CLogManager::Instance()->WriteLineT(g_log_buf);

            // QMessageBox::warning( this, tr("下装参数:"), tr("下装参数 消息总线注册失败...") );



        }

        else if(ret==-3)

        {

            sprintf(g_log_buf,"下装参数 消息请求发送失败...");

            CLogManager::Instance()->WriteLineT(g_log_buf);

            //QMessageBox::warning( this, tr("下装参数:"), tr("下装参数 消息请求发送失败...") );



        }

        /* else if(ret==-4)

    {



    }*/

        else if(ret==-5)

        {

            sprintf(g_log_buf,"下装参数 收到的消息长度小于0...");

            CLogManager::Instance()->WriteLineT(g_log_buf);

            //QMessageBox::warning( this, tr("下装参数:"), tr("下装参数 收到的消息长度小于0...") );



        }

        else if(ret==-6)//超时

        {





            if(reply_para.terminal_para_seq.size()>0)//国网只返回一个结果参数

            {

                for( int i = 0; i < reply_para.terminal_para_seq.size(); i++ )

                {

                    Terminal_Para_Info &info = reply_para.terminal_para_seq[i];

                    sprintf(g_log_buf,"下装返回结果:result=%d",info.result);

                    CLogManager::Instance()->WriteLineT(g_log_buf);

                    update_oper_result_download(info.data_type,info.result );

                    insertParamHisByPara(info,2,-1,m_write_type==2?1:99);

                }

            }

            else

            {

                for( int i = 0; i < modify_para_per.terminal_para_seq.size(); i++ )

                {

                    Terminal_Para_Info &info = modify_para_per.terminal_para_seq[i];

                    update_oper_result_download(info.data_type, 103);

                    insertParamHisByPara(info,2,-1,m_write_type==2?1:99);

                    if(m_write_type==1)

                    {

                        updateSysParamStatus(info.data_type,1);

                    }



                }

            }

            sprintf(g_log_buf,"下装参数 超时...");

            CLogManager::Instance()->WriteLineT(g_log_buf);

            //QMessageBox::warning( this, tr("下装参数:"), tr("下装参数 超时...") );

        }

        else if(ret==-7)//中止

        {







            if(reply_para.terminal_para_seq.size()>0)//国网只返回一个结果参数

            {

                for( int i = 0; i < reply_para.terminal_para_seq.size(); i++ )

                {

                    Terminal_Para_Info &info = reply_para.terminal_para_seq[i];

                    sprintf(g_log_buf,"下装返回结果:result=%d",info.result);

                    CLogManager::Instance()->WriteLineT(g_log_buf);

                    update_oper_result_download(info.data_type,info.result );

                    insertParamHisByPara(info,2,-1,m_write_type==2?1:99);

                }

            }

            else

            {

                for( int i = 0; i < modify_para_per.terminal_para_seq.size(); i++ )

                {

                    Terminal_Para_Info &info = modify_para_per.terminal_para_seq[i];

                    update_oper_result_download( info.data_type,106);

                    insertParamHisByPara(info,2,-1,m_write_type==2?1:99);



                    if(m_write_type==1)

                    {

                        updateSysParamStatus(info.data_type,1);

                    }



                }

            }



            sprintf(g_log_buf,"下装参数 中止...");

            CLogManager::Instance()->WriteLineT(g_log_buf);

            return;

            //QMessageBox::warning( this, tr("下装参数:"), tr("下装参数 中止...") );

        }

        else//ret==1

        {

            update_progress_dlg(m_oper_timeout_slice);//接收完成

            SheetStopProgressStep();

            for( int i = 0; i < reply_para.terminal_para_seq.size(); i++ )//只返回一个结果

            {

                Terminal_Para_Info &info = reply_para.terminal_para_seq[i];



                sprintf(g_log_buf,"下装返回结果:result=%d",info.result);

                CLogManager::Instance()->WriteLineT(g_log_buf);



                for(int zz=0;zz<warn_data_lst.count();++zz)

                {

                    QString strResult = get_result_desc( info.result );



                    TWarnData warn_data=warn_data_lst[zz];

                    if(warn_data.code_10==info.data_type)

                    {

                        QString op_str=warn_data.op_str.c_str();

                        op_str.replace("[]",strResult);

                        warn_data.op_str=op_str.toStdString();

                        sendWarn(warn_data);

                    }

                }

                if(info.result==2)

                {

                    for( int kk = 0; kk < modify_para_per.terminal_para_seq.size(); kk++ )

                    {

                        Terminal_Para_Info modify_info = modify_para_per.terminal_para_seq[kk];



                        insertParamHisByPara(modify_info,2,1,m_write_type==2?1:99);

                        update_oper_result_download(modify_info.data_type,info.result );

                        if(m_write_type==1)

                        {

                            updateSysParamStatus(modify_info.data_type,2);

                        }



                    }

#if 0

                    waitDlg  *dlg=new waitDlg(m_maxsec,this);

                    connect(dlg,SIGNAL(exec_sigal(int)),this,SLOT(exec_slot(int)));

                    dlg->exec();

                    if(dlg!=NULL)

                    {

                        delete dlg;

                        dlg=NULL;

                    }

#endif

                    exec_slot(code_set,ACT_EXEC);

                    if(!m_bContinue)

                        return;

                    //on_pushBtn_Act_clicked(modify_para_per,warn_data_lst);





                }

                else

                {

                    for( int kk = 0; kk < modify_para_per.terminal_para_seq.size(); kk++ )

                    {

                        Terminal_Para_Info modify_info = modify_para_per.terminal_para_seq[kk];



                        update_oper_result_download(modify_info.data_type,info.result );

                        insertParamHisByPara(modify_info,2,-1,m_write_type==2?1:99);

                        if(m_write_type==1)

                        {

                            updateSysParamStatus(modify_info.data_type,1);

                        }



                    }

                }



            }











        }



        modify_para_per.num=0;

        modify_para_per.terminal_para_seq.clear();

        code_set.clear();



    }



    return ;

#if 0

    QString strOper = tr("下装参数");



    if( m_term_dev_id == 0 || m_term_xh_id == 0 )

    {

        ZMessageBox::warning( this, strOper, tr("无效的终端设备或未设置有效的终端型号...") );

        return;

    }



    if( m_reply_event != 0 )

    {

        show_tip_doing();

        return;

    }



    int write_type;

    QPushButton *okbtn = new QPushButton(QString::fromLocal8Bit("整定系统值下装"));

    QPushButton *cancelbtn = new QPushButton(QString::fromLocal8Bit("召唤值下装"));

    QMessageBox *mymsgbox = new QMessageBox;





    mymsgbox->setWindowTitle(QString::fromLocal8Bit("下装参数"));

    mymsgbox->setText(QString::fromLocal8Bit("请选择下装类型"));

    mymsgbox->addButton(okbtn, QMessageBox::AcceptRole);

    mymsgbox->addButton(cancelbtn, QMessageBox::RejectRole);

    mymsgbox->show();



    mymsgbox->exec();//阻塞等待用户输入

    if (mymsgbox->clickedButton()==okbtn)//点击了OK按钮

    {

        write_type=1;

    }

    else if (mymsgbox->clickedButton()==cancelbtn)

    {

        write_type=2;

    }

    T_Terminal_Para para;

    //init_term_write_para( para ,write_type);

    if(!init_term_write_para( para ,write_type))

    {

        QMessageBox::warning( this, tr("id = %1").arg(m_term_dev_id), tr("参数转换失败 不允许下装...") );

        return ;

    }



    if( para.num == 0 )

    {

        ZMessageBox::warning( this, strOper, tr("请勾选需要下装的参数记录...") );

        return;

    }



    short serv          = CH_DMS_TERMINAL_DATA;

    short event         = MT_DMS_TERM_PARA_MODIFY_PRE;

    short reply_event   = MT_DMS_TERM_PARA_MODIFY_REPLY;



    MsgBus_RW_Thread *pThread = new MsgBus_RW_Thread();

    pThread->init( m_term_dev_id, m_oper_timeout_slice );

    if( pThread->init_msg( serv, event, reply_event, para ) != 0 )

    {

        delete pThread;

        pThread = NULL;



        ZMessageBox::warning( this, strOper, tr("下装参数太多,请分批下装...") );

        return;

    }

    connect( pThread, SIGNAL( finished() ), pThread, SLOT( deleteLater() ) );

    connect( pThread, SIGNAL( Post_ParaModifyPre(short,QByteArray) ), this, SLOT( On_MsgBus_ParaModifyPre(short,QByteArray) ) );

    connect( pThread, SIGNAL( Post_Finished(short,int) ), this, SLOT( On_MsgBus_Finished(short,int) ) );

    pThread->start();



    StartProgressStep( reply_event );

    StartProgressDlg();

#endif

}



void TermParamWindow::on_pushBtn_Act_clicked(const QSet<int> &code_set,T_Terminal_Para act_para,QList<TWarnData>  warn_data_lst)

{

    QString strOper = tr("激活参数");



    if( m_term_dev_id == 0 || m_term_xh_id == 0 )

    {

        // ZMessageBox::warning( this, strOper, tr("无效的终端设备或未设置有效的终端型号...") );

        return;

    }









    if(m_pThread!=NULL)

    {

        delete m_pThread;

        m_pThread=NULL;

    }

#if 0



    //用户权限验证

#ifdef USER_PRIV_CHECK

    if(!delJudgePriv(PRIV_TERM_PARAM_OP,g_strLoginUserName))

    {

        return;

    }

    setWidText(g_strLoginUserName);

    if(QMessageBox::warning(this,tr("提示:"),tr("执行该操作需要监护用户登录进行允许操作,是否继续?"),QMessageBox::Yes|QMessageBox::No)==QMessageBox::Yes)

    {

        int expire_time = 0;

        QDateTime dtStartExeTime;

        QString userName;

        if (doLogin(userName, expire_time, dtStartExeTime)){





            if(!delJudgePriv(PRIV_TERM_PARAM_CUSTODY,userName))

            {

                return;

            }

            m_strCustodyName=userName;

        }

        else

        {

            return;

        }

    }

    else

    {

        return;

    }

    setWidText(g_strLoginUserName);

#else



    QString loginName=g_strLoginUserName;

    if(!delJudgePriv(PRIV_TERM_PARAM_OP,loginName,true))

    {

        return;

    }

    g_strLoginUserName=loginName;

    setWidText(g_strLoginUserName);



    QString userName=m_strCustodyName;

    if(!delJudgePriv(PRIV_TERM_PARAM_CUSTODY,userName))

    {

        return;

    }

    m_strCustodyName=userName;

    setWidText(g_strLoginUserName);



#endif



#endif

    short serv          = CH_DMS_TERMINAL_DATA;

    short event         = MT_DMS_TERM_PARA_MODIFY;

    m_download_reply_event   = MT_DMS_TERM_PARA_MODIFY_CONF;









    T_Terminal_Op para;

    int area_no=get_cur_area_no();

    int line_no=get_cur_line_no();

    //QString str_name=get_cb_name();

    if( line_no >= TERM_PARAM_LINE_NO_BASE ) line_no -= TERM_PARAM_LINE_NO_BASE;

    Terminal_Para_Op item = { 0 };

    item.comm_fac_id    =m_term_dev_id;

    item.line_no        = line_no;

    item.serial_num     = area_no;

    item.para_type      = 0;    //0，运行参数 1，动作定值参数，

    //文档中有此说明，但前置未处理，即同时激活运行参数和动作定值

    para.terminal_op_seq.push_back( item );

    para.num=para.terminal_op_seq.size();



    sprintf(g_log_buf,"激活参数:\ncomm_fac_id=%ld line_no=%d area_no=%d para_type=%d ",

            item.comm_fac_id,item.line_no,item.serial_num);

    CLogManager::Instance()->WriteLineT(g_log_buf);





    //T_Terminal_Para act_para;

    //QList<TWarnData>  warn_data_lst;

    //init_term_act_para( act_para,warn_data_lst);





    m_oper_timeout_slice    = 45;   //操作超时间隔(秒)

    m_sheet_result=0;

    m_pThread=new MsgBus_RW_Thread();

    m_pThread->init( m_term_dev_id, m_oper_timeout_slice );

    m_pThread->setRunFlag(0);

    if( m_pThread->init_msg( serv, event, m_download_reply_event, para ) != 0 )

    {

        //初始化失败

        sprintf(g_log_buf,"激活参数 初始化失败...");

        CLogManager::Instance()->WriteLineT(g_log_buf);

        // QMessageBox::warning( this, tr("激活参数:"), tr("激活参数 初始化失败...") );



        return ;

    }

    m_pThread->setSheetFlag(true);

    // connect( m_pThread, SIGNAL( Post_SheetParaCall(short,QByteArray,qint32)), this, SLOT( On_MsgBus_SheetParaCall(short,QByteArray,qint32)) );

    m_pThread->start();

    //set_result_state(false);

    sheet_exec_dlg_start();

    //set_result_state(true);

    T_Terminal_Op reply_op;

    while(1)

    {



        usleep(5000);

        cout<<"while!!!!!!!!"<<endl;

        m_sheet_result=m_pThread->getParamReply(reply_op);

        if(m_sheet_result==0)

            continue;

        else

            break;

    }

    if(m_pThread!=NULL)

    {

        delete m_pThread;

        m_pThread=NULL;

    }

    int ret=m_sheet_result;





    if(ret==-1||ret==-2)

    {

        sprintf(g_log_buf,"激活参数 消息总线注册失败...");

        CLogManager::Instance()->WriteLineT(g_log_buf);

        //QMessageBox::warning( this, tr("激活参数:"), tr("激活参数 消息总线注册失败...") );





    }

    else if(ret==-3)

    {

        sprintf(g_log_buf,"激活参数 消息请求发送失败...");

        CLogManager::Instance()->WriteLineT(g_log_buf);

        //QMessageBox::warning( this, tr("激活参数:"), tr("激活参数 消息请求发送失败...") );



    }

    /* else if(ret==-4)

    {



    }*/

    else if(ret==-5)

    {

        sprintf(g_log_buf,"激活参数 收到的消息长度小于0...");

        CLogManager::Instance()->WriteLineT(g_log_buf);

        //QMessageBox::warning( this, tr("激活参数:"), tr("激活参数 收到的消息长度小于0...") );



    }

    else if(ret==-6)//超时

    {



        for( int i = 0; i < act_para.terminal_para_seq.size(); i++ )

        {

            Terminal_Para_Info &info = act_para.terminal_para_seq[i];

            update_oper_result( 103);

            insertParamHisByPara(info,3,-1,m_write_type==2?1:99);

            if(m_write_type==1)

            {

                updateSysParamStatus(info.data_type,1);

            }





        }

        sprintf(g_log_buf,"激活参数 超时...");

        CLogManager::Instance()->WriteLineT(g_log_buf);

        //QMessageBox::warning( this, tr("激活参数:"), tr("激活参数 超时...") );



    }

    else if(ret==-7)//中止

    {



        for( int i = 0; i < act_para.terminal_para_seq.size(); i++ )

        {

            Terminal_Para_Info &info = act_para.terminal_para_seq[i];

            update_oper_result_download(info.data_type,106);

            insertParamHisByPara(info,3,-1,m_write_type==2?1:99);

            if(m_write_type==1)

            {

                updateSysParamStatus(info.data_type,1);

            }



        }

        sprintf(g_log_buf,"激活参数 中止...");

        CLogManager::Instance()->WriteLineT(g_log_buf);

        m_bContinue=false;

        return;

        //QMessageBox::warning( this, tr("激活参数:"), tr("激活参数 中止...") );



    }

    else//ret==1

    {

        update_progress_dlg(m_oper_timeout_slice);//接收完成

        SheetStopProgressStep();



        for( int i = 0; i < reply_op.terminal_op_seq.size(); i++ )

        {

            Terminal_Para_Op &item = reply_op.terminal_op_seq[i];





            QString strText = tr("返回值=%1 ").arg(item.result);

            sprintf(g_log_buf,"激活返回结果:result=%d",item.result);

            CLogManager::Instance()->WriteLineT(g_log_buf);



            for(int zz=0;zz<warn_data_lst.count();++zz)

            {

                QString strResult;

                if(item.result==2)

                {

                    strResult = QString("激活成功(%1)").arg(item.result);



                }

                else

                {

                    strResult = QString("激活失败(%1)").arg(item.result);



                }

                TWarnData warn_data=warn_data_lst[zz];

                warn_data.warn_type=MENU_OPT_PARA_ACTIVATE;

                QString op_str=warn_data.op_str.c_str();

                op_str.replace("[]",strResult);

                warn_data.op_str=op_str.toStdString();

                sendWarn(warn_data);

            }





            if(item.result==2)

            {

                for( int i = 0; i < act_para.terminal_para_seq.size(); i++ )

                {

                    Terminal_Para_Info &info = act_para.terminal_para_seq[i];



                    insertParamHisByPara(info,3,1,m_write_type==2?1:99);

                    if(m_write_type==1)

                    {

                        updateSysParamStatus(info.data_type,3);

                    }



                }

            }

            else

            {

                for( int i = 0; i < act_para.terminal_para_seq.size(); i++ )

                {

                    Terminal_Para_Info &info = act_para.terminal_para_seq[i];



                    insertParamHisByPara(info,3,-1,m_write_type==2?1:99);

                    if(m_write_type==1)

                    {

                        updateSysParamStatus(info.data_type,1);

                    }



                }

            }





                switch( item.result )

                {

                case 0:///711

                    strText = tr("默认(%1)").arg(item.result);

                    for( int i = 0; i < act_para.terminal_para_seq.size(); i++ )

                    {

                    update_oper_result_download(act_para.terminal_para_seq[i].data_type, 711);



                    }

                    //ZMessageBox::warning( this, tr("激活参数:"), strText );

                    break;

                case 1://712

                    strText = tr("激活失败(%1)").arg(item.result);

                    for( int i = 0; i < act_para.terminal_para_seq.size(); i++ )

                    {

                    update_oper_result_download(act_para.terminal_para_seq[i].data_type,712);

                    }

                    //ZMessageBox::critical( this, tr("激活参数:"), strText );

                    break;

                case 2://713

                    strText = tr("激活成功(%1)").arg(item.result);

                    for( int i = 0; i < act_para.terminal_para_seq.size(); i++ )

                    {

                    update_oper_result_download(act_para.terminal_para_seq[i].data_type,713);

                    }// update_reply_act_para(act_para);

                    on_pushBtn_check_clicked(code_set);

                    if(!m_bContinue)

                        return;

                    //ZMessageBox::information( this, tr("激活参数:"), strText );

                    break;



                default:

                    for( int i = 0; i < act_para.terminal_para_seq.size(); i++ )

                    {

                    update_oper_result_download(act_para.terminal_para_seq[i].data_type,714);

                    }

                    strText=tr("激活失败(%1)......").arg(item.result);

                    //ZMessageBox::warning( this, tr("激活参数:"), strText );

                    break;



            }

        }





    }

    return ;



#if 0

    QString strOper = tr("激活参数");



    if( m_term_dev_id == 0 || m_term_xh_id == 0 )

    {

        ZMessageBox::warning( this, strOper, tr("无效的终端设备或未设置有效的终端型号...") );

        return;

    }



    if( m_reply_event != 0 )

    {

        show_tip_doing();

        return;

    }



    T_Terminal_Op para;

    init_term_op_para( para );



    short serv          = CH_DMS_TERMINAL_DATA;

    short event         = MT_DMS_TERM_PARA_MODIFY;

    short reply_event   = MT_DMS_TERM_PARA_MODIFY_CONF;



    MsgBus_RW_Thread *pThread = new MsgBus_RW_Thread();

    pThread->init( m_term_dev_id, m_oper_timeout_slice );

    if( pThread->init_msg( serv, event, reply_event, para ) != 0 )

    {

        delete pThread;

        pThread = NULL;



        ZMessageBox::warning( this, strOper, tr("激活参数太多,请分批激活...") );

        return;

    }

    connect( pThread, SIGNAL( finished() ), pThread, SLOT( deleteLater() ) );

    connect( pThread, SIGNAL( Post_ParaModify(short,QByteArray) ), this, SLOT( On_MsgBus_ParaModify(short,QByteArray) ) );

    connect( pThread, SIGNAL( Post_Finished(short,int) ), this, SLOT( On_MsgBus_Finished(short,int) ) );

    pThread->start();



    set_result_state( false );



    StartProgressStep( reply_event );

    StartProgressDlg();

#endif

}



void TermParamWindow::get_local_area_no( qint8 &is_read_from_conf, qint8 &local_region_id )

{

    is_read_from_conf   = 0;

    local_region_id     = 0;



    const char * szFilePath = "/etc/dms_sys_para.conf";



    QFile file( szFilePath );

    if( ! file.open( QFile::ReadOnly | QFile::Text ) ) return;



    int read_count = 0;



    while( true )

    {

        char szLine[256];

        if( file.readLine( szLine, sizeof( szLine ) ) > 0 )

        {

            QString strLine = QString::fromStdString( szLine ).trimmed();



            QStringList lst = strLine.split( tr("=") );

            if( lst.size() == 2 )

            {

                QString strName     = lst[0].trimmed();

                QString strValue    = lst[1].trimmed();



                strValue = strValue.replace( tr("("), tr("") );

                strValue = strValue.replace( tr(")"), tr("") );

                strValue = strValue.trimmed();



                if( strName.compare( tr("is_read_from_conf"), Qt::CaseInsensitive ) == 0 )

                {

                    is_read_from_conf = strValue.toInt();

                    read_count++;

                }



                if( strName.compare( tr("local_region_id"), Qt::CaseInsensitive ) == 0 )

                {

                    local_region_id = strValue.toInt();

                    read_count++;

                }

            }

        }

        else

        {

            break;

        }



        if( read_count == 2 ) break;

    }



    file.close();

}

//add by lxf for ningxia

void TermParamWindow::on_pushButton_clicked()

{

    readConf();

    QWidget * pCurPage = ui->tabWidget->currentWidget();



    QStandardItemModel *pModel = NULL;





    QString content;

    QString file_name;

    QString term_name=ui->lineEdit_TermName->text();

    QString tm_str=QDateTime::currentDateTime().toString("yyyy-MM-dd hh:mm:ss:zzz");

    content=QString("Time:%1\n").arg(tm_str);

    content+=QString("<Term_param>\n");





    if( ui->tableView_Para0->parentWidget() == pCurPage )

    {

        term_name+=QString("_运行参数.csv");

        pModel = (QStandardItemModel *)ui->tableView_Para0->model();

        content+=QString("#参数名称\t,代码\t,参数值\t,召测时间\t,操作结果\n");

    }

    else if( ui->tableView_Para1->parentWidget() == pCurPage )

    {

        term_name+=QString("_动作定值.csv");

        pModel = (QStandardItemModel *)ui->tableView_Para1->model();

        content+=QString("#动作名称\t,代码\t,动作值\t,召测时间\t,操作结果\n");

    }

    else if( ui->tableView_Para2->parentWidget() == pCurPage )

    {

        term_name+=QString("_固定参数.csv");

        pModel = (QStandardItemModel *)ui->tableView_Para2->model();

        content+=QString("#参数名称\t,代码\t,参数值\t,召测时间\t,操作结果\n");

    }



    cout<<"cout==================="<<pModel->rowCount()<<endl;

    for(int row=0;row<pModel->rowCount();++row)

    {

        content+=QString("@");

        for(int col=0;col<pModel->columnCount();++col)

        {

            QModelIndex index=pModel->index(row,col);

            QString data=pModel->data(index).toString();

            content+=data;

            content+=QString("\t,");

        }

        content+=QString("\n");



    }

    /* for(int row=0;row<ui->tableView_Para1->model()->rowCount();++row)

    {

        content+=QString("@\r\t");

        for(int col=0;col<ui->tableView_Para0->model()->columnCount();++col)

        {

            QModelIndex index=ui->tableView_Para0->model()->index(row,col);

            QString data=ui->tableView_Para0->model()->data(index).toString();

            content+=data;

            content+=QString("\r\t");

        }

      content+=QString("\r\n");

    }

    for(int row=0;row<ui->tableView_Para2->model()->rowCount();++row)

    {

        for(int col=0;col<ui->tableView_Para0->model()->columnCount();++col)

        {

            QModelIndex index=ui->tableView_Para0->model()->index(row,col);

            QString data=ui->tableView_Para0->model()->data(index).toString();

            content+=data;

            content+=QString("\r\t");

        }

         content+=QString("\r\n");

    }*/

    content+=QString("</Term_param>");

    QMessageBox::information(this,tr("通知:"),tr("生成成功!!!"));

    file_name=m_create_file_path+term_name;

    createFile(file_name,content);

}

void TermParamWindow::createFile(QString file_name,QString content)

{



    QMessageBox::warning(this,tr("警告:"),file_name);

    cout<<"str=========="<<content.toStdString()<<endl;

    QFile file(file_name);

    if(file.open(QFile::ReadWrite|QFile::Text))

    {

        QTextStream stream(&file);

        // stream.setCodec("gbk");

        stream<<content;



    }

    else

    {

        QMessageBox::warning(this,tr("警告:"),tr("打开文件失败!!!!!!!!"));

    }

    file.close();

}

void TermParamWindow::readConf()

{

    m_maxsec=30;

    CParaManage *pm;

    QString value;

    QString rootkey=QString("PARAM");

    QString subrootkey=QString("create_file_path");

    char  ret_buf[100];

    char  root[100];

    char subkey[100];

    sprintf(subkey,"%s",subrootkey.toAscii().data());

    sprintf(root,"%s",rootkey.toAscii().data());

    pm = CParaManage::CreateObject("term_param.ini");

    if(pm==NULL)

    {

        cout<<"1lxfffffffffffffffffffffffffffffffffff1"<<endl;

        return ;

    }

    cout<<"root===="<<root<<"  subkey===="<<subkey<<endl;

    int retcode=pm->GetKey( root,subkey, ret_buf,100);

    if(retcode<0)

    {

        CParaManage::RemoveObject(pm);

        cout<<"lxffffffffffffffff2"<<endl;

        return ;

    }



    m_create_file_path=QString(ret_buf);



    sprintf(root,"%s","PATH");

    sprintf(subkey,"%s","exec_file_path");

    retcode=pm->GetKey( root,subkey, ret_buf,100);

    if(retcode<0)

    {

        CParaManage::RemoveObject(pm);

        cout<<"read num is error!!!!!!!!!!!"<<endl;

        return ;

    }

    m_exec_file_path_str=ret_buf;



    cout<<"m_exec_file_path_str"<<m_exec_file_path_str<<endl;

    int size=0;

#if 0

    sprintf(root,"%s","OP_USER");

    sprintf(subkey,"%s","num");

    retcode=pm->GetKey( root,subkey, ret_buf,100);

    if(retcode<0)

    {

        CParaManage::RemoveObject(pm);

        cout<<"read num is error!!!!!!!!!!!"<<endl;

        return ;

    }

    size=atoi(ret_buf);

    cout<<"size=============="<<size<<endl;

    //set<QString> m_op_user_set;

    for(int i=0;i<size;++i)

    {

        sprintf(subkey,"op_user%d",i+1);

        pm->GetKey( root,subkey, ret_buf,100);

        m_op_user_set.insert(QString(ret_buf));

        cout<<"op_user=============="<<ret_buf<<endl;

    }

#endif



    sprintf(root,"%s","AREA_ID");

    sprintf(subkey,"%s","num");

    retcode=pm->GetKey( root,subkey, ret_buf,100);

    if(retcode<0)

    {

        CParaManage::RemoveObject(pm);

        cout<<"read num is error!!!!!!!!!!!"<<endl;

        return ;

    }

    size=atoi(ret_buf);

    cout<<"size=============="<<size<<endl;



    for(int i=0;i<size;++i)

    {

        sprintf(subkey,"area_id%d",i+1);

        pm->GetKey( root,subkey, ret_buf,100);

        m_area_id.insert(atol(ret_buf));

        cout<<"area_id=============="<<ret_buf<<endl;

    }



    sprintf(root,"%s","NODE_NAME");

    sprintf(subkey,"%s","num");

    retcode=pm->GetKey( root,subkey, ret_buf,100);

    if(retcode<0)

    {

        CParaManage::RemoveObject(pm);

        cout<<"read num is error!!!!!!!!!!!"<<endl;

        return ;

    }

    size=atoi(ret_buf);

    cout<<"node size=============="<<size<<endl;



    for(int i=0;i<size;++i)

    {

        sprintf(subkey,"node_name%d",i+1);

        pm->GetKey( root,subkey, ret_buf,100);

        m_node_name_set.insert(ret_buf);

        cout<<"node_name=============="<<ret_buf<<endl;

    }



    sprintf(root,"%s","ZD_PARAM");

    sprintf(subkey,"%s","is_allow_zd_op");

    retcode=pm->GetKey( root,subkey, ret_buf,100);

    if(retcode<0)

    {

        CParaManage::RemoveObject(pm);

        cout<<"read num is error!!!!!!!!!!!"<<endl;

        return ;

    }

    m_is_allow_zd_op=atoi(ret_buf);





    sprintf(root,"%s","PARAM");

    sprintf(subkey,"%s","max_sec");

    retcode=pm->GetKey( root,subkey, ret_buf,100);

    if(retcode<0)

    {

        CParaManage::RemoveObject(pm);

        cout<<"read num is error!!!!!!!!!!!"<<endl;

        return ;

    }

    m_maxsec=atoi(ret_buf);



#if 0

    char code_num_buf[1024];

    sprintf(root,"%s","PARAM_CODE");

    sprintf(subkey,"%s","num");

    retcode=pm->GetKey( root,subkey, code_num_buf,1024);

    if(retcode<0)

    {

        CParaManage::RemoveObject(pm);

        cout<<"read num is error!!!!!!!!!!!"<<endl;

        return ;

    }

    int code_num=atoi(code_num_buf);

    for(int jj=0;jj<code_num;++jj)

    {

        char code_buf[1024];

        char xh_id_buf[1024];

        sprintf(subkey,"term_xh_id%d",jj+1);

        retcode=pm->GetKey( root,subkey, xh_id_buf,1024);

        qint64 xh_id=QString(xh_id_buf).toLongLong();



        sprintf(subkey,"code%d",jj+1);

        retcode=pm->GetKey( root,subkey, code_buf,1024);



        QStringList codeLst=QString(code_buf).split(",");

        m_xhId_codeStrLst_map[xh_id]=codeLst;

        cout<<"xhId====="<<xh_id<<" codeLst=="<<code_buf<<endl;

    }

#endif



    int ip_num=0;

    retcode=pm->GetKey("INTENT_PATH","num",ret_buf,100);

    ip_num=atoi(ret_buf);

    for(int ct=0;ct<ip_num;++ct)

    {

        string ip_str;

        string path_str;

        char tmp_szbuf[100];

        char tmp_data_buf[1024];



        sprintf(tmp_szbuf,"ip_%d",ct+1);

        pm->GetKey("INTENT_PATH",tmp_szbuf,tmp_data_buf,1024);

        ip_str=tmp_data_buf;



        sprintf(tmp_szbuf,"path_%d",ct+1);

        pm->GetKey("INTENT_PATH",tmp_szbuf,tmp_data_buf,1024);

        path_str=tmp_data_buf;



        m_ip_path_map[ip_str]=path_str;

    }

    CParaManage::RemoveObject(pm);

    return ;



}



void TermParamWindow::setCtrlEnable(bool bEnable)

{

    ui->pushBtn_Write->setEnabled(bEnable);;

    //ui->pushBtn_Act->setEnabled(bEnable);

    ui->comboBox_Area->setEditable(bEnable);

    ui->comboBox_Brand->setEnabled(bEnable);

    ui->comboBox_Line->setEnabled(bEnable);

    ui->pushBtn_CallArea->setEnabled(bEnable);

    //ui->pushBtn_AlterArea->setEnabled(bEnable);//0725

    ui->pushBtn_Read->setEnabled(bEnable);

    ui->pushBtn_Write->setEnabled(bEnable);

    //ui->pushBtn_Act->setEnabled(bEnable);

    // ui->pushBtn_Compare->setEnabled(bEnable);

    ui->lineEdit_TermName->setEnabled(bEnable);

    ui->pushBtn_SaveFile->setEnabled(bEnable);

    ui->comboBox_Type->setEnabled(bEnable);

    ui->comboBox_Status->setEnabled(bEnable);

    ui->pushBtn_Compare->setEnabled(bEnable);







}



void TermParamWindow::loadDevTree_mul(const QList_T_SubControlArea lstArea)

{

    cout<<"loadDevTree_mul"<<endl;

    QTreeWidget *pTreeWidget = ui->treeWidget_mul;

    if( pTreeWidget == NULL ) return;



    pTreeWidget->clear();

    pTreeWidget->setHeaderHidden( true );

    pTreeWidget->setStyleSheet( "QTreeView::item:hover{background-color:rgb(128,0,0,32)}" "QTreeView::item:selected{background-color:rgb(0,0,255,125)}" );



    QListDmsTerminalInfo  lstTerm;

    m_rtdbOper.GetTerminalInfo(lstTerm);



    QListDmsTermXh     lstTermXh;

    m_rtdbOper.GetDmsTermXh(lstTermXh);

    QMap<qint64,DMS_TERM_XH_STRUCT> xhId_info_map;

    for(int ct=0;ct<lstTermXh.count();++ct)

    {

        xhId_info_map[lstTermXh[ct].id]=lstTermXh[ct];

    }



    QMap<qint64,QList<qint64> > feederId_xhIdLst_map;



    m_xhId_termIdLst_map.clear();

    QMap<qint64,qint64> xhId_feederId_map;

    for(int ct=0;ct<lstTerm.count();++ct)

    {

        if(lstTerm[ct].feeder_id<=0||lstTerm[ct].term_xh<=0)

            continue;



        if(m_xhId_termIdLst_map.find(lstTerm[ct].term_xh)!=m_xhId_termIdLst_map.end())

        {

            m_xhId_termIdLst_map[lstTerm[ct].term_xh].push_back(lstTerm[ct].id);

        }

        else

        {

            QList<qint64>  termLst;

            termLst.push_back(lstTerm[ct].id);

            m_xhId_termIdLst_map[lstTerm[ct].term_xh]=termLst;

        }



        if(feederId_xhIdLst_map.find(lstTerm[ct].feeder_id)!=feederId_xhIdLst_map.end())

        {

            // if(xhId_feederId_map.find(lstTerm[ct].term_xh)!=xhId_feederId_map.end())

            //   continue;

            //if(feederId_xhIdLst_map[lstTerm[ct].term_xh].indexOf(lstTerm[ct].term_xh)!=-1)

            //  continue;

            feederId_xhIdLst_map[lstTerm[ct].feeder_id].push_back(lstTerm[ct].term_xh);

            xhId_feederId_map[lstTerm[ct].term_xh]=lstTerm[ct].feeder_id;



        }

        else

        {

            QList<qint64> xhIdLst;

            xhIdLst.push_back(lstTerm[ct].term_xh);

            feederId_xhIdLst_map[lstTerm[ct].feeder_id]=xhIdLst;

            xhId_feederId_map[lstTerm[ct].term_xh]=lstTerm[ct].feeder_id;

        }

    }





    QTreeWidgetItem *pStationNodeItem = NULL;



    T_SubControlArea t_area;

    foreach( t_area, lstArea )

    {

        QTreeWidgetItem *pAreaItem = NULL;



        long tmp_area_id=t_area.m_SubControlArea.id;

        if(m_area_id.find(tmp_area_id)==m_area_id.end()&&m_area_id.size()>0)//不配区域id  正常显示

            continue;



        bool bFlag = false;

        if( t_area.m_SubControlArea.area_type == 0 && t_area.m_SubControlArea.father_id > 0 )

        {

            SUBCONTROLAREA_STRUCT area;

            if( m_rtdbOper.GetSubControlArea( area, t_area.m_SubControlArea.father_id ) )

            {

                if( area.area_type == 3/*区域类型-省*/ )

                {

                    bFlag = true;

                }

            }

        }



        if( bFlag || t_area.m_SubControlArea.area_type == 4/*区域类型-地*/ )

        {

            if( pStationNodeItem == NULL )

            {

                pStationNodeItem = new QTreeWidgetItem( QStringList() << tr("变电站") );

                pStationNodeItem->setIcon( 0, QIcon( tr(":/Resources/h7.ico") ) );

                pStationNodeItem->setData( 0, Qt::UserRole+10, (qint64)0L );

                pTreeWidget->addTopLevelItem( pStationNodeItem );

            }



            pAreaItem = pStationNodeItem;

        }

        else

        {

            pAreaItem = new QTreeWidgetItem( QStringList() << t_area.m_SubControlArea.name );

            pAreaItem->setIcon( 0, QIcon( tr(":/Resources/x%1.bmp").arg(TABLE_NO_SUBCONTROLAREA) ) );

            pAreaItem->setData( 0, Qt::UserRole+10, t_area.m_SubControlArea.id );



#ifdef _DEBUG

            pAreaItem->setToolTip( 0, tr("区域%1").arg(D5000_KEYID(t_area.m_SubControlArea.id).ToString().c_str()) );

#endif



            pTreeWidget->addTopLevelItem( pAreaItem );

        }







        T_SubStation t_station;

        foreach( t_station, t_area.m_lstSubStation )

        {

            QTreeWidgetItem *pStationItem = new QTreeWidgetItem( QStringList() << t_station.m_SubStation.name );

            pStationItem->setIcon( 0, QIcon( tr(":/Resources/x%1.bmp").arg(TABLE_NO_SUBSTATION) ) );

            pStationItem->setData( 0, Qt::UserRole+10, t_station.m_SubStation.id );



#ifdef _DEBUG

            pStationItem->setToolTip( 0, tr("厂站%1").arg(D5000_KEYID(t_station.m_SubStation.id).ToString().c_str()) );

#endif



            pAreaItem->addChild( pStationItem );



            DMS_FEEDER_DEVICE_STRUCT t_feeder;

            foreach( t_feeder, t_station.m_lstFeeder )

            {

                QTreeWidgetItem *pFeederItem = new QTreeWidgetItem( QStringList() << t_feeder.name );

                pFeederItem->setIcon( 0, QIcon( tr(":/Resources/x%1.bmp").arg(TABLE_NO_DMS_FEEDER_DEVICE) ) );

                pFeederItem->setData( 0, Qt::UserRole+10, t_feeder.id );

                pFeederItem->setToolTip( 0, tr("馈线%1").arg(D5000_KEYID(t_feeder.id).ToString().c_str()) );



                if(feederId_xhIdLst_map.find(t_feeder.id)!=feederId_xhIdLst_map.end())

                {

                    QList<qint64> lstXhId=feederId_xhIdLst_map[t_feeder.id];



                    qint64 xh_id=0L;

                    QSet<qint64> xhId_set;

                    foreach( xh_id, lstXhId )

                    {

                        if(xhId_set.find(xh_id)!=xhId_set.end())

                            continue;

                        xhId_set.insert(xh_id);

                        if(xhId_info_map.find(xh_id)!=xhId_info_map.end())

                        {

                            DMS_TERM_XH_STRUCT xh_info=xhId_info_map[xh_id];

                            QTreeWidgetItem *pXhItem = new QTreeWidgetItem( QStringList() <<xh_info.name );

                            pXhItem->setIcon( 0, QIcon( tr(":/Resources/x13510.bmp") ) );

                            pXhItem->setData( 0, Qt::UserRole+10, xh_info.id );

                            pXhItem->setData( 0, Qt::UserRole+11, t_feeder.id );

                            pXhItem->setToolTip( 0, tr("型号:%1").arg(xh_info.id) );

                            pFeederItem->addChild(pXhItem);

                        }

                    }

                }

                pStationItem->addChild( pFeederItem );

            }

        }

    }



    connect( pTreeWidget, SIGNAL(itemDoubleClicked(QTreeWidgetItem *, int)), this, SLOT( OnTreeItemDoubleClicked_mul(QTreeWidgetItem *, int) ) );

}



void TermParamWindow::initDevTreeLocate_mul(bool b)

{

    m_TreeDevLocate_mul.Clear();



    HzToPy help;



    QTreeWidgetItemIterator it( ui->treeWidget_mul );

    while( (*it) != NULL )

    {

        QTreeWidgetItem *pItem = *it;

        if( pItem != NULL )

        {

            qint64 dev_id = pItem->data( 0, Qt::UserRole+10 ).toLongLong();



            int table_no = D5000_KEYID(dev_id).table_no;



            switch( table_no )

            {

            case TABLE_NO_SUBCONTROLAREA:

            {

                SUBCONTROLAREA_STRUCT rec;

                if( m_rtdbOper.GetSubControlArea( rec, dev_id ) )

                {

                    SMatchItem item;

                    strcpy( item.hz_code, rec.name );

                    help.TransHZIntoPY( item.hz_code, item.py_code );

                    item.userData = pItem;



                    m_TreeDevLocate_mul.m_lstCache.append( item );

                }

            }

                break;

            case TABLE_NO_SUBSTATION:

            {

                SUBSTATION_STRUCT rec;

                if( m_rtdbOper.GetSubStation( rec, dev_id ) )

                {

                    SMatchItem item;

                    strcpy( item.hz_code, rec.name );

                    help.TransHZIntoPY( item.hz_code, item.py_code );

                    item.userData = pItem;



                    m_TreeDevLocate_mul.m_lstCache.append( item );

                }

            }

                break;

            case TABLE_NO_DMS_FEEDER_DEVICE:

            {

                DMS_FEEDER_DEVICE_STRUCT rec;

                if( m_rtdbOper.GetFeederDevice( rec, dev_id ) )

                {

                    SMatchItem item;

                    strcpy( item.hz_code, rec.name );

                    help.TransHZIntoPY( item.hz_code, item.py_code );

                    item.userData = pItem;



                    m_TreeDevLocate_mul.m_lstCache.append( item );

                }

            }

                break;

            default:

                break;

            }

        }



        it++;

    }



    connect( ui->lineEdit_TreeLocate_mul, SIGNAL(textChanged(QString)), this, SLOT( OnTreeLocateTextChanged_mul(QString) ) );

    connect( ui->lineEdit_TreeLocate_mul, SIGNAL(returnPressed()), this, SLOT( OnTreeLocateReturnPressed_mul() ) );

}



void TermParamWindow::clicked_paraRightMenu_mul(const QPoint &pos)

{



    cout<<"zzzzzzz"<<endl;

    QTableView *pTableView = NULL;



    pTableView=ui->tableView_term_mul;

    if( pTableView == NULL ) return;



    int col = pTableView->horizontalHeader()->logicalIndexAt( pos );



    if( col == MUL_TERM_COL_NAME )

    {

        if( m_paraRightMenu_mul != NULL )

        {

            m_paraRightMenu_mul->exec( QCursor::pos() );

        }

    }

}

void TermParamWindow::respValue2Keys(const long resp_area,QSet<long> &child_reap_area)

{

    const int       WORD_LEN = 64;

    unsigned char   key = 0;



    child_reap_area.clear();

    TRACE("责任区:%ld",resp_area);

    for (int j = 0; j < WORD_LEN; ++j)

    {

        TRACE("j=============%d       resp_area & ((long)1 << j)===%d\n",(int)(resp_area & ((long)1 << j)));

        if ((resp_area & ((long)1 << j)) > 0)

        {

            TRACE("控制台登录责任区:%d\n",j);

            long tmp_resp_area=pow(2,j);

            child_reap_area.insert(tmp_resp_area);

        }

    }

    return;



}

void TermParamWindow::getLocalRespAreaTermId(QList<qint64> &termId_lst,qint64 resp_area,const int tab_no,const QString  table_name)

{

    long local_varaible_id;

    long key = 1;

    short field_no = 0;

    int table_no = tab_no;

    QSet<long> child_resp_area;

    respValue2Keys(resp_area,child_resp_area);

    ODB::CCommon::key_id_to_long(key,field_no,table_no,(cmnUint64*) &local_varaible_id);



    QString strSql = QString("SELECT id,resp_area FROM %1 ").arg(table_name);

    // long resp_area = 0;

    CBuffer buf_base;

    MyRtdbDataDriver rtdbOp;

    if (!rtdbOp.openSqlTable(buf_base, table_no, strSql)) {

        return ;

    }

    struct TData

    {

        long id;

        long resp_area;

    };

    TData *data_ptr=NULL;

    data_ptr=(TData*)buf_base.GetBufPtr();

    int ct=buf_base.GetLength()/sizeof(TData);

    map<long,QSet<long> > respArea_childRespArea_map;

    TRACE("resp  size======================%d\n",child_resp_area.size());

    for(int i=0;i<ct;++i)

    {

        //cout<<"id========================"<<termId_ptr[i]<<endl;





        if(resp_area>0)

        {

            QSet<long> term_resp_area;

            cout<<"term_id=="<<data_ptr[i].id;



            if(respArea_childRespArea_map.find(data_ptr[i].resp_area)==respArea_childRespArea_map.end())

            {

                respValue2Keys(data_ptr[i].resp_area,term_resp_area);

                respArea_childRespArea_map[data_ptr[i].resp_area]=term_resp_area;

            }



            QSet<long>::iterator it_set=respArea_childRespArea_map[data_ptr[i].resp_area].begin();



            for(;it_set!=respArea_childRespArea_map[data_ptr[i].resp_area].end();++it_set)

            {

                if(child_resp_area.find(*it_set)!=child_resp_area.end())

                {

                    termId_lst.push_back(data_ptr[i].id);

                }

            }



        }

        else //全系统责任区过滤

        {

            termId_lst.push_back(data_ptr[i].id);

        }

    }

    cout<<"termId_lst ct==="<<termId_lst.count()<<endl;

    //memcpy(&resp_area, szWorkBuf, sizeof(resp_area));



    //return resp_area;

}

cmnInt64  TermParamWindow::getLocalRespArea()

{

    //cmnInt64  g_resp_area;//m_RespAreaId;//0系统没有定义责任区 -1所有责任区 //主机责任区值//191,5



    long local_varaible_id;

    long key = 1;

    short field_no = 0;

    int table_no = 191;

    ODB::CCommon::key_id_to_long(key,field_no,table_no,(cmnUint64*) &local_varaible_id);



    QString strSql = QString("SELECT host_area_value FROM local_variable WHERE var_id=%1L").arg(local_varaible_id);

    long resp_area = 0;

    CBuffer buf_base;

#if 0

    MyRtdbDataDriver rtdbOp;

    if (!rtdbOp.openSqlTable(buf_base, table_no, strSql)) {

        return resp_area;

    }



    char *szWorkBuf = buf_base.GetBufPtr();

    if (buf_base.GetLength() < sizeof(resp_area)) {

        return resp_area;

    }

    memcpy(&resp_area, szWorkBuf, sizeof(resp_area));

#endif

    CTableOp tab_op;

    int ret_code=tab_op.Open(AP_PUBLIC,table_no);

    if(ret_code<0)

    {

        TRACE("Open 191 table is error!!!!!! ret_code===%d\n",ret_code);

        return -1;

    }

    ret_code=tab_op.SqlGet(strSql.toStdString().c_str(),buf_base);

    if(ret_code<0)

    {

        TRACE("SqlGet 191 table is error!!!!!! ret_code===%d  sql_str==%s\n",ret_code,strSql.toStdString().c_str());

        return -1;

    }



    char *szWorkBuf = buf_base.GetBufPtr();

    if (buf_base.GetLength() < sizeof(resp_area)) {

        return resp_area;

    }

    memcpy(&resp_area, szWorkBuf, sizeof(resp_area));

    TRACE("RESP_AREA========%ld\n",resp_area);

    return resp_area;

}

//add end

#if 0

void TermParamWindow::on_pushBtn_SaveFile_clicked()

{

    

}

#endif

void TermParamWindow::on_pushButton_ConditionQuery_clicked()

{

    load_param_file();

    int area_index=ui->comboBox_Area_3->currentIndex();

    int supply_index=ui->comboBox_Supply->currentIndex();



    if(area_index!=0)

    {

        QString area_name=ui->comboBox_Area_3->itemText(area_index);

        qDebug()<<"area_name==="<<area_name;

        for(int row=0;row<m_lstData.count();)

        {

            if(area_name.indexOf(m_lstData[row][5].toString())==-1||m_lstData[row][5].toString().isEmpty())

            {

                m_lstData.removeAt(row);

            }

            else

            {

                ++row;

            }



        }

    }



    if(supply_index!=0)

    {

        QString supply_name=ui->comboBox_Supply->itemText(supply_index);

        qDebug()<<"area_name==="<<supply_name;

        for(int row=0;row<m_lstData.count();)

        {

            if(supply_name.indexOf(m_lstData[row][6].toString())==-1||m_lstData[row][6].toString().isEmpty())

            {

                m_lstData.removeAt(row);

            }

            else

            {

                ++row;

            }

        }

    }



    if(ui->checkBox_ExecTime->checkState()==Qt::Checked)

    {

        QDateTime start_date_time=ui->dateTimeEdit_begin->dateTime();

        QDateTime end_date_time=ui->dateTimeEdit_end->dateTime();

        long    start_time=start_date_time.toTime_t();

        long    end_time=end_date_time.toTime_t();



        qDebug()<<"start_time==="<<start_time<<" end_time=="<<end_time;



        for(int row=0;row<m_lstData.count();)

        {

            long sheet_time=m_lstData[row][SHEET_COL_RECV_TIME].toInt();

            // long sheet_time=date_time.toTime_t();

            qDebug()<<"sheet time=="<<sheet_time;

            if(sheet_time>end_time||sheet_time<start_time)

            {

                m_lstData.removeAt(row);

            }

            else

            {

                ++row;

            }



        }

    }

#if 0

    QString ip_str=ui->lineEdit_IP->text();

    if(!ip_str.isEmpty())

    {

        for(int row=0;row<m_lstData.count();)

        {

            QString  sheet_ip_str=m_lstData[row][SHEET_COL_IP].toString();



            if(sheet_ip_str.indexOf(ip_str)==-1)

            {

                m_lstData.removeAt(row);

            }

            else

            {

                ++row;

            }

        }

    }

#endif

    QStandardItemModel *pModel = (QStandardItemModel*)ui->tableView->model();

    pModel->removeRows(0,pModel->rowCount());



    for(int row=0;row<m_lstData.count();++row)

    {

        for(int col=0;col<m_lstData[row].count();++col)

        {

            //QStandardItem *value_Item = new QStandardItem( tr("%1").arg(m_lstData[row][col].toString()) );

            QStandardItem *value_Item ;



            if(col==SHEET_COL_EXEC_STATUS)

            {

                int status=m_lstData[row][col].toInt();

                value_Item= new QStandardItem( tr("%1").arg(getFailResult(status)) );

            }

            else

            {

                if(col==SHEET_COL_RECV_TIME||col==SHEET_COL_EXEC_TIME)

                {

                    QDateTime datetime;

                    datetime.setTime_t(m_lstData[row][col].toInt());

                    QString datetime_str=datetime.toString("yyyy-MM-dd hh:mm:ss");

                    if(col==SHEET_COL_EXEC_TIME)

                    {

                        if(m_lstData[row][SHEET_COL_EXEC_STATUS].toInt()==0)

                            value_Item= new QStandardItem();

                        else

                            value_Item= new QStandardItem( tr("%1").arg(datetime_str) );

                    }

                    else

                    {

                        value_Item= new QStandardItem( tr("%1").arg(datetime_str) );

                    }

                }

                else

                {

                    value_Item= new QStandardItem( tr("%1").arg(m_lstData[row][col].toString()) );

                }





                //value_Item= new QStandardItem( tr("%1").arg(m_lstData[row][col].toString()) );

            }

            value_Item->setEditable( false );

            pModel->setItem( row, col, value_Item );



        }

    }



}



void TermParamWindow::on_pushButton_AllQuery_clicked()

{

    load_param_file();

    QStandardItemModel *pModel = (QStandardItemModel*)ui->tableView->model();

    pModel->removeRows(0,pModel->rowCount());



    for(int row=0;row<m_lstData.count();++row)

    {

        for(int col=0;col<m_lstData[row].count();++col)

        {

            //QStandardItem *value_Item = new QStandardItem( tr("%1").arg(m_lstData[row][col].toString()) );

            QStandardItem *value_Item ;



            if(col==SHEET_COL_EXEC_STATUS)

            {

                int status=m_lstData[row][col].toInt();

                value_Item= new QStandardItem( tr("%1").arg(getFailResult(status)) );

            }

            else

            {

                if(col==SHEET_COL_RECV_TIME||col==SHEET_COL_EXEC_TIME)

                {

                    QDateTime datetime;

                    datetime.setTime_t(m_lstData[row][col].toInt());

                    QString datetime_str=datetime.toString("yyyy-MM-dd hh:mm:ss");

                    if(col==SHEET_COL_EXEC_TIME)

                    {

                        if(m_lstData[row][SHEET_COL_EXEC_STATUS].toInt()==0)

                            value_Item= new QStandardItem();

                        else

                            value_Item= new QStandardItem( tr("%1").arg(datetime_str) );

                    }

                    else

                    {

                        value_Item= new QStandardItem( tr("%1").arg(datetime_str) );

                    }

                }

                else

                {

                    value_Item= new QStandardItem( tr("%1").arg(m_lstData[row][col].toString()) );

                }



                //value_Item= new QStandardItem( tr("%1").arg(m_lstData[row][col].toString()) );

            }

            value_Item->setEditable( false );

            pModel->setItem( row, col, value_Item );



        }

    }

}



void TermParamWindow::on_pushButton_ExecSheet_clicked()

{

    m_oper_timeout_slice    = 45;   //操作超时间隔(秒)

    m_sheetId_paramLst_map.clear();

    m_cur_sheet_id_str="";

    QItemSelectionModel *select_model=ui->tableView->selectionModel();

    QModelIndexList index_lst=select_model->selectedRows();

    if(index_lst.count()<=0)

    {

        QMessageBox::warning(this,tr("提示:"),tr("未选中工单"));

        return;

    }

#if 0

    if(index_lst.count()!=1)

    {

        QMessageBox::warning(this,tr("提示:"),tr("一次只允许执行一个工单"));

        return;

    }

#endif

    //获取表单参数信息

    QStandardItemModel *pModel=(QStandardItemModel *)ui->tableView->model();

    map<long,QString> termId_fileName_map;



    //获取工单信息

    for(int row=0;row<index_lst.count();++row)

    {

        QModelIndex index=index_lst.at(row);



        QString sheet_id;

        sheet_id=pModel->item(index.row(),SHEET_COL_TASK_ID)->text();

        QString sheet_name=pModel->item(index.row(),SHEET_COL_TASK_NAME)->text();

        //file_name.replace("待执行定值","执行定值结果");

        // file_name.replace("未","已");

        QList<QVariantList> lstData;





        //根据工单获取该工单参数

        getParamContentBySheetId(sheet_id,lstData);

        //m_cur_sheet_id_str=sheet_id;

        if(lstData.count()<=0)

        {

            QMessageBox::warning(this,tr("提示:"),tr("该工单未读到相关参数"));

            return;

        }

        QList<SheetInfo>  sheetInfoLst;

        bool bExec=false;

        for(int ct=0;ct<lstData.count();++ct)

        {

            SheetInfo sheetInfo;

            sheetInfo.term_id=lstData[ct][SHEET_PARAM_COL_TERM_ID].toLongLong();

            sheetInfo.xh_id=lstData[ct][SHEET_PARAM_COL_TERM_TYPE].toLongLong();

            sheetInfo.code=QString(lstData[ct][SHEET_PARAM_COL_PARAM_CODE].toString()).toInt(NULL,16);

            sheetInfo.status=lstData[ct][SHEET_PARAM_COL_EXEC_STATUS].toInt();

            sheetInfo.line_no=lstData[ct][SHEET_PARAM_COL_LINE_NO].toInt();

            sheetInfo.area_no=lstData[ct][SHEET_PARAM_COL_AREA_NO].toInt();

            sheetInfo.value=lstData[ct][SHEET_PARAM_COL_PARAM_VALUE].toString();

            sheetInfo.gis_id_str=lstData[ct][SHEET_PARAM_COL_GIS_ID].toString();

            sheetInfo.ip_str=lstData[ct][SHEET_PARAM_COL_IP].toString();

            sheetInfo.term_flag=lstData[ct][SHEET_PARAM_COL_DEV_FLAG].toInt();

            sheetInfo.sheet_name_str=sheet_name;

            sheetInfo.sheet_id_str=sheet_id;

            if(sheetInfo.status!=SHEET_STATUS_DEFAULT/*SHEET_STATUS_SUCCESSED*/)

            {

                bExec=true;

            }

            sheetInfoLst.push_back(sheetInfo);

            // termId_fileName_map[sheetInfo.term_id]=file_name;

            qDebug()<<"code====="<<sheetInfo.code;



        }

        if(bExec)

        {

            QMessageBox::warning(this,tr("提示:"),tr("该工单已执行完成 %1!!!!!").arg(sheet_id));

            continue;

        }

        m_sheetId_paramLst_map[sheet_id]=sheetInfoLst;

        //m_sheetId_paramLst_map["1"]=sheetInfoLst;

        // m_sheetId_paramLst_map["2"]=sheetInfoLst;



    }

    //





    //用户权限验证

#ifdef USER_PRIV_CHECK

    if(!delJudgePriv(PRIV_TERM_PARAM_OP,g_strLoginUserName))

    {

        return;

    }

    setWidText(g_strLoginUserName);

    if(QMessageBox::warning(this,tr("提示:"),tr("执行该操作需要监护用户登录进行允许操作,是否继续?"),QMessageBox::Yes|QMessageBox::No)==QMessageBox::Yes)

    {

        int expire_time = 0;

        QDateTime dtStartExeTime;

        QString userName;

        if (doLogin(userName, expire_time, dtStartExeTime)){





            if(!delJudgePriv(PRIV_TERM_PARAM_CUSTODY,userName))

            {

                return;

            }

            m_strCustodyName=userName;

        }

        else

        {

            return;

        }

    }

    else

    {

        return;

    }

    setWidText(g_strLoginUserName);

#else

    QString loginName=g_strLoginUserName;

    if(!delJudgePriv(PRIV_TERM_PARAM_OP,loginName,true))

    {

        return;

    }

    g_strLoginUserName=loginName;

    setWidText(g_strLoginUserName);



    QString userName=m_strCustodyName;

    if(!delJudgePriv(PRIV_TERM_PARAM_CUSTODY,userName))

    {

        return;

    }

    m_strCustodyName=userName;

    setWidText(g_strLoginUserName);

#endif



    //开始执行工单

    bool bFinised=false;

    QMap<QString,QList<SheetInfo> >::iterator it=m_sheetId_paramLst_map.begin();

    for(;it!=m_sheetId_paramLst_map.end();++it)

    {

        QList<SheetInfo> sheetLst=it.value();



        m_cur_sheet_id_str="";

        m_cur_sheet_id_str=sheetLst[0].sheet_id_str;

        cout<<CLogManager::Instance()->WriteLineT("工单执行:");

        qDebug()<<"term_id==="<<sheetLst.size()<<" size=="<<m_sheetId_paramLst_map.size();

        sprintf(g_log_buf,"用户:%s对单号=%s工单进行执行工单操作",g_strLoginUserName.toStdString().c_str(),it.key().toStdString().c_str());

        cout<<CLogManager::Instance()->WriteLineT(g_log_buf);



        if(sheetLst[0].term_id<=0)

        {

            QMessageBox::warning( this, tr(" 单号= %1").arg(it.key()), tr("查询配网终端信息失败...") );

            // m_sheetId_paramLst_map.erase(it);

            continue;

        }



        if(sheetLst[0].xh_id<=0)

        {

            //m_sheetId_paramLst_map.erase(it);

            QMessageBox::warning( this, tr("单号=%1 %2").arg(it.key()),tr("未设置有效的终端型号，请检查..."));

            continue;

        }



        m_sheet_line_no=0;

        m_sheet_term_dev_id=0L;

        m_sheet_term_xh_id=0L;

        m_sheet_term_param_rec.clear();

        //获取配网参数字典表

        QListDmsTermParamDict  term_param_rec;

        if( ! m_rtdbOper.GetTermParam( sheetLst[0].xh_id, m_sheet_term_param_rec ) )

        {

            QMessageBox::warning( this, tr("id = %1").arg(sheetLst[0].xh_id), tr("装载终端参数字典失败...") );

            continue;

        }//

        cout<<"m_sheet_term_param_rec ct===="<<m_sheet_term_param_rec.count()<<"  code==="<<m_xhId_content_map.size()<<endl;

        term_param_rec=m_sheet_term_param_rec;

        m_sheet_term_xh_id=sheetLst[0].xh_id;

        //m_sheet_term_dev_id=sheetLst[0].term_id;

        //m_sheet_area_no=sheetLst[0].area_no;

#if 0   //定值区不再从配置里读取

        TConfContent content_data;

        QString area_code_str=content_data.code.trimmed();



        if(m_xhId_content_map.find(m_sheet_term_xh_id)!=m_xhId_content_map.end())

        {

            content_data=m_xhId_content_map[m_sheet_term_xh_id];

            area_code_str=content_data.code.trimmed();

        }

        else

        {



            QMessageBox::warning( this, tr("召唤参数:"), tr("配置里未找到型号:%1对应的定值区参数配置.....").arg(sheetLst[0].xh_id) );

            break;

        }

#endif

        //删除不是工单里的参数

        for( int jj=0;jj<term_param_rec.count();)

        {



            bool bFind=false;

            for(int ii=0;ii<sheetLst.count();++ii)

            {

                //不是本体终端或设备的过滤

                if(sheetLst[ii].term_flag==1)

                {

                    if(term_param_rec[jj].dev_flag!=0)

                    {

                        bFind=false;

                        continue;

                    }

                }

                else

                {



                    if(term_param_rec[jj].dev_flag!=1)

                    {

                        bFind=false;

                        continue;

                    }



                }



                //南网定值区 当做参数去召   国网需要确认

                if(sheetLst[ii].code==QString(term_param_rec[jj].param_code).toInt(NULL,16))

                {

                    ++jj;

                    bFind=true;

                    break;

                }

                else

                {

                    bFind=false;

                    continue;

                }





            }

            if(!bFind)

            {

                term_param_rec.removeAt(jj);

            }

        }

        //

        if(term_param_rec.count()!=sheetLst.count())

        {

            QMessageBox::warning( this, tr("id = %1").arg(sheetLst[0].xh_id), tr("终端参数字典信息与工单参数列表不一致 dict=%1 sheet=%2...").arg(term_param_rec.count()).arg(sheetLst.count()) );

            continue;

        }







        //召唤参数

        T_Terminal_Para call_para,modify_para,para3,check_para;

        QString cur_content_str;

        int termType=m_termId_termType_map[sheetLst[0].term_id];

        QString term_type_str=m_actNo_menuName_map[termType];

        //QMap<qint32,QString> code_name_map;

        m_code_name_map.clear();

        m_code_value_map.clear();

        m_ifModifyCode_paramDict_map.clear();



        bool bIfModify=false;

        bool bAreaCodeAdd=false;

        QString ifModify_str="";

        Terminal_Para_Info pi_area={0};

        CLogManager::Instance()->WriteLineT("工单执行参数:");

        for(int ct=0;ct<sheetLst.count();++ct)

        {

            DMS_TERM_PARAM_DICT_STRUCT param_rec;



            foreach( param_rec, term_param_rec )

            {



                if(sheetLst[ct].code==QString(param_rec.param_code).toInt(NULL,16))

                {



                    Terminal_Para_Info pi = { 0 };

                    pi.comm_fac_id  = sheetLst[ct].term_id;

                    pi.line_no      = sheetLst[0].line_no ;

                    pi.serial_num   = sheetLst[0].area_no;

                    pi.para_type    = param_rec.param_type;

                    pi.value_type   = param_rec.data_type;

                    pi.data_type    = QString(param_rec.param_code).toInt(NULL,16);

                    pi.result       = 104;





                    para3.terminal_para_seq.push_back( pi );

                    // qDebug()<<"type==="<<pi.para_type<<"vale type=="<<pi.value_type<<"value==="<<pi.value_f<<"line_no=="<<sheetLst[0].line_no<<" area_no="<<sheetLst[0].area_no<<"code====="<<QString(param_rec.param_code).toInt(NULL,16)<<"  cose2==="<<param_rec.param_code<<"  term_id==="<<sheetLst[ct].term_id<<" line=="<<sheetLst[0].line_no;

                    //cur_content_str+=QString("%1@%2@%3\n").arg("执行失败").arg(QString(param_rec.param_code).toInt(NULL,16)).arg(param_rec.name);//.arg(getDataTypeName(param_rec.param_type,param_rec.data_type)).arg(param_rec.step_len).arg(param_rec.param_range).arg(sheetLst[ct].value).arg(param_rec.dev_flag);



                    sprintf(g_log_buf,"comm_fac_id=%ld line_no=%d area_no=%d para_type=%d value_type=%d data_type=%d result=%d if_modify=%d",

                            pi.comm_fac_id,pi.line_no,pi.serial_num,pi.para_type,pi.value_type,pi.data_type,pi.result,(int)param_rec.if_modify);

                    cout<<CLogManager::Instance()->WriteLineT(g_log_buf);

                    if((int)param_rec.if_modify==0)

                    {



                        ifModify_str+=QString("%1@%2@%3\n").arg("不可设置").arg(QString(param_rec.param_code).toInt(NULL,16)).arg(param_rec.name);//.arg(getDataTypeName(param_rec.param_type,param_rec.data_type)).arg(param_rec.step_len).arg(param_rec.param_range).arg(sheetLst[ct].value).arg(param_rec.dev_flag);

                        m_ifModifyCode_paramDict_map[QString(param_rec.param_code).toInt(NULL,16)]=param_rec;

                        break;

                    }



                    call_para.terminal_para_seq.push_back( pi );

                    check_para.terminal_para_seq.push_back( pi );

                    string_to_param_value( sheetLst[ct].value, pi );

                    modify_para.terminal_para_seq.push_back( pi );//用于下装的参数

                    m_code_name_map[QString(param_rec.param_code).toInt(NULL,16)]=param_rec.name;

                    m_code_value_map[QString(param_rec.param_code).toInt(NULL,16)]=sheetLst[ct].value;

                    break;



                }



            }

        }





        cout<<"call_para size==="<<call_para.terminal_para_seq.size()<<endl;;

        cout<<"size==========="<<term_param_rec.size()<<endl;

        m_sheet_line_no=sheetLst[0].term_flag==1?sheetLst[0].line_no:(TERM_PARAM_LINE_NO_BASE+sheetLst[0].line_no);

        m_sheet_area_no=sheetLst[0].area_no;

        m_sheet_term_dev_id=sheetLst[0].term_id;

        //m_curSheet_param.terminal_para_seq.clear();

        QString file_name;

        QString file_path_str;//=QString(m_exec_file_path_str.c_str())+file_name+QString(".pwdz");;

        QString content_str;

        QDateTime datetime=QDateTime::currentDateTime();

        QString datetime_str=datetime.toString("yyyyMMddhhmmss");

#if 0

        //发告警

        TWarnData data;

        DMS_TERMINAL_INFO_STRUCT struDmsTerminalInfo;

        m_rtdbOper.ReadTerminalInfo(struDmsTerminalInfo,sheetLst[0].term_id);

        //data.combined_id=struDmsTerminalInfo.feeder_id;

        data.term_id=struDmsTerminalInfo.id;

        data.warn_type=MENU_OPT_DEV_STAT_SHEET_EXEC;

        data.sheet_id_str=sheetLst[0].sheet_name_str.toStdString();

        sendWarn(data);

#endif

        if(m_ifModifyCode_paramDict_map.count()>0)

        {

            QMessageBox::warning(this,tr("提示:"),tr("存在不可设置参数,请修改参数可设置再继续!!"));

            //如果继续执行 就退出

            // if(call_para.terminal_para_seq.size()<=0)

            {

                QMap<qint32,DMS_TERM_PARAM_DICT_STRUCT>::iterator code_iterator=m_ifModifyCode_paramDict_map.begin();

                for(;code_iterator!=m_ifModifyCode_paramDict_map.end();++code_iterator)

                {

                    DMS_TERM_PARAM_DICT_STRUCT  tmp_param_rec=code_iterator.value();

                    QString code_str=QString::number(code_iterator.key(),16);

                    QString code_10_str=QString::number(code_iterator.key());

                    updateParamStatus(m_cur_sheet_id_str,-8,code_str);

                    cout<<CLogManager::Instance()->WriteLineT(g_log_buf);



                }



                updateSheetStatus(m_cur_sheet_id_str,-8);



                content_str=QString("已@执行失败@%1@PMSID%2@IP%3@组号%4@定值区号%5@时间%6@是否本体终端%7@%8\n").arg(sheetLst[0].sheet_name_str)

                        .arg(sheetLst[0].gis_id_str).arg(sheetLst[0].ip_str).arg(sheetLst[0].line_no).arg(sheetLst[0].area_no).arg(datetime_str).arg(sheetLst[0].term_flag).arg(getFailResult(-8));



                file_name=QString("已@执行失败@%1@PMSID%2@IP%3@组号%4@时间%5@是否本体终端%6@%7.pwdz").arg(sheetLst[0].sheet_name_str)

                        .arg(sheetLst[0].gis_id_str).arg(sheetLst[0].ip_str).arg(sheetLst[0].line_no).arg(datetime_str).arg(sheetLst[0].term_flag).arg(getFailResult(-8));



                file_path_str=QString(m_exec_file_path_str.c_str())+file_name;

                content_str+=ifModify_str;

                writeFile(file_path_str,content_str);

                continue;

            }

        }



        //召唤定值区



        int ret_code=call_area_thread(sheetLst[0],call_para);

        if(ret_code!=1)

        {

            content_str=QString("已@执行失败@%1@PMSID%2@IP%3@组号%4@定值区号%5@时间%6@是否本体终端%7@%8\n").arg(sheetLst[0].sheet_name_str)

                    .arg(sheetLst[0].gis_id_str).arg(sheetLst[0].ip_str).arg(sheetLst[0].line_no).arg(sheetLst[0].area_no).arg(datetime_str).arg(sheetLst[0].term_flag).arg(getFailResult(ret_code));

            file_name=QString("已@执行失败@%1@PMSID%2@IP%3@组号%4@定值区号%5@时间%6@是否本体终端%7@%8.pwdz").arg(sheetLst[0].sheet_name_str)

                    .arg(sheetLst[0].gis_id_str).arg(sheetLst[0].ip_str).arg(sheetLst[0].line_no).arg(sheetLst[0].area_no).arg(datetime_str).arg(sheetLst[0].term_flag).arg(getFailResult(ret_code));



            file_path_str=QString(m_exec_file_path_str.c_str())+file_name;

            content_str +=cur_content_str;

            writeFile(file_path_str,content_str);

            continue;



        }

        //召唤参数

        ret_code=call_param_thread(call_para,modify_para,sheetLst[0]);

        if(ret_code!=1)

        {

            content_str=QString("已@执行失败@%1@PMSID%2@IP%3@组号%4@定值区号%5@时间%6@是否本体终端%7@%8\n").arg(sheetLst[0].sheet_name_str)

                    .arg(sheetLst[0].gis_id_str).arg(sheetLst[0].ip_str).arg(sheetLst[0].line_no).arg(sheetLst[0].area_no).arg(datetime_str).arg(sheetLst[0].term_flag).arg(getFailResult(ret_code));

            file_name=QString("已@执行失败@%1@PMSID%2@IP%3@组号%4@定值区号%5@时间%6@是否本体终端%7@%8.pwdz").arg(sheetLst[0].sheet_name_str)

                    .arg(sheetLst[0].gis_id_str).arg(sheetLst[0].ip_str).arg(sheetLst[0].line_no).arg(sheetLst[0].area_no).arg(datetime_str).arg(sheetLst[0].term_flag).arg(getFailResult(ret_code));



            file_path_str=QString(m_exec_file_path_str.c_str())+file_name;

            content_str +=cur_content_str;

            writeFile(file_path_str,content_str);

            continue;



        }

        //下装参数

        ret_code=download_param_thread(modify_para,sheetLst[0]);

        if(ret_code!=1)

        {

            content_str=QString("已@执行失败@%1@PMSID%2@IP%3@组号%4@定值区号%5@时间%6@是否本体终端%7@%8\n").arg(sheetLst[0].sheet_name_str)

                    .arg(sheetLst[0].gis_id_str).arg(sheetLst[0].ip_str).arg(sheetLst[0].line_no).arg(sheetLst[0].area_no).arg(datetime_str).arg(sheetLst[0].term_flag).arg(getFailResult(ret_code));

            file_name=QString("已@执行失败@%1@PMSID%2@IP%3@组号%4@定值区号%5@时间%6@是否本体终端%7@%8.pwdz").arg(sheetLst[0].sheet_name_str)

                    .arg(sheetLst[0].gis_id_str).arg(sheetLst[0].ip_str).arg(sheetLst[0].line_no).arg(sheetLst[0].area_no).arg(datetime_str).arg(sheetLst[0].term_flag).arg(getFailResult(ret_code));



            file_path_str=QString(m_exec_file_path_str.c_str())+file_name;

            //content_str +=cur_content_str;

            writeFile(file_path_str,content_str);

            continue;

        }

        //激活参数

        ret_code=act_param_thread(modify_para,sheetLst[0]);

        if(ret_code!=1)

        {

            content_str=QString("已@执行失败@%1@PMSID%2@IP%3@组号%4@定值区号%5@时间%6@是否本体终端%7@%8\n").arg(sheetLst[0].sheet_name_str)

                    .arg(sheetLst[0].gis_id_str).arg(sheetLst[0].ip_str).arg(sheetLst[0].line_no).arg(sheetLst[0].area_no).arg(datetime_str).arg(sheetLst[0].term_flag).arg(getFailResult(ret_code));

            file_name=QString("已@执行失败@%1@PMSID%2@IP%3@组号%4@定值区号%5@时间%6@是否本体终端%7@%8.pwdz").arg(sheetLst[0].sheet_name_str)

                    .arg(sheetLst[0].gis_id_str).arg(sheetLst[0].ip_str).arg(sheetLst[0].line_no).arg(sheetLst[0].area_no).arg(datetime_str).arg(sheetLst[0].term_flag).arg(getFailResult(ret_code));



            file_path_str=QString(m_exec_file_path_str.c_str())+file_name;

            // content_str +=cur_content_str;

            writeFile(file_path_str,content_str);

            continue;

        }

        m_check_content_str=tr("");

        ret_code=check_param_thread(check_para,modify_para,sheetLst[0]);

        if(ret_code!=1)

        {

            content_str=QString("已@执行失败@%1@PMSID%2@IP%3@组号%4@定值区号%5@时间%6@是否本体终端%7@%8\n").arg(sheetLst[0].sheet_name_str)

                    .arg(sheetLst[0].gis_id_str).arg(sheetLst[0].ip_str).arg(sheetLst[0].line_no).arg(sheetLst[0].area_no).arg(datetime_str).arg(sheetLst[0].term_flag).arg(getFailResult(ret_code));

            file_name=QString("已@执行失败@%1@PMSID%2@IP%3@组号%4@定值区号%5@时间%6@是否本体终端%7@%8.pwdz").arg(sheetLst[0].sheet_name_str)

                    .arg(sheetLst[0].gis_id_str).arg(sheetLst[0].ip_str).arg(sheetLst[0].line_no).arg(sheetLst[0].area_no).arg(datetime_str).arg(sheetLst[0].term_flag).arg(getFailResult(ret_code));

            content_str+=m_check_content_str;

            file_path_str=QString(m_exec_file_path_str.c_str())+file_name;

            //content_str +=cur_content_str;

            writeFile(file_path_str,content_str);

            continue;

        }

        content_str=QString("已@执行成功@%1@PMSID%2@IP%3@组号%4@定值区号%5@时间%6@是否本体终端%7@%8\n").arg(sheetLst[0].sheet_name_str)

                .arg(sheetLst[0].gis_id_str).arg(sheetLst[0].ip_str).arg(sheetLst[0].line_no).arg(sheetLst[0].area_no).arg(datetime_str).arg(sheetLst[0].term_flag).arg(getFailResult(ret_code));

        file_name=QString("已@执行成功@%1@PMSID%2@IP%3@组号%4@定值区号%5@时间%6@是否本体终端%7@%8.pwdz").arg(sheetLst[0].sheet_name_str)

                .arg(sheetLst[0].gis_id_str).arg(sheetLst[0].ip_str).arg(sheetLst[0].line_no).arg(sheetLst[0].area_no).arg(datetime_str).arg(sheetLst[0].term_flag).arg(getFailResult(ret_code));

        content_str+=m_check_content_str;

        file_path_str=QString(m_exec_file_path_str.c_str())+file_name;

        //content_str +=cur_content_str;

        writeFile(file_path_str,content_str);

    }

    //





    QMessageBox::warning(this,tr("提示:"),tr("工单执行操作已完成 请点击全部查询按钮查看!!!!!"));



}

QString TermParamWindow::getFailResult(qint32 code)//返回状态和工单显示状态都放到这个函数里

{

    switch(code)

    {

    case 0:

        return tr("未执行");

    case 1:

        return tr("执行成功");

    case -1:

    case -2:

        return tr("消息总线注册失败");

    case -3:

        return tr("消息请求发送失败");

    case -4:

        return tr("不支持定值区参数与其它参数一起下装");

    case -5:

        return tr("收到的消息长度小于0");

    case -6:

        return tr("操作超时");

    case -7:

        return tr("操作中止");



    case -8:

        return tr("存在不可设置参数");

    case -9:

        return tr("返校失败");

    case -10:

        return tr("激活失败");

    case -11:

        return tr("校验失败");

    case -12:

        return tr("未找到定值区参数配置");

    case -13:

        return tr("定值区不一致");

    case -15:

        return tr("数据类型转换失败");

default:



        return tr("未识别返回状态");

        break;

    }

}

int TermParamWindow::call_param_thread(T_Terminal_Para &call_para,T_Terminal_Para &modify_para,const SheetInfo & sheetInfo)

{

    if(m_pThread!=NULL)

    {

        delete m_pThread;

        m_pThread=NULL;

    }

    //激活参数

    short serv          = CH_DMS_TERMINAL_DATA;

    short event         = MT_DMS_TERM_PARA_CALL;;

    m_download_reply_event   = MT_DMS_TERM_PARA_REPLY;

    m_sheet_result=0;

    m_pThread=new MsgBus_RW_Thread();

    m_pThread->init( sheetInfo.term_id, m_oper_timeout_slice );



    

    m_pThread->setRunFlag(0);

    if( m_pThread->init_msg( serv, event, m_download_reply_event, call_para ) != 0 )

    {



        updateSheetStatus(m_cur_sheet_id_str,-1);

        for( int i = 0; i < call_para.terminal_para_seq.size(); i++ )

        {

            Terminal_Para_Info info = call_para.terminal_para_seq[i];

            QString code_str=QString::number(info.data_type,16);

            updateParamStatus(m_cur_sheet_id_str,-1,code_str);



        }

        sprintf(g_log_buf,"工单召唤参数 初始化失败...");

        CLogManager::Instance()->WriteLineT(g_log_buf);

        QMessageBox::warning( this, tr("召唤参数:"), tr("召唤参数 初始化失败...") );

        //初始化失败

        return -1;

    }



    //       connect( m_pThread, SIGNAL( finished() ), m_pThread, SLOT( deleteLater() ) );

    // connect( m_pThread, SIGNAL( Post_SheetParaCall(short,QByteArray,qint32)), this, SLOT( On_MsgBus_SheetParaCall(short,QByteArray,qint32)) );

    m_pThread->setSheetFlag(true);

    m_pThread->start();

    cout<<"zzzzzzzzzzzzzz"<<endl;

    sheet_exec_dlg_start();

    T_Terminal_Para reply_para;

    T_Terminal_Op reply_op;



    while(1)

    {



        usleep(5000);

        cout<<"while!!!!!!!!"<<endl;

        m_sheet_result=m_pThread->getParamReply(reply_para);

        if(m_sheet_result==0)

            continue;

        else

            break;

    }

    if(m_pThread!=NULL)

    {

        delete m_pThread;

        m_pThread=NULL;

    }

    int ret=m_sheet_result;

    //reply_para=m_reply_para;

    CLogManager::Instance()->WriteLineT("工单执行召唤参数返回结果:");

    for( int i = 0; i < reply_para.terminal_para_seq.size(); i++ )

    {

        Terminal_Para_Info pi = reply_para.terminal_para_seq[i];

        sprintf(g_log_buf,"comm_fac_id=%ld line_no=%d area_no=%d para_type=%d value_type=%d data_type=%d value_f=%f value_i=%d result=%d ",

                pi.comm_fac_id,pi.line_no,pi.serial_num,pi.para_type,pi.value_type,pi.data_type,pi.value_f,pi.value_i,pi.result);

        CLogManager::Instance()->WriteLineT(g_log_buf);

        //  CLogManager::Instance()->WriteLineT(QString::fromUtf8(pi.value_s).toStdString());

    }

    if(ret==-1||ret==-2)

    {



        updateSheetStatus(m_cur_sheet_id_str,-1);

        for( int i = 0; i < call_para.terminal_para_seq.size(); i++ )

        {

            Terminal_Para_Info info = call_para.terminal_para_seq[i];

            QString code_str=QString::number(info.data_type,16);

            updateParamStatus(m_cur_sheet_id_str,-1,code_str);



        }

        sprintf(g_log_buf,"工单召唤参数 消息总线注册失败...");

        CLogManager::Instance()->WriteLineT(g_log_buf);

        QMessageBox::warning( this, tr("召唤参数:"), tr("召唤参数 消息总线注册失败...") );



    }

    else if(ret==-3)

    {



        updateSheetStatus(m_cur_sheet_id_str,-3);

        for( int i = 0; i < call_para.terminal_para_seq.size(); i++ )

        {

            Terminal_Para_Info info = call_para.terminal_para_seq[i];

            QString code_str=QString::number(info.data_type,16);

            updateParamStatus(m_cur_sheet_id_str,-3,code_str);



        }

        sprintf(g_log_buf,"工单召唤参数 消息请求发送失败...");

        CLogManager::Instance()->WriteLineT(g_log_buf);

        QMessageBox::warning( this, tr("召唤参数:"), tr("召唤参数 消息请求发送失败...") );

    }

    /* else if(ret==-4)

    {



    }*/

    else if(ret==-5)

    {



        updateSheetStatus(m_cur_sheet_id_str,-5);

        for( int i = 0; i < call_para.terminal_para_seq.size(); i++ )

        {

            Terminal_Para_Info info = call_para.terminal_para_seq[i];

            QString code_str=QString::number(info.data_type,16);

            updateParamStatus(m_cur_sheet_id_str,-5,code_str);



        }

        sprintf(g_log_buf,"工单召唤参数 收到的消息长度小于0...");

        CLogManager::Instance()->WriteLineT(g_log_buf);

        QMessageBox::warning( this, tr("召唤参数:"), tr("召唤参数 收到的消息长度小于0...") );

    }

    else if(ret==-6)//超时

    {



        updateSheetStatus(m_cur_sheet_id_str,-6);

        for( int i = 0; i < call_para.terminal_para_seq.size(); i++ )

        {

            Terminal_Para_Info info = call_para.terminal_para_seq[i];

            QString code_str=QString::number(info.data_type,16);

            updateParamStatus(m_cur_sheet_id_str,-6,code_str);



        }

        sprintf(g_log_buf,"工单召唤参数 超时...");

        CLogManager::Instance()->WriteLineT(g_log_buf);

        QMessageBox::warning( this, tr("召唤参数:"), tr("召唤参数 超时...") );





    }

    else if(ret==-7)//中止

    {



        updateSheetStatus(m_cur_sheet_id_str,-7);

        for( int i = 0; i < call_para.terminal_para_seq.size(); i++ )

        {

            Terminal_Para_Info info = call_para.terminal_para_seq[i];

            QString code_str=QString::number(info.data_type,16);

            updateParamStatus(m_cur_sheet_id_str,-7,code_str);



        }

        sprintf(g_log_buf,"工单召唤参数 中止...");

        CLogManager::Instance()->WriteLineT(g_log_buf);

        QMessageBox::warning( this, tr("召唤参数:"), tr("召唤参数 中止...") );

    }

    else//ret==1

    {

        update_progress_dlg(m_oper_timeout_slice);//接收完成

        SheetStopProgressStep();





        update_sheet_reply_para(reply_para);

        DMS_TERMINAL_INFO_STRUCT struDmsTerminalInfo;

        m_rtdbOper.ReadTerminalInfo(struDmsTerminalInfo,sheetInfo.term_id);

        for( int i = 0; i < modify_para.terminal_para_seq.size(); i++ )

        {



            Terminal_Para_Info info = modify_para.terminal_para_seq[i];

            bool bParamOk=false;



            QString code_str=QString::number(info.data_type,16);

            for( int j = 0; j < reply_para.terminal_para_seq.size(); j++ )

            {

                Terminal_Para_Info curinfo = reply_para.terminal_para_seq[j];

                if(curinfo.data_type==info.data_type)//请求参数地址和返回参数地址相同

                {

                    qDebug()<<"cdoe================"<<info.data_type<<"   data_type============"<<modify_para.terminal_para_seq[i].value_type<<" data_2======"<<curinfo.value_type;



                    //para.terminal_para_seq[i].para_type=curinfo.para_type;

                    curinfo.para_type=modify_para.terminal_para_seq[i].para_type;

                    QString  cur_value_str=fixed_param_value_to_string(curinfo);

                    modify_para.terminal_para_seq[i].value_type=curinfo.value_type;



                    QString info_value_str=m_code_value_map[info.data_type];

                    if(!fixed_string_to_param_value( info_value_str, modify_para.terminal_para_seq[i]))

                    {

                        updateSheetStatus(m_cur_sheet_id_str,-15);

                        updateParamStatus(m_cur_sheet_id_str,-15,code_str);



                        sprintf(g_log_buf,"工单下装前 数据类型转换失败....");

                        CLogManager::Instance()->WriteLineT(g_log_buf);



                        QMessageBox::warning( this, tr("召唤参数:"), tr("工单下装前 数据类型转换失败...") );

                        return -15;

                    }

                    QString op_str=QString("下装参数 参数名称:%1 信息体:%2 原值:%3 修改值:%4").arg(m_code_name_map[info.data_type]).arg(info.data_type).arg(cur_value_str).arg(info_value_str);

                    cout<<CLogManager::Instance()->WriteLineT(g_log_buf);

#if 1

                    //curinfo.para_type=info.para_type;

                    //string_to_param_value( cur_value_str, para.terminal_para_seq[i]);

                    //发告警

                    TWarnData data;

                    //DMS_TERMINAL_INFO_STRUCT struDmsTerminalInfo;

                    //  m_rtdbOper.ReadTerminalInfo(struDmsTerminalInfo,term_dev_id);

                    //data.combined_id=struDmsTerminalInfo.feeder_id;

                    data.term_id=sheetInfo.term_id;

                    data.feeder_id=struDmsTerminalInfo.feeder_id;

                    data.warn_type=MENU_OPT_DEV_STAT_SHEET_EXEC;

                    data.sheet_id_str=m_cur_sheet_id_str.toStdString();

                    //QString op_str=QString("参数名称:%1 信息体:%2 原值:%3 修改值:%4").arg(m_code_name_map[info.data_type]).arg(info.data_type).arg(cur_value_str).arg(info_value_str);

                    data.op_str=op_str.toStdString();

                    sendWarn(data);

#endif

                    break;

                }

            }

        }

        return 1;



    }

    return  ret;







}



int TermParamWindow::download_param_thread(T_Terminal_Para &modify_para, const SheetInfo &sheetInfo)

{

    if(m_pThread!=NULL)

    {

        delete m_pThread;

        m_pThread=NULL;

    }



    cout<<"----------国网参数预下装-----------"<<endl;

    short serv          = CH_DMS_TERMINAL_DATA;

    short event         = MT_DMS_TERM_PARA_MODIFY_PRE;

    m_download_reply_event  = MT_DMS_TERM_PARA_MODIFY_REPLY;

    m_sheet_result=0;

    m_pThread=new MsgBus_RW_Thread();

    m_pThread->init( sheetInfo.term_id, m_oper_timeout_slice );

    m_pThread->setRunFlag(0);

    if( m_pThread->init_msg( serv, event, m_download_reply_event, modify_para ) != 0 )

    {

        //初始化失败



        updateSheetStatus(m_cur_sheet_id_str,-1);

        for( int i = 0; i < modify_para.terminal_para_seq.size(); i++ )

        {

            Terminal_Para_Info info = modify_para.terminal_para_seq[i];

            QString code_str=QString::number(info.data_type,16);

            updateParamStatus(m_cur_sheet_id_str,-1,code_str);



        }

        sprintf(g_log_buf,"工单下装参数 初始化失败...");

        CLogManager::Instance()->WriteLineT(g_log_buf);

        QMessageBox::warning( this, tr("下装参数:"), tr("下装参数 初始化失败...") );

        return -1;

    }

    m_pThread->setSheetFlag(true);

    //connect( m_pThread, SIGNAL( Post_SheetParaCall(short,QByteArray,qint32)), this, SLOT( On_MsgBus_SheetParaCall(short,QByteArray,qint32)) );

    m_pThread->start();

    sheet_exec_dlg_start();

    T_Terminal_Para reply_para;

    T_Terminal_Op reply_op;



    while(1)

    {



        usleep(5000);

        cout<<"while!!!!!!!!"<<endl;

        m_sheet_result=m_pThread->getParamReply(reply_para);

        if(m_sheet_result==0)

            continue;

        else

            break;

    }

    if(m_pThread!=NULL)

    {

        delete m_pThread;

        m_pThread=NULL;

    }

    int ret=m_sheet_result;

    sprintf(g_log_buf,"工单执行下装返回结果:");

    CLogManager::Instance()->WriteLineT(g_log_buf);

    for( int j = 0; j < reply_para.terminal_para_seq.size(); j++ )

    {

        Terminal_Para_Info curinfo = reply_para.terminal_para_seq[j];

        sprintf(g_log_buf,"返回结果:result=%d",curinfo.result);

        CLogManager::Instance()->WriteLineT(g_log_buf);

    }

    if(ret==-1||ret==-2)

    {



        updateSheetStatus(m_cur_sheet_id_str,-1);

        for( int i = 0; i < modify_para.terminal_para_seq.size(); i++ )

        {

            Terminal_Para_Info info = modify_para.terminal_para_seq[i];

            QString code_str=QString::number(info.data_type,16);

            updateParamStatus(m_cur_sheet_id_str,-1,code_str);



        }

        sprintf(g_log_buf,"工单下装参数 消息总线注册失败...");

        CLogManager::Instance()->WriteLineT(g_log_buf);

        QMessageBox::warning( this, tr("下装参数:"), tr("下装参数 消息总线注册失败...") );



    }

    else if(ret==-3)

    {



        updateSheetStatus(m_cur_sheet_id_str,-3);

        for( int i = 0; i < modify_para.terminal_para_seq.size(); i++ )

        {

            Terminal_Para_Info info = modify_para.terminal_para_seq[i];

            QString code_str=QString::number(info.data_type,16);

            updateParamStatus(m_cur_sheet_id_str,-3,code_str);



        }

        sprintf(g_log_buf,"工单下装参数 消息请求发送失败...");

        CLogManager::Instance()->WriteLineT(g_log_buf);

        QMessageBox::warning( this, tr("下装参数:"), tr("下装参数 消息请求发送失败...") );

    }

    /* else if(ret==-4)

    {



    }*/

    else if(ret==-5)

    {



        updateSheetStatus(m_cur_sheet_id_str,-5);

        for( int i = 0; i < modify_para.terminal_para_seq.size(); i++ )

        {

            Terminal_Para_Info info = modify_para.terminal_para_seq[i];

            QString code_str=QString::number(info.data_type,16);

            updateParamStatus(m_cur_sheet_id_str,-5,code_str);



        }

        sprintf(g_log_buf,"工单下装参数 收到的消息长度小于0...");

        CLogManager::Instance()->WriteLineT(g_log_buf);

        QMessageBox::warning( this, tr("下装参数:"), tr("下装参数 收到的消息长度小于0...") );

    }

    else if(ret==-6)//超时

    {



        updateSheetStatus(m_cur_sheet_id_str,-6);

        for( int i = 0; i < modify_para.terminal_para_seq.size(); i++ )

        {

            Terminal_Para_Info info = modify_para.terminal_para_seq[i];

            QString code_str=QString::number(info.data_type,16);

            updateParamStatus(m_cur_sheet_id_str,-6,code_str);



        }

        sprintf(g_log_buf,"工单下装参数 超时...");

        CLogManager::Instance()->WriteLineT(g_log_buf);

        QMessageBox::warning( this, tr("下装参数:"), tr("下装参数 超时...") );



    }

    else if(ret==-7)//中止

    {



        updateSheetStatus(m_cur_sheet_id_str,-7);

        for( int i = 0; i < modify_para.terminal_para_seq.size(); i++ )

        {

            Terminal_Para_Info info = modify_para.terminal_para_seq[i];

            QString code_str=QString::number(info.data_type,16);

            updateParamStatus(m_cur_sheet_id_str,-7,code_str);



        }

        sprintf(g_log_buf,"工单下装参数 中止...");

        CLogManager::Instance()->WriteLineT(g_log_buf);

        QMessageBox::warning( this, tr("下装参数:"), tr("下装参数 中止...") );

    }

    else//ret==1

    {

        update_progress_dlg(m_oper_timeout_slice);//接收完成

        SheetStopProgressStep();



        bool bParamOk=true;





        for( int j = 0; j < reply_para.terminal_para_seq.size(); j++ )

        {

            Terminal_Para_Info curinfo = reply_para.terminal_para_seq[j];

            //sprintf(g_log_buf,"工单执行下装返回结果:result=%d",curinfo.result);

            //CLogManager::Instance()->WriteLineT(g_log_buf);

            if(curinfo.result!=2)//2返校成功  !=2返校失败

            {

                bParamOk=false;

                break;

            }

        }

        if(!bParamOk)

        {

            QMessageBox::warning( this, tr("下装参数:"), tr("下装参数 返校失败...") );

            updateSheetStatus(m_cur_sheet_id_str,-9);

            for( int i = 0; i < modify_para.terminal_para_seq.size(); i++ )

            {

                Terminal_Para_Info info = modify_para.terminal_para_seq[i];

                QString code_str=QString::number(info.data_type,16);

                updateParamStatus(m_cur_sheet_id_str,-9,code_str);



            }

            sprintf(g_log_buf,"工单返校失败");

            CLogManager::Instance()->WriteLineT(g_log_buf);

            return -9;

        }

        sprintf(g_log_buf,"工单返校成功");

        CLogManager::Instance()->WriteLineT(g_log_buf);

        return 1;



    }

    return  ret;



}

int TermParamWindow::act_param_thread(const T_Terminal_Para &modify_para,const SheetInfo & sheetInfo)

{

    if(m_pThread!=NULL)

    {

        delete m_pThread;

        m_pThread=NULL;

    }

    short serv          = CH_DMS_TERMINAL_DATA;

    short event         = MT_DMS_TERM_PARA_MODIFY;

    m_download_reply_event   = MT_DMS_TERM_PARA_MODIFY_CONF;





    T_Terminal_Op para;

    int area_no=sheetInfo.area_no;

    int line_no=sheetInfo.line_no;

    if( line_no >= TERM_PARAM_LINE_NO_BASE ) line_no -= TERM_PARAM_LINE_NO_BASE;

    Terminal_Para_Op item = { 0 };

    item.comm_fac_id    = sheetInfo.term_id;

    item.line_no        = line_no;

    item.serial_num     = area_no;

    item.para_type      = 0;    //0，运行参数 1，动作定值参数，

    //文档中有此说明，但前置未处理，即同时激活运行参数和动作定值



    sprintf(g_log_buf,"工单执行激活:");

    CLogManager::Instance()->WriteLineT(g_log_buf);

    sprintf(g_log_buf,"term_id=%ld line_no=%d area_no=%d para_type=%d",item.comm_fac_id,item.line_no,item.serial_num,item.para_type);

    CLogManager::Instance()->WriteLineT(g_log_buf);



    para.terminal_op_seq.push_back( item );

    para.num=para.terminal_op_seq.size();



    m_sheet_result=0;



    TWarnData data;

    DMS_TERMINAL_INFO_STRUCT struDmsTerminalInfo;

    m_rtdbOper.ReadTerminalInfo(struDmsTerminalInfo,sheetInfo.term_id);

    //data.combined_id=struDmsTerminalInfo.feeder_id;

    data.term_id=sheetInfo.term_id;

    data.warn_type=MENU_OPT_DEV_STAT_SHEET_EXEC;

    data.feeder_id=struDmsTerminalInfo.feeder_id;

    data.op_str="激活参数";

    data.sheet_id_str=m_cur_sheet_id_str.toStdString();

    sendWarn(data);





    m_pThread=new MsgBus_RW_Thread();

    m_pThread->init( sheetInfo.term_id, m_oper_timeout_slice );

    m_pThread->setRunFlag(0);

    if( m_pThread->init_msg( serv, event, m_download_reply_event, para ) != 0 )

    {

        //初始化失败



        updateSheetStatus(m_cur_sheet_id_str,-1);

        for( int i = 0; i < modify_para.terminal_para_seq.size(); i++ )

        {

            Terminal_Para_Info info = modify_para.terminal_para_seq[i];

            QString code_str=QString::number(info.data_type,16);

            updateParamStatus(m_cur_sheet_id_str,-1,code_str);



        }

        sprintf(g_log_buf,"工单激活参数 初始化失败...");

        CLogManager::Instance()->WriteLineT(g_log_buf);

        QMessageBox::warning( this, tr("激活参数:"), tr("激活参数 初始化失败...") );

        return -1;

    }

    // connect( m_pThread, SIGNAL( Post_SheetParaCall(short,QByteArray,qint32)), this, SLOT( On_MsgBus_SheetParaCall(short,QByteArray,qint32)) );

    m_pThread->setSheetFlag(true);

    m_pThread->start();

    sheet_exec_dlg_start();

    T_Terminal_Op reply_op;

    while(1)

    {



        usleep(5000);

        cout<<"while!!!!!!!!"<<endl;

        m_sheet_result=m_pThread->getParamReply(reply_op);

        if(m_sheet_result==0)

            continue;

        else

            break;

    }

    if(m_pThread!=NULL)

    {

        delete m_pThread;

        m_pThread=NULL;

    }

    int ret=m_sheet_result;

    sprintf(g_log_buf,"工单执行激活返回结果:");

    CLogManager::Instance()->WriteLineT(g_log_buf);

    for( int i = 0; i < reply_op.terminal_op_seq.size(); i++ )

    {

        Terminal_Para_Op &item = reply_op.terminal_op_seq[i];

        sprintf(g_log_buf,"返回结果:result=%d",item.result);

        CLogManager::Instance()->WriteLineT(g_log_buf);

    }

    if(ret==-1||ret==-2)

    {



        updateSheetStatus(m_cur_sheet_id_str,-1);

        for( int i = 0; i < modify_para.terminal_para_seq.size(); i++ )

        {

            Terminal_Para_Info info = modify_para.terminal_para_seq[i];

            QString code_str=QString::number(info.data_type,16);

            updateParamStatus(m_cur_sheet_id_str,-1,code_str);



        }

        sprintf(g_log_buf,"工单激活参数 消息总线注册失败...");

        CLogManager::Instance()->WriteLineT(g_log_buf);

        QMessageBox::warning( this, tr("激活参数:"), tr("激活参数 消息总线注册失败...") );



    }

    else if(ret==-3)

    {



        updateSheetStatus(m_cur_sheet_id_str,-3);

        for( int i = 0; i < modify_para.terminal_para_seq.size(); i++ )

        {

            Terminal_Para_Info info = modify_para.terminal_para_seq[i];

            QString code_str=QString::number(info.data_type,16);

            updateParamStatus(m_cur_sheet_id_str,-3,code_str);



        }

        sprintf(g_log_buf,"工单激活参数 消息请求发送失败...");

        CLogManager::Instance()->WriteLineT(g_log_buf);

        QMessageBox::warning( this, tr("激活参数:"), tr("激活参数 消息请求发送失败...") );

    }

    /* else if(ret==-4)

    {



    }*/

    else if(ret==-5)

    {



        updateSheetStatus(m_cur_sheet_id_str,-5);

        for( int i = 0; i < modify_para.terminal_para_seq.size(); i++ )

        {

            Terminal_Para_Info info = modify_para.terminal_para_seq[i];

            QString code_str=QString::number(info.data_type,16);

            updateParamStatus(m_cur_sheet_id_str,-5,code_str);



        }

        sprintf(g_log_buf,"工单激活参数 收到的消息长度小于0...");

        CLogManager::Instance()->WriteLineT(g_log_buf);

        QMessageBox::warning( this, tr("激活参数:"), tr("激活参数 收到的消息长度小于0...") );

    }

    else if(ret==-6)//超时

    {



        updateSheetStatus(m_cur_sheet_id_str,-6);

        for( int i = 0; i < modify_para.terminal_para_seq.size(); i++ )

        {

            Terminal_Para_Info info = modify_para.terminal_para_seq[i];

            QString code_str=QString::number(info.data_type,16);

            updateParamStatus(m_cur_sheet_id_str,-6,code_str);



        }

        sprintf(g_log_buf,"工单激活参数 超时...");

        CLogManager::Instance()->WriteLineT(g_log_buf);

        QMessageBox::warning( this, tr("激活参数:"), tr("激活参数 超时...") );





    }

    else if(ret==-7)//中止

    {



        updateSheetStatus(m_cur_sheet_id_str,-7);

        for( int i = 0; i < modify_para.terminal_para_seq.size(); i++ )

        {

            Terminal_Para_Info info = modify_para.terminal_para_seq[i];

            QString code_str=QString::number(info.data_type,16);

            updateParamStatus(m_cur_sheet_id_str,-7,code_str);



        }

        sprintf(g_log_buf,"工单激活参数 中止...");

        CLogManager::Instance()->WriteLineT(g_log_buf);

        QMessageBox::warning( this, tr("激活参数:"), tr("激活参数 中止...") );

    }

    else//ret==1

    {

        update_progress_dlg(m_oper_timeout_slice);//接收完成

        SheetStopProgressStep();



        for( int i = 0; i < reply_op.terminal_op_seq.size(); i++ )

        {

            Terminal_Para_Op &item = reply_op.terminal_op_seq[i];



            QString strText = tr("返回值=%1 ").arg(item.result);



            //sprintf(g_log_buf,"工单执行激活返回结果:result=%d",item.result);

            //CLogManager::Instance()->WriteLineT(g_log_buf);

            switch( item.result )

            {

#if 0

            case 0:

                strText = tr("默认(%1)").arg(item.result);

                strText = tr("默认");

                ZMessageBox::warning( this, strOpDesc, strText );

                break;

            case 1:

                QString strText = tr("否定确认(%1)").arg(item.result);

                strText = tr("否定确认");

                ZMessageBox::critical( this, strOpDesc, strText );

                break;

#endif

            case 2:

            {

                QString strText = tr("肯定确认(%1)").arg(item.result);

                strText = tr("肯定确认");

                sprintf(g_log_buf,"工单激活参数成功");

                CLogManager::Instance()->WriteLineT(g_log_buf);

                update_sheet_reply_act_para( modify_para );

                //ZMessageBox::information( this, tr("激活参数:"), strText );

                return 1;

            }

            default:

            {

                sprintf(g_log_buf,"工单激活参数失败");

                CLogManager::Instance()->WriteLineT(g_log_buf);

                QString strText=tr("激活失败(%1)......").arg(item.result);

                ZMessageBox::warning( this, tr("激活参数:"), strText );

                break;

            }

            }

        }



        updateSheetStatus(m_cur_sheet_id_str,-10);

        for( int i = 0; i < modify_para.terminal_para_seq.size(); i++ )

        {

            Terminal_Para_Info info = modify_para.terminal_para_seq[i];

            QString code_str=QString::number(info.data_type,16);

            updateParamStatus(m_cur_sheet_id_str,-10,code_str);



        }

        return -10;

    }

    return  ret;







}



int TermParamWindow::check_param_thread(T_Terminal_Para &call_para, T_Terminal_Para &modify_para, const SheetInfo &sheetInfo)

{

    if(m_pThread!=NULL)

    {

        delete m_pThread;

        m_pThread=NULL;

    }

    //激活参数

    short serv          = CH_DMS_TERMINAL_DATA;

    short event         = MT_DMS_TERM_PARA_CALL;;

    m_download_reply_event   = MT_DMS_TERM_PARA_REPLY;

    m_sheet_result=0;

    m_pThread=new MsgBus_RW_Thread();

    m_pThread->init( sheetInfo.term_id, m_oper_timeout_slice );





    m_pThread->setRunFlag(0);

    if( m_pThread->init_msg( serv, event, m_download_reply_event, call_para ) != 0 )

    {



        updateSheetStatus(m_cur_sheet_id_str,-1);

        for( int i = 0; i < call_para.terminal_para_seq.size(); i++ )

        {

            Terminal_Para_Info info = call_para.terminal_para_seq[i];

            QString code_str=QString::number(info.data_type,16);

            updateParamStatus(m_cur_sheet_id_str,-1,code_str);



        }

        sprintf(g_log_buf,"工单校验参数 初始化失败...");

        CLogManager::Instance()->WriteLineT(g_log_buf);

        QMessageBox::warning( this, tr("校验参数:"), tr("校验参数 初始化失败...") );

        //初始化失败

        return -1;

    }



    //       connect( m_pThread, SIGNAL( finished() ), m_pThread, SLOT( deleteLater() ) );

    // connect( m_pThread, SIGNAL( Post_SheetParaCall(short,QByteArray,qint32)), this, SLOT( On_MsgBus_SheetParaCall(short,QByteArray,qint32)) );

    m_pThread->setSheetFlag(true);

    m_pThread->start();

    cout<<"zzzzzzzzzzzzzz"<<endl;

    sheet_exec_dlg_start();

    T_Terminal_Para reply_para;

    T_Terminal_Op reply_op;



    while(1)

    {



        usleep(5000);

        cout<<"while!!!!!!!!"<<endl;

        m_sheet_result=m_pThread->getParamReply(reply_para);

        if(m_sheet_result==0)

            continue;

        else

            break;

    }

    if(m_pThread!=NULL)

    {

        delete m_pThread;

        m_pThread=NULL;

    }

    int ret=m_sheet_result;

    //reply_para=m_reply_para;

    CLogManager::Instance()->WriteLineT("工单执行校验参数返回结果:");

    for( int i = 0; i < reply_para.terminal_para_seq.size(); i++ )

    {

        Terminal_Para_Info pi = reply_para.terminal_para_seq[i];

        sprintf(g_log_buf,"comm_fac_id=%ld line_no=%d area_no=%d para_type=%d value_type=%d data_type=%d value_f=%f value_i=%d result=%d ",

                pi.comm_fac_id,pi.line_no,pi.serial_num,pi.para_type,pi.value_type,pi.data_type,pi.value_f,pi.value_i,pi.result);

        CLogManager::Instance()->WriteLineT(g_log_buf);

        //  CLogManager::Instance()->WriteLineT(QString::fromUtf8(pi.value_s).toStdString());

    }

    if(ret==-1||ret==-2)

    {



        updateSheetStatus(m_cur_sheet_id_str,-1);

        for( int i = 0; i < call_para.terminal_para_seq.size(); i++ )

        {

            Terminal_Para_Info info = call_para.terminal_para_seq[i];

            QString code_str=QString::number(info.data_type,16);

            updateParamStatus(m_cur_sheet_id_str,-1,code_str);



        }

        sprintf(g_log_buf,"工单校验参数 消息总线注册失败...");

        CLogManager::Instance()->WriteLineT(g_log_buf);

        QMessageBox::warning( this, tr("校验参数:"), tr("校验参数 消息总线注册失败...") );

    }

    else if(ret==-3)

    {



        updateSheetStatus(m_cur_sheet_id_str,-3);

        for( int i = 0; i < call_para.terminal_para_seq.size(); i++ )

        {

            Terminal_Para_Info info = call_para.terminal_para_seq[i];

            QString code_str=QString::number(info.data_type,16);

            updateParamStatus(m_cur_sheet_id_str,-3,code_str);



        }

        sprintf(g_log_buf,"工单校验参数 消息请求发送失败...");

        CLogManager::Instance()->WriteLineT(g_log_buf);

        QMessageBox::warning( this, tr("校验参数:"), tr("校验参数 消息请求发送失败...") );

    }

    /* else if(ret==-4)

    {



    }*/

    else if(ret==-5)

    {



        updateSheetStatus(m_cur_sheet_id_str,-5);

        for( int i = 0; i < call_para.terminal_para_seq.size(); i++ )

        {

            Terminal_Para_Info info = call_para.terminal_para_seq[i];

            QString code_str=QString::number(info.data_type,16);

            updateParamStatus(m_cur_sheet_id_str,-5,code_str);



        }

        sprintf(g_log_buf,"工单校验参数 收到的消息长度小于0...");

        CLogManager::Instance()->WriteLineT(g_log_buf);

        QMessageBox::warning( this, tr("校验参数:"), tr("校验参数 收到的消息长度小于0...") );

    }

    else if(ret==-6)//超时

    {



        updateSheetStatus(m_cur_sheet_id_str,-6);

        for( int i = 0; i < call_para.terminal_para_seq.size(); i++ )

        {

            Terminal_Para_Info info = call_para.terminal_para_seq[i];

            QString code_str=QString::number(info.data_type,16);

            updateParamStatus(m_cur_sheet_id_str,-6,code_str);



        }

        sprintf(g_log_buf,"工单校验参数 超时...");

        CLogManager::Instance()->WriteLineT(g_log_buf);

        QMessageBox::warning( this, tr("校验参数:"), tr("校验参数 超时...") );



    }

    else if(ret==-7)//中止

    {



        updateSheetStatus(m_cur_sheet_id_str,-7);

        for( int i = 0; i < call_para.terminal_para_seq.size(); i++ )

        {

            Terminal_Para_Info info = call_para.terminal_para_seq[i];

            QString code_str=QString::number(info.data_type,16);

            updateParamStatus(m_cur_sheet_id_str,-7,code_str);



        }

        sprintf(g_log_buf,"工单校验参数 中止...");

        CLogManager::Instance()->WriteLineT(g_log_buf);

        QMessageBox::warning( this, tr("校验参数:"), tr("校验参数 中止...") );

    }

    else//ret==1

    {

        update_progress_dlg(m_oper_timeout_slice);//接收完成

        SheetStopProgressStep();



        update_sheet_reply_para(reply_para);

        bool bSheetOk=true;

        for( int i = 0; i < modify_para.terminal_para_seq.size(); i++ )

        {



            Terminal_Para_Info info = modify_para.terminal_para_seq[i];

            bool bParamOk=false;



            QString code_str=QString::number(info.data_type,16);

            for( int j = 0; j < reply_para.terminal_para_seq.size(); j++ )

            {



                Terminal_Para_Info curinfo = reply_para.terminal_para_seq[j];





                curinfo.para_type=info.para_type;

                QString  cur_value_str=fixed_param_value_to_string(curinfo);



                QString  info_value_str=fixed_param_value_to_string(info);

                qDebug()<<"cur code==="<<curinfo.data_type<<" code=="<<info.data_type<<"  cur value=="<<cur_value_str <<" value=="<<info_value_str;

                qDebug()<<"value_type==="<<curinfo.value_type<<"  value_type=="<<info.value_type;

                if(curinfo.data_type==info.data_type)

                {

                    if(cur_value_str!=info_value_str)

                    {



                        sprintf(g_log_buf,"激活失败 cur_code=%d cur_value=%s  code=%d value=%s    code===%s\n",curinfo.data_type,cur_value_str.toStdString().c_str(),info.data_type,info_value_str.toStdString().c_str(),code_str.toStdString().c_str());

                        cout<<CLogManager::Instance()->WriteLineT(g_log_buf);

                        //bParamOk=false;

                        m_check_content_str+=QString("执行失败@%1@%2@%3\n").arg(curinfo.data_type).arg(m_code_name_map[curinfo.data_type]).arg(getFailResult(-11));

                        break;

                    }

                    else

                    {

                        bParamOk=true;

                    }

                    m_check_content_str+=QString("执行成功@%1@%2\n").arg(curinfo.data_type).arg(m_code_name_map[curinfo.data_type]);

                }

            }

            if(bParamOk)

            {



                updateParamStatus(m_cur_sheet_id_str,1,code_str);

            }

            else

            {



                updateParamStatus(m_cur_sheet_id_str,-11,code_str);

                bSheetOk=false;

            }

            //if(!bOk)

            //  break;

        }

        if(bSheetOk)

        {

            sprintf(g_log_buf,"工单校验参数成功......");

            CLogManager::Instance()->WriteLineT(g_log_buf);

            updateSheetStatus(m_cur_sheet_id_str,1);

            QMessageBox::warning( this, tr("校验参数:"), tr("校验成功......") );

            return 1;

        }

        else

        {

            sprintf(g_log_buf,"工单校验参数失败......");

            CLogManager::Instance()->WriteLineT(g_log_buf);

            updateSheetStatus(m_cur_sheet_id_str,-11);

            QMessageBox::warning( this, tr("校验参数:"), tr("校验失败......") );

            return -11;

        }





        return 1;



    }

    return  ret;





}

void TermParamWindow::On_MsgBus_SheetParaCall( short reply_event, QByteArray buf,qint32 result )

{

    cout<<"result====="<<result<<endl;

    m_sheet_result=result;

    if( ! MsgBusDataIO::FromBuffer( buf, m_reply_para ) ) {



        cout<<"zzzzzzzzzzzzzzzzzzzzzz1"<<endl;

        return;

    }

    int count = m_reply_para.terminal_para_seq.size();

    Q_ASSERT( m_reply_para.num == count );



}

QString TermParamWindow::fixed_param_value_to_string( const Terminal_Para_Info &info )

{

    QString strValue;



    switch( info.value_type )

    {

    case 2: //int

    case 4: //short

    case 5: //unsigned short

    case 6: //unsigned int

    case 8: //BOOL

        strValue = tr("%1").arg( info.value_i );

        break;

    case 7:

        if( strlen( info.value_s ) == 0 )

            strValue = tr("null");

        else

            strValue = tr("%1").arg( info.value_s );

        break;

    case 1: //类型不确定

    case 3: //float

    default:

    {

        char szText[32];

        sprintf( szText, "%.4f", info.value_f );



        TermParamRec::TrimRightDotZero( szText );



        strValue = tr("%1").arg( szText );

    }

        break;

    }

#if 0

    if( info.para_type == 1/*动作定值*/ )

    {

        strValue = ( strValue == tr("1") ? tr("投入") : tr("退出") );

    }

#endif

    return strValue;

}

bool TermParamWindow::fixed_string_to_param_value( QString strText, Terminal_Para_Info &info )

{

    bool bFlag=true;

    /*  if( info.para_type == 1

    {



        //info.value_f = ( ( strText.indexOf( tr("退出") ) >= 0 ) ? 0 : 1 );

        info.value_f = ( ( strText.indexOf( tr("0") ) >= 0 ) ? 0 : 1 );

    }

    else*/

    {

        switch( info.value_type )

        {

        case 2: //int

        case 4: //short

        case 5: //unsigned short

        case 6: //unsigned int

        case 8: //BOOL

            info.value_i = strText.toInt(&bFlag);

            break;

        case 7:

            strcpy( info.value_s, strText.toStdString().c_str() );

            break;

        case 1: //类型不确定

        case 3: //float

        default:

            info.value_f = strText.toFloat(&bFlag);

            break;

        }

    }



    //cg2018

    /*	{

        char szDataType[32];

        sprintf( szDataType, "%04x", info.data_type );

        QString strTip = tr("[%1] code=%2 serial_num=%3 值类型=%4 整数值=%5 浮点值=%6 字符串=%7")

                            .arg(QDateTime::currentDateTime().toString("yyyy-MM-dd hh:mm:ss"))

                            .arg(szDataType).arg(info.serial_num)

                            .arg(info.value_type).arg(info.value_i).arg(info.value_f).arg(info.value_s));

        QMessageBox::information( this, tr("write_param"), strTip );

    }*/



    return bFlag;

}



void TermParamWindow::sheet_exec_dlg_start()

{



    if( m_pProgressTimer == NULL )

    {

        m_start_time = time( NULL );

        //m_reply_event = event_id;

        m_pProgressTimer = new QTimer( this );

        m_pProgressTimer->start( 200 );

        connect( m_pProgressTimer, SIGNAL( timeout() ), this, SLOT( OnSheetProgressStep() ) );

        if( m_pProgressBar != NULL ) m_pProgressBar->setVisible( true );

    }



    if( m_pProgressDlg != NULL )

    {

        m_pProgressDlg->close();



        delete m_pProgressDlg;

        m_pProgressDlg = NULL;



        m_progress_idx = 0;

    }

    m_sheet_exec_status=0;//0默认 1超时  2中止



    m_pProgressDlg = new QProgressDialog( this );

    m_pProgressDlg->setFont( this->font() );



    int w = m_pProgressDlg->width();

    m_pProgressDlg->setMinimumWidth( w * 2 );



    m_pProgressDlg->setWindowTitle(get_event_desc(m_download_reply_event) );

    m_pProgressDlg->setLabelText( tr("正在操作中...") );

    m_pProgressDlg->setRange( 0, m_oper_timeout_slice );

    m_pProgressDlg->setModal( true );

    m_pProgressDlg->setCancelButtonText(tr("中止"));

    if(m_pProgressDlg->exec()==QProgressDialog::Rejected)

    {

        if(m_sheet_exec_status==-6)//超时

            return;

        cout<<"zzzzzzzzzzzzz"<<m_sheet_exec_status<<endl;

        m_sheet_exec_status=-7;

        cout<<"1stop "<<endl;

        if(m_pThread!=NULL)

        {

            cout<<"stop "<<endl;

            m_pThread->setRunFlag(m_sheet_exec_status);

        }

        if( m_pProgressBar != NULL ) m_pProgressBar->setValue( m_oper_timeout_slice );

        update_progress_dlg( m_oper_timeout_slice );

        SheetStopProgressStep();

        return;

    }

}

void TermParamWindow::OnSheetProgressStep()

{



    time_t cur_t = time( NULL );

    if( m_start_time > 0 && cur_t - m_start_time > m_oper_timeout_slice-2)   //超时

    {





        cout<<"1time over"<<endl;



        if( m_pProgressBar != NULL ) m_pProgressBar->setValue( m_oper_timeout_slice );

        update_progress_dlg( m_oper_timeout_slice );

        SheetStopProgressStep();

        m_sheet_exec_status=-6;

        if(m_pThread!=NULL)

        {

            cout<<"time over"<<endl;

            m_pThread->setRunFlag(m_sheet_exec_status);

        }



        return;

    }

#if 0

    else if( m_pProgressDlg != NULL && m_pProgressDlg->wasCanceled() )      //中止

    {

        m_sheet_exec_status=-7;

        cout<<"1stop "<<endl;

        if(m_pThread!=NULL)

        {

            cout<<"stop "<<endl;

            m_pThread->setRunFlag(m_sheet_exec_status);

        }

        if( m_pProgressBar != NULL ) m_pProgressBar->setValue( m_oper_timeout_slice );

        update_progress_dlg( m_oper_timeout_slice );

        SheetStopProgressStep();

        return;

    }

#endif

    else

    {

        int step = ( time(NULL) - m_start_time );



#if 1

        if(m_pThread!=NULL)

        {

            T_Terminal_Para reply_para;

            int ret=m_pThread->getParamReply(reply_para);

            if(ret!=0)

            {



                // m_sheet_exec_status=-6;

                step=m_oper_timeout_slice;

            }

        }

#endif

        if( m_pProgressBar != NULL ) m_pProgressBar->setValue( step );

        update_progress_dlg( step );



        if(step==m_oper_timeout_slice)//消息发送后快速返回

            SheetStopProgressStep();



    }



}

void TermParamWindow::SheetStopProgressStep()

{



    if( m_pProgressTimer != NULL )

    {

        m_pProgressTimer->stop();



        disconnect( m_pProgressTimer, SIGNAL( timeout() ), this, SLOT( OnSheetProgressStep() ) );



        delete m_pProgressTimer;

        m_pProgressTimer = NULL;

    }

    if( m_pOperLabel != NULL ) m_pOperLabel->setText( tr("空闲") );

    if( m_pProgressBar != NULL ) m_pProgressBar->setVisible( false );



    if( m_pProgressDlg != NULL )

    {

        m_pProgressDlg->close();



        delete m_pProgressDlg;

        m_pProgressDlg = NULL;



        m_progress_idx = 0;

    }

}

void TermParamWindow::on_pushButton_CancelSheet_clicked()

{

    if(QMessageBox::warning(this,tr("提示:"),tr("是否确定撤销已选中工单?"),QMessageBox::Yes|QMessageBox::No)==QMessageBox::No)

        return;



    QItemSelectionModel *select_model=ui->tableView->selectionModel();

    QModelIndexList index_lst=select_model->selectedRows();

    if(index_lst.count()<=0)

    {

        QMessageBox::warning(this,tr("提示:"),tr("未选中工单"));

        return;

    }



    //获取表单参数信息

    QStandardItemModel *pModel=(QStandardItemModel *)ui->tableView->model();

    map<long,QString> termId_fileName_map;



    for(int row=0;row<index_lst.count();++row)

    {



        QString gis_id_str;

        QString ip_str;



        QString content_str;

        QString code_str;

        QString line_no_str;

        QString value_str;



        QString param_name_str;

        QString file_name_str;

        QString all_content_str;

        QString file_path_str;

        QString dev_flag_str;

        QDateTime datetime=QDateTime::currentDateTime();

        QString datetime_str=datetime.toString("yyyyMMddhhmmss");



        QModelIndex index=index_lst.at(row);

        QString sheet_id_str=pModel->item(index.row(),SHEET_COL_TASK_ID)->text();

        QString sheet_name_str=pModel->item(index.row(),SHEET_COL_TASK_NAME)->text();



        QString sheet_file_name_str=pModel->item(index.row(),SHEET_COL_FILE_NAME)->text();

        //file_name.replace("待执行定值","执行定值结果");

        // file_name.replace("未","已");

        QList<QVariantList> lstData;

        getParamContentBySheetId(sheet_id_str,lstData);



        QList<QVariantList>  sheetDataLst;

        getSheetStatusSheetId(sheet_id_str,sheetDataLst);

        if(sheetDataLst.count()<=0)

            continue;

        qint32 sheet_status=sheetDataLst[0][0].toInt();

        if(sheet_status!=SHEET_STATUS_DEFAULT)

        {

            QMessageBox::warning(this,tr("提示:"),tr("工单:%1已执行 不可以撤销!!!!!").arg(sheet_id_str));

            continue;

        }





        if(lstData.count()<=0)

        {

#if 0

            QString tmp_content_str=sheet_file_name_str.split(",");



            sheet_file_name_str=sheet_file_name_str.replace("撤销失败","待执行定值");

            sheet_file_name_str=sheet_file_name_str.replace("未","已");

            file_path_str=QString(m_exec_file_path_str.c_str())+file_name_str;

            writeFile(file_path_str+QString(".pwdz"),content_str);

#endif

            continue;

        }





        for(int ct=0;ct<lstData.count();++ct)

        {



            code_str=QString(lstData[ct][SHEET_PARAM_COL_PARAM_CODE].toString());;

            line_no_str=lstData[ct][SHEET_PARAM_COL_LINE_NO].toString();

            value_str=lstData[ct][SHEET_PARAM_COL_PARAM_VALUE].toString();

            gis_id_str=lstData[ct][SHEET_PARAM_COL_GIS_ID].toString();

            ip_str=lstData[ct][SHEET_PARAM_COL_IP].toString();

            param_name_str=lstData[ct][SHEET_PARAM_COL_PARAM_NAME].toString();

            dev_flag_str=lstData[ct][SHEET_PARAM_COL_DEV_FLAG].toString();

            content_str+=QString("@%1@%2@%3\n").arg(code_str).arg(param_name_str).arg(value_str);



        }

        //cout<<CLogManager::Instance()->WriteLineT(g_log_buf);

        QString sql_str=QString("delete from d5000.dms_param_content_his  where task_id='%1'").arg(sheet_id_str);

        if(m_hisdbOper.ExecSql(sql_str)<0)

        {



            QMessageBox::warning(this,tr("提示:"),tr("工单撤销失败!!!"));

            sprintf(g_log_buf,"%s \n",sql_str.toStdString().c_str());

            cout<<CLogManager::Instance()->WriteLineT(g_log_buf);



            file_name_str=QString("已@撤销失败@%1@GISID%2@IP%3@组号%4@时间%5@是否本体终端%6").arg(sheet_name_str).arg(gis_id_str).arg(ip_str).arg(line_no_str).arg(datetime_str).arg(dev_flag_str);

            all_content_str=file_name_str+QString("\n")+content_str;

            file_path_str=QString(m_exec_file_path_str.c_str())+file_name_str;

            writeFile(file_path_str+QString(".pwdz"),all_content_str);



            return;

        }



        sql_str=QString("delete from d5000.dms_param_file_his where task_id='%1'").arg(sheet_id_str);

        if(m_hisdbOper.ExecSql(sql_str)<0)

        {



            QMessageBox::warning(this,tr("提示:"),tr("工单撤销失败!!!"));

            sprintf(g_log_buf,"%s \n",sql_str.toStdString().c_str());

            cout<<CLogManager::Instance()->WriteLineT(g_log_buf);





            file_name_str=QString("已@撤销失败@%1@GISID%2@IP%3@组号%4@时间%5@是否本体终端%6").arg(sheet_name_str).arg(gis_id_str).arg(ip_str).arg(line_no_str).arg(datetime_str).arg(dev_flag_str);

            all_content_str=file_name_str+QString("\n")+content_str;

            file_path_str=QString(m_exec_file_path_str.c_str())+file_name_str;

            writeFile(file_path_str+QString(".pwdz"),all_content_str);

            return;

        }



        file_name_str=QString("已@撤销成功@%1@GISID%2@IP%3@组号%4@时间%5@是否本体终端%6").arg(sheet_name_str).arg(gis_id_str).arg(ip_str).arg(line_no_str).arg(datetime_str).arg(dev_flag_str);

        all_content_str=file_name_str+QString("\n")+content_str;

        file_path_str=QString(m_exec_file_path_str.c_str())+file_name_str;

        writeFile(file_path_str+QString(".pwdz"),all_content_str);



        QMessageBox::warning(this,tr("提示:"),tr("工单撤销操作完成 请点击全部查询按钮查看!!!!!"));

    }

}

void TermParamWindow::On_rowDoubleClicked(const QModelIndex & index)

{



    if(index.column()!=0)

        return;





    QString task_id=index.data().toString();

    qDebug()<<"task_id==="<<task_id;

    param_dlg *dg=new param_dlg(this);

    dg->init(task_id);

    dg->show();



}





QString TermParamWindow::getSheetStatusStr(int status)

{

    switch(status)

    {

    case 0:

        return tr("未执行");

    case 1:

        return tr("执行成功");

    case 2:

        return tr("下装失败");

    case 3:

        return tr("激活失败");

    default:

        return tr("未知状态");

    }

}

void TermParamWindow::load_param_file()

{

    QString strFilter;

    if(ui->checkBox_ExecTime->checkState()==Qt::Checked)

    {

        strFilter=QString("WHERE  recv_time>=to_date('%1','yyyy-mm-dd hh24:mi:ss') AND recv_time<=to_date('%2','yyyy-mm-dd hh24:mi:ss')")

                .arg(ui->dateTimeEdit_begin->dateTime().toString("yyyy-MM-dd hh:mm:ss"))

                .arg(ui->dateTimeEdit_end->dateTime().toString("yyyy-MM-dd hh:mm:ss"));

    }

    else

    {

        QDateTime datetime=QDateTime::currentDateTime();

        QDate date=datetime.date();

        date.setDate(date.year(),date.month(),1);

        datetime.setDate(date);

        QString datetime_str=datetime.toString("yyyy-MM-dd hh:mm:ss");

        strFilter=QString("WHERE  recv_time>=to_date('%1','yyyy-mm-dd hh24:mi:ss')").arg(datetime_str);

    }



#ifdef SUPPORT_KINGBASES

    QString strSql=QString("SELECT task_id,task_code,recv_time,execution,ip_port,area_name,supply_name,task_name,finish_time,file_name,user_name FROM d5000.dms_param_file_his@emslink %1").arg(strFilter);

#else

    QString strSql=QString("SELECT task_id,task_code,recv_time,execution,ip_port,area_name,supply_name,task_name,finish_time,file_name,user_name FROM d5000.dms_param_file_his %1").arg(strFilter);



#endif

    QList<QVariantList> lstData;

    m_lstData.clear();

    //QList<QVariantList> lstData;

    m_hisdbOper.QueryBySql(strSql,m_lstData);

    cout<<"ct====="<<m_lstData.count()<<endl;

}



void TermParamWindow::initTableWidget_paramFile()

{

    load_param_file();



    QStringList lstHeader;

    lstHeader << tr("任务ID");

    lstHeader << tr("定值单编号");

    lstHeader << tr("接受时间");

    lstHeader << tr("执行情况");

    lstHeader << tr("IP地址");

    lstHeader << tr("所属区域");

    lstHeader << tr("所属供电所");

    lstHeader << tr("任务名称");

    lstHeader << tr("完成时间");

    lstHeader << tr("文件名");

    lstHeader << tr("执行人");







    QList< QStandardItem * >    listColumn;



    QString column_name;

    foreach( column_name, lstHeader )

    {

        QStandardItem *pH = new QStandardItem( column_name );

        listColumn.append( pH );

    }



    //添加表头

    //准备数据模型

    QStandardItemModel *pModel = new QStandardItemModel();



    for(int row=0;row<m_lstData.count();++row)

    {

        for(int col=0;col<m_lstData[row].count();++col)

        {

            //       QStandardItem *value_Item = new QStandardItem( tr("%1").arg(m_lstData[row][col].toString()) );

            QStandardItem *value_Item ;//= new QStandardItem( tr("%1").arg(m_lstData[row][col].toString()) );



            if(col==SHEET_COL_EXEC_STATUS)

            {

                int status=m_lstData[row][col].toInt();

                value_Item= new QStandardItem( tr("%1").arg(getFailResult(status)) );

            }

            else

            {

                if(col==SHEET_COL_RECV_TIME||col==SHEET_COL_EXEC_TIME)

                {

                    QDateTime datetime;

                    datetime.setTime_t(m_lstData[row][col].toInt());

                    QString datetime_str=datetime.toString("yyyy-MM-dd hh:mm:ss");

                    // cout<<"time col====="<<col<<endl;

                    if(col==SHEET_COL_EXEC_TIME)

                    {

                        if(m_lstData[row][SHEET_COL_EXEC_STATUS].toInt()==0)

                            value_Item= new QStandardItem();

                        else

                            value_Item= new QStandardItem( tr("%1").arg(datetime_str) );

                    }

                    else

                    {

                        value_Item= new QStandardItem( tr("%1").arg(datetime_str) );

                    }

                }

                else

                {

                    value_Item= new QStandardItem( tr("%1").arg(m_lstData[row][col].toString()) );

                }

                //value_Item= new QStandardItem( tr("%1").arg(m_lstData[row][col].toString()) );

            }

            if(col==SHEET_COL_RECV_TIME)

                qDebug()<<"time=================="<<m_lstData[row][col].toString()<<" "<<m_lstData[row][col].toLongLong();

            value_Item->setEditable( false );

            pModel->setItem( row, col, value_Item );



        }

    }



    cout<<"listColumn.size()========"<<listColumn.size()<<endl;

    for( int i = 0; i < listColumn.size(); i++ )

        pModel->setHorizontalHeaderItem( i, listColumn[i] );

    //利用setModel()方法将数据模型与QTableView绑定

    ui->tableView->setModel(pModel);





    //设置表格的各列的宽度值

    for( int c = 0; c < pModel->columnCount(); c++ )

        ui->tableView->setColumnWidth( c, 100 );









    //设置选中时为整行选中

    ui->tableView->setSelectionBehavior( QAbstractItemView::SelectRows );

    //单行选

    //  ui->tableView->setSelectionMode( QAbstractItemView::SingleSelection );

    //设置表格的单元为只读属性，即不能编辑

    //if( bReadOnly ) ui->tableView->setEditTriggers( QAbstractItemView::NoEditTriggers );



    connect( ui->tableView, SIGNAL(doubleClicked(const QModelIndex&)), this, SLOT( On_rowDoubleClicked(const QModelIndex&)));

    QHeaderView *pHV = ui->tableView->horizontalHeader();

    if(pHV != NULL)

    {

        pHV->setSortIndicator(0,Qt::AscendingOrder);

        pHV->setSortIndicatorShown(true);

        connect( pHV, SIGNAL(sectionClicked(int)), this, SLOT(on_header_clicked(int)) );

    }

    ui->tableView->resizeColumnsToContents();

    ui->tableView->update();



    //  ui->co

    ui->comboBox_Area_3->addItem(tr("全部区域"));

    ui->comboBox_Area_3->setFixedWidth(130);

    ui->comboBox_Supply->addItem(tr("全部供电所"));

    ui->comboBox_Supply->setFixedWidth(130);







    QStringList areaLst;

    QStringList supplyLst;

    for(int row=0;row<m_lstData.count();++row)

    {

        if(areaLst.indexOf(m_lstData[row][5].toString())==-1&&!m_lstData[row][5].toString().isEmpty())

        {

            ui->comboBox_Area_3->addItem(m_lstData[row][5].toString());

            areaLst<<m_lstData[row][5].toString();

        }

        if(supplyLst.indexOf(m_lstData[row][6].toString())==-1&&!m_lstData[row][6].toString().isEmpty())

        {

            ui->comboBox_Supply->addItem(m_lstData[row][6].toString());

            supplyLst<<m_lstData[row][6].toString();

        }

    }

    QDate date;

    QTime time;

    QDateTime start_date_time;//=QDateTime::currentDateTime();

    date=QDateTime::currentDateTime().date();

    time.setHMS(0,0,0);

    start_date_time.setDate(date);

    start_date_time.setTime(time);

    ui->dateTimeEdit_begin->setDateTime(start_date_time);



    QDateTime end_date_time=QDateTime::currentDateTime();

    ui->dateTimeEdit_end->setDateTime(end_date_time);





}



void TermParamWindow::on_header_clicked(int index)

{





    int i   = ui->tableView->horizontalHeader()->sortIndicatorOrder();

    QStandardItemModel *pModel=(QStandardItemModel *)ui->tableView->model();

    if(pModel==NULL)

        return;



    if(0 == i)

        pModel->sort(index,Qt::AscendingOrder);

    else

        pModel->sort(index,Qt::DescendingOrder);

}

void TermParamWindow::getParamContentBySheetId(const QString &sheet_id,QList<QVariantList>& lstData)

{

#ifdef SUPPORT_KINGBASES

    QString strSql=QString("SELECT content_id,task_id,ip_port,gis_id,xh_id,term_id,param_line,param_area,param_code,param_name,param_value,execution,user_name,term_flag FROM d5000.dms_param_content_his@emslink WHERE task_id='%1'").arg(sheet_id);

#else

    QString strSql=QString("SELECT content_id,task_id,ip_port,gis_id,xh_id,term_id,param_line,param_area,param_code,param_name,param_value,execution,user_name,term_flag FROM d5000.dms_param_content_his WHERE task_id='%1'").arg(sheet_id);



#endif

    lstData.clear();

    m_hisdbOper.QueryBySql(strSql,lstData);

}

void TermParamWindow::getSheetStatusSheetId(const QString &sheet_id,QList<QVariantList>& lstData)

{

#ifdef SUPPORT_KINGBASES

    QString strSql=QString("SELECT execution FROM d5000.dms_param_file_his@emslink WHERE task_id='%1'").arg(sheet_id);

#else

    QString strSql=QString("SELECT execution FROM d5000.dms_param_file_his WHERE task_id='%1'").arg(sheet_id);



#endif

    lstData.clear();

    m_hisdbOper.QueryBySql(strSql,lstData);

}

#ifdef USER_PRIV_CHECK

bool TermParamWindow::delJudgePriv(int fun_code,QString & strLoginUserName)

{

    int         ret_code;

    bool        flag = false;

    CPrivAccess     my_privaccess;

    char        tmp_str[32];

    int msg_flag=-1;

    //QMessageBox::information(0, QObject::tr("提示"), QObject::tr(g_strLoginUserName.toStdString().c_str()));

#if 1



    char  hostname[32];



    gethostname(hostname, 32);



    string node_name=hostname;

    strcpy(tmp_str, "");

    ret_code =  my_privaccess.HasGivenFunc(strLoginUserName.toStdString().c_str(), 0, tmp_str, tmp_str, node_name.c_str(), 0, fun_code,

                                           0);





    if (ret_code == P_PERMIT)

    {

        flag = true;

    }

    else if (ret_code == P_DB_FAIL)

    {

        if (msg_flag)

        {

            QMessageBox::information(0, QObject::tr("提示"), QObject::tr("无法连接数据库 失败"));

        }



        flag = false;

    }

    else if (ret_code == P_NO_FUNC||ret_code == P_FORBID)

    {

        /*if (msg_flag)

        {

            QMessageBox::information(0, QObject::tr("提示"), QObject::tr("用户不具备该功能"));

        }



       flag = false;

    }

    else if ( ret_code == P_FORBID)

    {*/

        if (msg_flag)

        {



            QMessageBox *mess=new QMessageBox;//(&icon,tr("提示"), tr("用户不具备该功能,是否切换用户?"));

            mess->setWindowTitle(tr("提示"));

            mess->setText(tr("用户不具备该功能,是否切换用户?"));

            QPushButton *okBtn=new QPushButton(tr("确认"));//,QMessageBox::ActionRole));

            QPushButton *cancelBtn=new QPushButton(tr("取消"));//,QMessageBox::RejectRole));

            mess->addButton(okBtn,QMessageBox::ActionRole);

            mess->addButton(cancelBtn,QMessageBox::RejectRole);

            mess->exec();

            if(mess->clickedButton()==cancelBtn)

            {

                flag = false;

            }

            else

            {

                int expire_time = 0;

                QDateTime dtStartExeTime;

                QString userName;

                if (!doLogin(userName, expire_time, dtStartExeTime)){

                    flag=false;

                }

                else

                {





                    if(delJudgePriv(fun_code,userName))

                    {

                        strLoginUserName=userName;

                        flag=true;

                    }

                    else

                    {

                        flag=false;

                    }

                }



            }

        }





    }

    else

    {

        if (msg_flag)

        {

            QMessageBox::information(0, QObject::tr("提示"), QObject::tr("用户权限验证失败 code=%1").arg(ret_code));

        }



        flag = false;

    }

#endif

    return flag;

}



#else

bool TermParamWindow::delJudgePriv(int fun_code,QString & strLoginUserName,bool bLogin)

{

    int         ret_code;

    bool        flag = false;

    CPrivAccess     my_privaccess;

    char        tmp_str[32];

    int msg_flag=-1;

    //QMessageBox::information(0, QObject::tr("提示"), QObject::tr(g_strLoginUserName.toStdString().c_str()));

#if 1



    char  hostname[32];



    gethostname(hostname, 32);



    string node_name=hostname;

    strcpy(tmp_str, "");

    ret_code =  my_privaccess.HasGivenFunc(strLoginUserName.toStdString().c_str(), 0, tmp_str, tmp_str, node_name.c_str(), 0, fun_code,

                                           0);





    if (ret_code == P_PERMIT)

    {

        flag = true;

    }

    else if (ret_code == P_DB_FAIL)

    {

        if (msg_flag)

        {

            QMessageBox::information(0, QObject::tr("提示"), QObject::tr("无法连接数据库 失败"));

        }



        flag = false;

    }

    else if (ret_code == P_NO_FUNC||ret_code == P_FORBID)

    {

        /*if (msg_flag)

        {

            QMessageBox::information(0, QObject::tr("提示"), QObject::tr("用户不具备该功能"));

        }



       flag = false;

    }

    else if ( ret_code == P_FORBID)

    {*/

        if (msg_flag)

        {



            QMessageBox *mess=new QMessageBox;//(&icon,tr("提示"), tr("用户不具备该功能,是否切换用户?"));

            mess->setWindowTitle(tr("提示"));

            if(bLogin)

                mess->setText(tr("用户%1不具备操作功能,是否切换用户?").arg(strLoginUserName));

            else

                mess->setText(tr("用户%1不具备监护功能,是否切换用户?").arg(strLoginUserName));

            QPushButton *okBtn=new QPushButton(tr("确认"));//,QMessageBox::ActionRole));

            QPushButton *cancelBtn=new QPushButton(tr("取消"));//,QMessageBox::RejectRole));

            mess->addButton(okBtn,QMessageBox::ActionRole);

            mess->addButton(cancelBtn,QMessageBox::RejectRole);

            mess->exec();

            if(mess->clickedButton()==cancelBtn)

            {

                flag = false;

            }

            else

            {

                int expire_time = 0;

                QDateTime dtStartExeTime;

                QString userName;

                if (!doLogin(userName, expire_time, dtStartExeTime)){

                    flag=false;

                }

                else

                {





                    if(delJudgePriv(fun_code,userName,bLogin))

                    {

                        flag=true;

                        strLoginUserName=userName;

                    }

                    else

                    {

                        flag=false;

                    }

                }



            }

        }





    }

    else

    {

        if (msg_flag)

        {

            QMessageBox::information(0, QObject::tr("提示"), QObject::tr("用户权限验证失败 code=%1").arg(ret_code));

        }



        flag = false;

    }

#endif

    return flag;

}

#endif

bool TermParamWindow::judgeTermPriv(int fun_code,const QString & strLoginUserName)

{

    int         ret_code;

    bool        flag = false;

    CPrivAccess     my_privaccess;

    char        tmp_str[32];

    int msg_flag=-1;

    //QMessageBox::information(0, QObject::tr("提示"), QObject::tr(g_strLoginUserName.toStdString().c_str()));

    char  hostname[32];



    gethostname(hostname, 32);



    string node_name=hostname;

    strcpy(tmp_str, "");

    ret_code =  my_privaccess.HasGivenFunc(strLoginUserName.toStdString().c_str(), 0, tmp_str, tmp_str, node_name.c_str(), 0, fun_code,

                                           0);





    if (ret_code == P_PERMIT)

    {

        flag = true;

    }

    else if (ret_code == P_DB_FAIL)

    {



        flag = false;

    }

    else if (ret_code == P_NO_FUNC||ret_code == P_FORBID)

    {



        flag=false;

    }

    else

    {





        flag = false;

    }

    return flag;

}

bool TermParamWindow::updateSheetStatus(const QString& sheet_id_str,int status)

{

    QDateTime datetime=QDateTime::currentDateTime();

    QString datetime_str=datetime.toString("yyyy-MM-dd hh:mm:ss");

    QString sql_str=QString("update d5000.dms_param_file_his set execution=%1,finish_time=to_date('%2','yyyy-mm-dd hh24:mi:ss'),user_name='%3' where task_id='%4'").arg(status).arg(datetime_str).arg(g_strLoginUserName).arg(sheet_id_str);

    if(m_hisdbOper.ExecSql(sql_str)>=0)

    {



        sprintf(g_log_buf,"%s ",sql_str.toStdString().c_str());

        cout<<CLogManager::Instance()->WriteLineT(g_log_buf);

        return true;//updateParamStatus(sheet_id_str,status);

    }



    sprintf(g_log_buf,"%s  %s",sql_str.toStdString().c_str(),m_hisdbOper.getLastErrMsg().toStdString().c_str());

    cout<<CLogManager::Instance()->WriteLineT(g_log_buf);

    qDebug()<<"msg=="<<m_hisdbOper.getLastErrMsg();

    return false;

}



bool TermParamWindow::updateParamStatus(const QString &sheet_id_str, int status,const QString& tmp_code_str)

{

    QString code_str=tmp_code_str.toUpper();

    QString sql_str=QString("update d5000.dms_param_content_his  set execution=%1,user_name='%2' where task_id='%3' and param_code='%4'").arg(status).arg(g_strLoginUserName).arg(sheet_id_str).arg(code_str);

    if(m_hisdbOper.ExecSql(sql_str)>=0)

    {



        sprintf(g_log_buf,"%s ",sql_str.toStdString().c_str());

        cout<<CLogManager::Instance()->WriteLineT(g_log_buf);

        return true;

    }



    sprintf(g_log_buf,"%s  %s",sql_str.toStdString().c_str(),m_hisdbOper.getLastErrMsg().toStdString().c_str());

    //sprintf(g_log_buf,"%s \n",sql_str.toStdString().c_str());

    cout<<CLogManager::Instance()->WriteLineT(g_log_buf);

    qDebug()<<"msg=="<<m_hisdbOper.getLastErrMsg();

    return false;

}



void TermParamWindow::writeFile(const QString &path, const QString &content)

{

    qDebug()<<"path======="<<path;

#if 0

    QTextCodec *utf8 = QTextCodec::codecForName("UTF-8");

    QTextCodec* gbk = QTextCodec::codecForName("gbk");

    //gbk -> utf8

    //1. gbk to unicode

    QString strUnicode=gbk->toUnicode(content.toLocal8Bit().data());

    //2. unicode -> utf-8

    QByteArray utf8_bytes=utf8->fromUnicode(strUnicode);

    content=utf8_bytes;



    //1. gbk to unicode

    strUnicode=gbk->toUnicode(path.toLocal8Bit().data());

    //2. unicode -> utf-8

    utf8_bytes=utf8->fromUnicode(strUnicode);

    path=utf8_bytes;

#endif

    QString tmp_content=QString("<file>\n");

    FILE *fp = fopen(path.toStdString().c_str(),"w+");

    if(fp == NULL)

    {

        return ;

    }

    tmp_content+=content;

    tmp_content+=QString("</file>\n");

    fprintf(fp,"%s",content.toStdString().c_str());

    fclose(fp);

    sprintf(g_log_buf,"工单返回文件:%s",path.toStdString().c_str());

    CLogManager::Instance()->WriteLineT(g_log_buf);



#if 1

    map<string,string>::iterator it=m_ip_path_map.begin();

    for(;it!=m_ip_path_map.end();++it)

    {

        char szCmd[1024];

        sprintf(szCmd,"file_send %s %s %s ",path.toStdString().c_str(),it->first.c_str(),it->second.c_str());

        cout<<CLogManager::Instance()->WriteLineT(szCmd);

        system(szCmd);



    }

#endif

}



int TermParamWindow::call_area_thread(const SheetInfo &sheetInfo,const T_Terminal_Para &call_para)

{

    if(m_pThread!=NULL)

    {

        delete m_pThread;

        m_pThread=NULL;

    }

    //激活参数

    short serv          = CH_DMS_TERMINAL_DATA;

    short event         = MT_DMS_CALL_SETTING_AREA;

    m_download_reply_event   = MT_DMS_SETTING_AREA_REPLY;

    m_sheet_result=0;

    m_pThread=new MsgBus_RW_Thread();

    m_pThread->init( sheetInfo.term_id, m_oper_timeout_slice );





    m_pThread->setRunFlag(0);



    Setting_Area    area = { 0 };

    area.comm_fac_id    =sheetInfo.term_id;

    if( m_pThread->init_msg( serv, event, m_download_reply_event, area ) != 0 )

    {

        QMessageBox::warning( this, tr("召唤定值区:"), tr("召唤定值区 初始化失败...") );

        updateSheetStatus(m_cur_sheet_id_str,-1);

        for( int i = 0; i < call_para.terminal_para_seq.size(); i++ )

        {

            Terminal_Para_Info info = call_para.terminal_para_seq[i];

            QString code_str=QString::number(info.data_type,16);

            updateParamStatus(m_cur_sheet_id_str,-1,code_str);



        }

        //初始化失败

        return -1;

    }



    m_pThread->setSheetFlag(true);

    m_pThread->start();

    cout<<"zzzzzzzzzzzzzz"<<endl;

    sheet_exec_dlg_start();





    Setting_Area reply_area;

    while(1)

    {



        usleep(5000);

        cout<<"while!!!!!!!!"<<endl;

        m_sheet_result=m_pThread->getAreaReply(reply_area);

        if(m_sheet_result==0)

            continue;

        else

            break;

    }

    if(m_pThread!=NULL)

    {

        delete m_pThread;

        m_pThread=NULL;

    }

    int ret=m_sheet_result;

    //reply_para=m_reply_para;

    sprintf(g_log_buf,"工单执行召唤定值区返回结果 comm_fac_id=%ld cur_area=%d min_area=%d max_area=%d",

            reply_area.comm_fac_id,reply_area.cur_serial_num,reply_area.min_serial_num,reply_area.max_serial_num);

    cout<<CLogManager::Instance()->WriteLineT(g_log_buf);

    if(ret==-1||ret==-2)

    {



        updateSheetStatus(m_cur_sheet_id_str,-1);

        for( int i = 0; i < call_para.terminal_para_seq.size(); i++ )

        {

            Terminal_Para_Info info = call_para.terminal_para_seq[i];

            QString code_str=QString::number(info.data_type,16);

            updateParamStatus(m_cur_sheet_id_str,-1,code_str);



        }

        sprintf(g_log_buf,"工单定值区号召唤 消息总线注册失败...");

        cout<<CLogManager::Instance()->WriteLineT(g_log_buf);

        QMessageBox::warning( this, tr("定值区号召唤:"), tr("定值区号召唤 消息总线注册失败...") );



    }

    else if(ret==-3)

    {



        updateSheetStatus(m_cur_sheet_id_str,-3);

        for( int i = 0; i < call_para.terminal_para_seq.size(); i++ )

        {

            Terminal_Para_Info info = call_para.terminal_para_seq[i];

            QString code_str=QString::number(info.data_type,16);

            updateParamStatus(m_cur_sheet_id_str,-3,code_str);



        }

        sprintf(g_log_buf,"工单定值区号召唤 消息请求发送失败...");

        cout<<CLogManager::Instance()->WriteLineT(g_log_buf);

        QMessageBox::warning( this, tr("定值区号召唤:"), tr("定值区号召唤 消息请求发送失败...") );

    }

    /* else if(ret==-4)

    {



    }*/

    else if(ret==-5)

    {



        updateSheetStatus(m_cur_sheet_id_str,-5);

        for( int i = 0; i < call_para.terminal_para_seq.size(); i++ )

        {

            Terminal_Para_Info info = call_para.terminal_para_seq[i];

            QString code_str=QString::number(info.data_type,16);

            updateParamStatus(m_cur_sheet_id_str,-5,code_str);



        }

        sprintf(g_log_buf,"工单定值区号召唤 收到的消息长度小于0...");

        cout<<CLogManager::Instance()->WriteLineT(g_log_buf);

        QMessageBox::warning( this, tr("定值区号召唤:"), tr("定值区号召唤 收到的消息长度小于0...") );

    }

    else if(ret==-6)//超时

    {



        updateSheetStatus(m_cur_sheet_id_str,-6);

        for( int i = 0; i < call_para.terminal_para_seq.size(); i++ )

        {

            Terminal_Para_Info info = call_para.terminal_para_seq[i];

            QString code_str=QString::number(info.data_type,16);

            updateParamStatus(m_cur_sheet_id_str,-6,code_str);



        }

        sprintf(g_log_buf,"工单定值区号召唤 超时...");

        cout<<CLogManager::Instance()->WriteLineT(g_log_buf);

        QMessageBox::warning( this, tr("定值区号召唤:"), tr("定值区号召唤 超时...") );



    }

    else if(ret==-7)//中止

    {



        updateSheetStatus(m_cur_sheet_id_str,-7);

        for( int i = 0; i < call_para.terminal_para_seq.size(); i++ )

        {

            Terminal_Para_Info info = call_para.terminal_para_seq[i];

            QString code_str=QString::number(info.data_type,16);

            updateParamStatus(m_cur_sheet_id_str,-7,code_str);



        }

        sprintf(g_log_buf,"工单定值区号召唤 中止...");

        cout<<CLogManager::Instance()->WriteLineT(g_log_buf);

        QMessageBox::warning( this, tr("定值区号召唤:"), tr("定值区号召唤 中止...") );

    }

    else//ret==1

    {

        update_progress_dlg(m_oper_timeout_slice);//接收完成

        SheetStopProgressStep();

        if(reply_area.cur_serial_num!=sheetInfo.area_no)

        {

            QMessageBox::warning( this, tr("定值区号召唤:"), tr("定值区校验失败:定值区不一致 cur_area=%1 sheet_area=%2").arg(reply_area.cur_serial_num).arg(sheetInfo.area_no) );

            updateSheetStatus(m_cur_sheet_id_str,-13);

            for( int i = 0; i < call_para.terminal_para_seq.size(); i++ )

            {

                Terminal_Para_Info info = call_para.terminal_para_seq[i];

                QString code_str=QString::number(info.data_type,16);

                updateParamStatus(m_cur_sheet_id_str,-13,code_str);



            }

            sprintf(g_log_buf,"工单定值区校验失败:定值区不一致 工单定值区=%d 召唤上来的终端定值区=%d",sheetInfo.area_no,reply_area.cur_serial_num);

            cout<<CLogManager::Instance()->WriteLineT(g_log_buf);

            return -13;



        }

        else

        {

            sprintf(g_log_buf,"工单定值区校验成功:工单定值区=%d 召唤上来的终端定值区=%d",sheetInfo.area_no,reply_area.cur_serial_num);

            cout<<CLogManager::Instance()->WriteLineT(g_log_buf);

        }





        return 1;



    }

    return  ret;

}

void TermParamWindow::initMsg()

{

    m_WarnMsg = new APP_TO_WARN_SERVICE_MESSAGE_STRU;

    m_pMessageBus = new message_invocation;

    int ret_code = m_pMessageBus->messageInit(AC_REALTIME_NAME,AF_SCADA_NAME,"term_param");

    DllCreateAlarmOp(&m_alarm_op);

}

long TermParamWindow::GetNodeIdByMachineName()

{

    int         retcode;

    int         rec_num;

    long        node_id;

    CBuffer     buf_base;



    char hostname[40];

    gethostname(hostname, 40);



    const char  *field_name = "node_id,node_name";

    struct node_info_buf_stru

    {

        long node_id;

        char node_name[40];

    };

    node_info_buf_stru *node_info_buf;

    CTableOp tab_op;

    retcode = tab_op.Open(AP_PUBLIC, TABLE_NO_MNG_NODE_INFO);



    if (retcode != DB_OK)

    {

        return  -1;

    }



    retcode = tab_op.TableGet(field_name, buf_base);



    if (retcode < 0)

    {

        return  -1;

    }



    rec_num = buf_base.GetLength() / sizeof(node_info_buf_stru);

    node_info_buf = (node_info_buf_stru *)buf_base.GetBufPtr();



    for (int k = 0; k < rec_num; k++)

    {

        if (!strcmp(node_info_buf[k].node_name, hostname))

        {

            node_id = node_info_buf[k].node_id;

            break;

        }

    }



    TRACE("node_id =========== %ld\n", node_id);

    // TRACE("user_id =========== %d\n", g_user_id);

    TRACE("host name================%s\n",hostname);

    return node_id;

}

int   TermParamWindow::GetUserIdByUserName(const char* username,int &user_id)

{

    int  ret_code;

    CTableNet odbapi;

    ret_code = odbapi.SetAppNo(AP_PUBLIC );

    if (ret_code<0)

    {

        TRACE("SetAppNo Error! \n " );

        return -1;

    }

    if(odbapi.Open(AP_PUBLIC ,63)<0)

    {

        TRACE("open user_info is error \n");

        return -1;

    }



    char *m_buf;

    int buf_size;

    char  sql_str[100];

    sprintf(sql_str,"select  user_id from sys_priv_user_d_def where user_name=\'%s\' ",username);

    m_buf=NULL;

    buf_size=0;

    CBuffer tmpBuffer;

    ret_code=odbapi.SqlGet(sql_str,tmpBuffer);

    m_buf    = tmpBuffer.GetBufPtr();

    buf_size = tmpBuffer.GetLength();

    TRACE("ret_code is %d\n",ret_code );

    if (ret_code<0  ||m_buf==NULL)

    {

        TRACE("SqlGet Error! \n " );

        return -1;

    }

    memcpy(&user_id,m_buf,sizeof(int));

    TRACE("user_name  %s,user_id %d\n",username,user_id);

    return 1;

}

void GetTimeStr(string &time_string)

{

    time_t      ltime;

    ltime=time(NULL);

    char time_str[256];

    struct tm *tm1;

    tm1 = localtime((time_t *)&ltime);



    sprintf( time_str,"%d年%02d月%02d日%02d时%02d分%02d秒",(1900+tm1->tm_year),(tm1->tm_mon+1),tm1->tm_mday,tm1->tm_hour,tm1->tm_min,tm1->tm_sec);

    time_string=time_str;

    // cout<<"time_str===="<<time_string<<endl;

}

bool TermParamWindow::sendWarn(const TWarnData& data)

{

    long node_id=GetNodeIdByMachineName();

    char hostname[40];

    gethostname(hostname, 40);





    string warn_type_str;

    char warn_str[512]={0};

    time_t now_time = time(NULL);

    string cur_time;

    GetTimeStr(cur_time);

    QString feeder_name=m_rtdbOper.GetFeederDeviceName(data.feeder_id);

    //QString combined_name=m_rtdbOper.GetCombinedDeviceName(data.combined_id);

    QString term_name=m_rtdbOper.GetTerminalInfoName(data.term_id);

    cout<<"feeder_name====="<<feeder_name.toStdString()<<" "<<data.feeder_id<<endl;

    if(data.warn_type==MENU_OPT_DEV_STAT_SHEET_EXEC)

    {

        warn_type_str="操作工单执行";

        sprintf(warn_str,"%s  %s用户(%s监护用户)在%s机器上操作工单%s %s  %ld  %s %s",cur_time.c_str(),g_strLoginUserName.toStdString().c_str(),m_strCustodyName.toStdString().c_str(),hostname,data.sheet_id_str.c_str(),data.op_str.c_str(),data.term_id,feeder_name.toStdString().c_str()/*,combined_name.toStdString().c_str()*/,term_name.toStdString().c_str());



    }

    else if(data.warn_type==MENU_OPT_PARA_COMPARE)

    {

        warn_type_str="操作参数下装激活";

        sprintf(warn_str,"%s  %s用户在%s机器上执行%s  %ld  %s %s",cur_time.c_str(),g_strLoginUserName.toStdString().c_str(),hostname,data.op_str.c_str(),data.term_id,feeder_name.toStdString().c_str()/*,combined_name.toStdString().c_str()*/,term_name.toStdString().c_str());



    }

    else

    {

        warn_type_str="操作参数下装激活";

        // sprintf(warn_str,"%s  %s用户(%s监护用户)在%s机器上执行%s  %ld  %s %s",cur_time.c_str(),g_strLoginUserName.toStdString().c_str(),m_strCustodyName.toStdString().c_str(),hostname,data.op_str.c_str(),data.term_id,feeder_name.toStdString().c_str()/*,combined_name.toStdString().c_str()*/,term_name.toStdString().c_str());

        sprintf(warn_str,"%s  %s用户(%s监护用户)在%s机器上执行%s  %s  %s %s",cur_time.c_str(),g_strLoginUserName.toStdString().c_str(),m_strCustodyName.toStdString().c_str(),hostname,data.op_str.c_str(),data.dev_name.toStdString().c_str(),feeder_name.toStdString().c_str()/*,combined_name.toStdString().c_str()*/,term_name.toStdString().c_str());



    }







    //   sprintf(g_log_buf,"激活失败 cur_code=%d cur_value=%s  code=%d value=%s    code===%s\n",curinfo.data_type,cur_value_str.toStdString().c_str(),info.data_type,info_value_str.toStdString().c_str(),code_str.toStdString().c_str());

    cout<<CLogManager::Instance()->WriteLineT(warn_str);



    int user_id=0;

    GetUserIdByUserName(g_strLoginUserName.toStdString().c_str(),user_id);

    cout<<"warn_str==============="<<warn_str<<endl;

    m_WarnMsg->warn_num = 1;

    m_WarnMsg->seq_warn_message.length(1);

    m_WarnMsg->seq_warn_message[0].warn_type = DMS_OP_DEV_WARN_TYPE;

    m_WarnMsg->seq_warn_message[0].app_no  = AP_DMS_SCADA;

    m_WarnMsg->seq_warn_message[0].node_id =0/*user_id*/;

    m_WarnMsg->seq_warn_message[0].op_type  = 0;

    m_WarnMsg->seq_warn_message[0].is_restrain  = 0;

    m_WarnMsg->seq_warn_message[0].sound_table_key_field_order_no =-1;

    m_WarnMsg->seq_warn_message[0].reservered_1 = 0;

    m_WarnMsg->seq_warn_message[0].reservered_2 = 0;



    m_WarnMsg->seq_warn_message[0].seq_field_info.length(14);

    m_WarnMsg->seq_warn_message[0].seq_field_info[0].c_time(now_time);//时间

    m_WarnMsg->seq_warn_message[0].seq_field_info[1].c_long(data.term_id);//设备id

    m_WarnMsg->seq_warn_message[0].seq_field_info[2].c_long(0);//bv_id

    //m_WarnMsg->seq_warn_message[0].seq_field_info[3].c_long(st_id);//厂站id

    m_WarnMsg->seq_warn_message[0].seq_field_info[3].c_long(data.feeder_id);//馈线id

    m_WarnMsg->seq_warn_message[0].seq_field_info[4].c_long(0/*data.combined_id*/);//开关站ID

    m_WarnMsg->seq_warn_message[0].seq_field_info[5].c_long(0);//区域ID

    m_WarnMsg->seq_warn_message[0].seq_field_info[6].c_long(user_id);//量测类型

    m_WarnMsg->seq_warn_message[0].seq_field_info[7].c_long(0);//量测类型

    m_WarnMsg->seq_warn_message[0].seq_field_info[8].c_int(0);//抑制类型

    m_WarnMsg->seq_warn_message[0].seq_field_info[9].c_int(0);//量测值

    m_WarnMsg->seq_warn_message[0].seq_field_info[10].c_int(data.warn_type);//状态

    m_WarnMsg->seq_warn_message[0].seq_field_info[11].c_string(warn_str);//告警内容

    m_WarnMsg->seq_warn_message[0].seq_field_info[12].c_int(0);//备用1

    m_WarnMsg->seq_warn_message[0].seq_field_info[13].c_int(0);//备用2



    int buf_size = sizeof(APP_TO_WARN_SERVICE_MESSAGE_STRU);

    int ret = m_alarm_op->SendAlarmToServer(m_pMessageBus, *m_WarnMsg, buf_size);

    qDebug() << "++++++++++++++++ret" << ret;

    if(ret <= 0){



        sprintf(g_log_buf,"告警发送失败 %s",warn_str);

        cout<<CLogManager::Instance()->WriteLineT(g_log_buf);

        //qDebug() << QString::fromLocal8Bit("%1_告警发送失败").arg(m_dev_id);

        //cout<<"告警发送失败:dev_id="<<dev_id<<endl;

        // sprintf(g_log_szbuf,"告警发送失败:dev_id=%ld\n",dev_id);



    }

    //spri"ntf(g_log_szbuf,"告警发送成功:dev_id=%ld  warn_str==%s\n",dev_id,warn_str);

    cout<<"ret================"<<ret<<endl;

}



void TermParamWindow::on_pushBtn_check_clicked(const QSet<int> &code_set)

{

    QString strOper = tr("校验参数");



    if( m_term_dev_id == 0 || m_term_xh_id == 0 )

    {

       // ZMessageBox::warning( this, strOper, tr("无效的终端设备或未设置有效的终端型号...") );

        return;

    }

    if(m_pThread!=NULL)

    {

        delete m_pThread;

        m_pThread=NULL;

    }



    T_Terminal_Para check_para;

    init_term_check_para(code_set, check_para );

    QString str_name=get_cb_name();

    if( check_para.num == 0 )

    {

        //ZMessageBox::warning( this, strOper, tr("请勾选需要召唤的参数记录...") );

        return;

    }

    //激活参数

    short serv          = CH_DMS_TERMINAL_DATA;

    short event         = MT_DMS_TERM_PARA_CALL;;

    m_download_reply_event   = MT_DMS_TERM_PARA_REPLY;

    m_sheet_result=0;

    m_oper_timeout_slice    = 45;   //操作超时间隔(秒)

    m_pThread=new MsgBus_RW_Thread();

    m_pThread->init( m_term_dev_id, m_oper_timeout_slice );



    m_pThread->setRunFlag(0);

    if( m_pThread->init_msg( serv, event, m_download_reply_event, check_para ) != 0 )

    {

        sprintf(g_log_buf,"校验参数 初始化失败...");

        CLogManager::Instance()->WriteLineT(g_log_buf);

       // QMessageBox::warning( this, tr("校验参数:"), tr("校验参数 初始化失败...") );

        //初始化失败

        return ;

    }

    m_pThread->setSheetFlag(true);



    m_pThread->start();

    cout<<"zzzzzzzzzzzzzz"<<endl;

    //set_check_result_state(false);

    sheet_exec_dlg_start();

   // set_check_result_state(true);

    T_Terminal_Para reply_para;

    while(1)

    {



        usleep(5000);

        cout<<"while!!!!!!!!"<<endl;

        m_sheet_result=m_pThread->getParamReply(reply_para);

        if(m_sheet_result==0)

            continue;

        else

            break;

    }

    if(m_pThread!=NULL)

    {

        delete m_pThread;

        m_pThread=NULL;

    }

    int ret=m_sheet_result;

   // set_result_state(true);

    QMap<qint32,Terminal_Para_Info> recv_code_map;

    CLogManager::Instance()->WriteLineT("校验参数返回结果:");

    for( int i = 0; i < reply_para.terminal_para_seq.size(); i++ )

    {

        Terminal_Para_Info pi = reply_para.terminal_para_seq[i];

        sprintf(g_log_buf,"comm_fac_id=%ld line_no=%d area_no=%d para_type=%d value_type=%d data_type=%d value_f=%f value_i=%d result=%d ",

                pi.comm_fac_id,pi.line_no,pi.serial_num,pi.para_type,pi.value_type,pi.data_type,pi.value_f,pi.value_i,pi.result);

        CLogManager::Instance()->WriteLineT(g_log_buf);

        //  CLogManager::Instance()->WriteLineT(QString::fromUtf8(pi.value_s).toStdString());

    }

    if(ret==-1||ret==-2)

    {

        sprintf(g_log_buf,"校验参数 消息总线注册失败...");

        CLogManager::Instance()->WriteLineT(g_log_buf);

      //  QMessageBox::warning( this, tr("校验参数:"), tr("校验参数 消息总线注册失败...") );



    }

    else if(ret==-3)

    {

        sprintf(g_log_buf,"校验参数 消息请求发送失败...");

        CLogManager::Instance()->WriteLineT(g_log_buf);

       // QMessageBox::warning( this, tr("校验参数:"), tr("校验参数 消息请求发送失败...") );



    }

    /* else if(ret==-4)

    {



    }*/

    else if(ret==-5)

    {

        sprintf(g_log_buf,"校验参数 收到的消息长度小于0...");

        CLogManager::Instance()->WriteLineT(g_log_buf);

       // QMessageBox::warning( this, tr("校验参数:"), tr("校验参数 收到的消息长度小于0...") );



    }

    else if(ret==-6)//超时

    {



        cout<<"reply_para  size=="<<reply_para.terminal_para_seq.size()<<endl;

        for( int i = 0; i < reply_para.terminal_para_seq.size(); i++ )

        {

            Terminal_Para_Info &info = reply_para.terminal_para_seq[i];



            recv_code_map[info.data_type]=info;

            // cout<<"recv_code=="<<info.data_type<<endl;

            show_check_para_info( info );



            //insertParamHisByPara(info,4,1,m_write_type==2?1:99);

            // update_para_info( info );



            //update_oper_result( info.data_type, 10/*info.result*/ );  //召唤的返回结果不可用

        }

        // update_reply_check_para( reply_para );  //更新进商用数据库

        for( int i = 0; i < check_para.terminal_para_seq.size(); i++ )

        {

            Terminal_Para_Info &info = check_para.terminal_para_seq[i];

            if(recv_code_map.find(info.data_type)==recv_code_map.end())

            {

                //cout<<"check data_type=="<<info.data_type<<" size=="<<recv_code.size()<<endl;

                update_oper_check_result( info.data_type, 103);  //召唤的返回结果不可用

                insertParamHisByPara(info,4,-1,m_write_type==2?1:99);

                if(m_write_type==1)

                {

                    updateSysParamStatus(info.data_type,1);

                }

            }



        }

        sprintf(g_log_buf,"校验参数 超时...");

        CLogManager::Instance()->WriteLineT(g_log_buf);

      //  QMessageBox::warning( this, tr("校验参数:"), tr("校验参数 超时...") );

    }

    else if(ret==-7)//中止

    {



        //QSet<qint32> recv_code;

        for( int i = 0; i < reply_para.terminal_para_seq.size(); i++ )

        {

            Terminal_Para_Info &info = reply_para.terminal_para_seq[i];



            recv_code_map[info.data_type]=info;

            show_check_para_info( info );



            // insertParamHisByPara(info,4,1,m_write_type==2?1:99);



        }





        for( int i = 0; i < check_para.terminal_para_seq.size(); i++ )

        {

            Terminal_Para_Info &info = check_para.terminal_para_seq[i];

            if(recv_code_map.find(info.data_type)==recv_code_map.end())

            {

                update_oper_check_result( info.data_type, 106/*info.result*/ );  //召唤的返回结果不可用

                insertParamHisByPara(info,4,-1,m_write_type==2?1:99);

                if(m_write_type==1)

                {

                    updateSysParamStatus(info.data_type,1);

                }

            }

        }



        sprintf(g_log_buf,"校验参数 中止...");

        CLogManager::Instance()->WriteLineT(g_log_buf);

        m_bContinue=false;

        return;

       // QMessageBox::warning( this, tr("校验参数:"), tr("校验参数 中止...") );



    }

    else//ret==1

    {



        update_progress_dlg(m_oper_timeout_slice);//接收完成

        SheetStopProgressStep();

        cout<<"reply_size=="<<reply_para.terminal_para_seq.size()<<endl;

        // QSet<qint32> recv_code;

        for( int i = 0; i < reply_para.terminal_para_seq.size(); i++ )

        {

            Terminal_Para_Info &info = reply_para.terminal_para_seq[i];



            recv_code_map[info.data_type]=info;

            show_check_para_info( info );

            //update_oper_result( info.data_type, 10/*info.result*/ );  //召唤的返回结果不可用

        }



    }

    update_reply_check_para( reply_para );  //更新进商用数据库

#if 1

    QList< QStandardItemModel * >   lstModel;

    /*lstModel.append( (QStandardItemModel *)ui->tableView_Para0->model() );

       lstModel.append( (QStandardItemModel *)ui->tableView_Para1->model() );

       lstModel.append( (QStandardItemModel *)ui->tableView_Para2->model() );*/

    QMap<qint32,QTableView* >::iterator it=m_paramType_widget_map.begin();

    for(;it!=m_paramType_widget_map.end();++it)

    {

        lstModel.append((QStandardItemModel *)it.value()->model());

    }







    QStandardItemModel *pModel;

    foreach( pModel, lstModel )

    {

        if( pModel == NULL ) continue;



        for( int row = 0; row < pModel->rowCount(); row++ )

        {

            QStandardItem *pNameItem = pModel->item( row, PARA_COL_NAME );

            if( pNameItem == NULL ) continue;



            int code = pNameItem->data( Qt::UserRole+10 ).toInt();

            if(recv_code_map.find(code)==recv_code_map.end())

                continue;

            if( pNameItem->checkState()==Qt::Checked)

            {

                QStandardItem *pValueItem = pModel->item( row, m_write_type==2?PARA_COL_VALUE:PARA_COL_ZD_SYS_VALUE );

                if( pValueItem == NULL )

                {

                    continue;

                }



                QStandardItem *pCheckValueItem = pModel->item( row, PARA_COL_CHECK_VALUE );

                if( pCheckValueItem == NULL )

                {

                    continue;

                }

                QString value_str=pValueItem->text().trimmed();

                QString check_value_str=pCheckValueItem->text().trimmed();

                QStandardItem *pCheckResultItem = pModel->item( row, PARA_COL_CHECK_RESULT );



                TWarnData data;

                DMS_TERMINAL_INFO_STRUCT struDmsTerminalInfo;

                m_rtdbOper.ReadTerminalInfo(struDmsTerminalInfo,m_term_dev_id);

                //data.combined_id=struDmsTerminalInfo.feeder_id;

                data.term_id=struDmsTerminalInfo.id;

                data.warn_type=MENU_OPT_PARA_CHECK;

                data.feeder_id=struDmsTerminalInfo.feeder_id;

                data.op_str=m_write_type==1?"整定系统":"";

                data.op_str+="校验参数 ";



                data.dev_name=str_name;





                Terminal_Para_Info info=recv_code_map[code];

                if(value_str!=check_value_str)

                {



                    insertParamHisByPara(info,4,-1,m_write_type==2?1:99);

                    pCheckResultItem->setText(tr("校验失败"));

                    sprintf(g_log_buf,"校验参数失败 参数名称:%s 信息体:%d 下装激活值:%s 本次召唤上来的值:%s",

                            pNameItem->text().toStdString().c_str(),code,value_str.toStdString().c_str(),check_value_str.toStdString().c_str());

                    CLogManager::Instance()->WriteLineT(g_log_buf);

                    data.op_str+=g_log_buf;

                    sendWarn(data);

                    if(m_write_type==1)

                    {

                        updateSysParamStatus(info.data_type,1);

                    }

                    //QMessageBox::warning( this, tr("校验参数:"), tr("校验失败...") );

                }

                else

                {

                    insertParamHisByPara(info,4,1,m_write_type==2?1:99);

                    pCheckResultItem->setText(tr("校验成功"));

                    sprintf(g_log_buf,"校验参数成功 参数名称:%s 信息体:%d 下装激活值:%s 本次召唤上来的值:%s",

                            pNameItem->text().toStdString().c_str(),code,value_str.toStdString().c_str(),check_value_str.toStdString().c_str());

                    CLogManager::Instance()->WriteLineT(g_log_buf);

                    data.op_str+=g_log_buf;

                    sendWarn(data);

                    if(m_write_type==1)

                    {

                        updateSysParamStatus(info.data_type,3);

                    }

                    // QMessageBox::warning( this, tr("校验参数:"), tr("校验成功...") );

                }



            }

        }

    }

#endif

    return;

}

QString TermParamWindow::getParamTypeStr(qint32 type)

{

    /*switch(type)

    {

    case 0:  return QString("设备参数");

    case 1:return QString("保护控制");

    case 2:return QString("保护定值");

    case 3:return QString("保护软压板");

    default:

        break;

    };*/

    static QMap<qint32,QString> actVal_menuName_map;

    if(actVal_menuName_map.count()>0)

    {

        if(actVal_menuName_map.find(type)==actVal_menuName_map.end())

        {

            return "未识别";

        }

        else

        {

            return actVal_menuName_map[type];

        }

    }

    QListSysMenuInfo lstSysMenuInfo;

    m_rtdbOper.GetSysMenuInfo( lstSysMenuInfo, tr("终端参数类别") );

    SYS_MENU_INFO_STRUCT sub_menu;

    foreach( sub_menu, lstSysMenuInfo )

    {

        actVal_menuName_map[sub_menu.actual_value]=sub_menu.display_value;

    }

    if(actVal_menuName_map.find(type)==actVal_menuName_map.end())

    {

        return "未识别";

    }

    else

    {

        return actVal_menuName_map[type];

    }



}

QTableView *TermParamWindow::getWidgetByParamType(qint32 type)

{

    if(m_paramType_widget_map.find(type)==m_paramType_widget_map.end())

        return NULL;

    return m_paramType_widget_map[type];

}

void TermParamWindow::updateWidget(QTableView *pTabView)

{

    pTabView->resizeColumnsToContents();

    pTabView->update();

}



void TermParamWindow::on_pushBtn_SaveFile_clicked()

{

    //readConf();

    QWidget * pCurPage = ui->tabWidget->currentWidget();



    QStandardItemModel *pModel = NULL;





    QString content;

    QString file_name;

    QString term_name=ui->lineEdit_TermName->text();

    QString tm_str=QDateTime::currentDateTime().toString("yyyy-MM-dd hh:mm:ss:zzz");

    content=QString("Time:%1\n").arg(tm_str);

    content+=QString("<Term_param>\n");



    QMap<qint32,QTableView* >::iterator it=m_paramType_widget_map.begin();

    for(;it!=m_paramType_widget_map.end();++it)

    {

        // lstModel.append((QStandardItemModel *)it.value()->model());

        QTableView  *tabView=it.value();

        if(tabView->parentWidget()==pCurPage)

        {

            pModel=(QStandardItemModel *)it.value()->model();

            term_name+=QString("_%1.csv").arg(getParamTypeStr(it.key()));

            content+=QString("#参数名称\t,代码\t,召唤值\t,参数单位\t,参数范围\t,步长\t,是否允许修改\t,召测时间\t,操作结果\t,整定系统参数值\t,修改时间\t,整定系统比对结果\t,校验值\t,校验结果\n");

        }

    }





    if(pModel==NULL)

        return;



    cout<<"cout==================="<<pModel->rowCount()<<endl;

    for(int row=0;row<pModel->rowCount();++row)

    {

        content+=QString("@");

        for(int col=0;col<pModel->columnCount();++col)

        {

            QModelIndex index=pModel->index(row,col);

            QString data=pModel->data(index).toString();

            content+=data;

            content+=QString("\t,");

        }

        content+=QString("\n");



    }

    /* for(int row=0;row<ui->tableView_Para1->model()->rowCount();++row)

    {

        content+=QString("@\r\t");

        for(int col=0;col<ui->tableView_Para0->model()->columnCount();++col)

        {

            QModelIndex index=ui->tableView_Para0->model()->index(row,col);

            QString data=ui->tableView_Para0->model()->data(index).toString();

            content+=data;

            content+=QString("\r\t");

        }

      content+=QString("\r\n");

    }

    for(int row=0;row<ui->tableView_Para2->model()->rowCount();++row)

    {

        for(int col=0;col<ui->tableView_Para0->model()->columnCount();++col)

        {

            QModelIndex index=ui->tableView_Para0->model()->index(row,col);

            QString data=ui->tableView_Para0->model()->data(index).toString();

            content+=data;

            content+=QString("\r\t");

        }

         content+=QString("\r\n");

    }*/

    content+=QString("</Term_param>");

    QMessageBox::information(this,tr("通知:"),tr("生成成功!!!"));

    file_name=m_create_file_path+term_name;

    createFile(file_name,content);



}

bool TermParamWindow::GetCbDevice(DMS_CB_DEVICE_STRUCT & cb_rec,const qint64 &cb_id,const qint32 index)

{

    qint32 app_no;

    if(index==1)

    {

        app_no=6510000;

    }

    else

    {

        app_no=6500000;

    }

    CTableNet tabNet;

    struct tmp_data_stru

    {

        long id;

        char name[64];

        int line_no;

    };

    tmp_data_stru tmp_data;

    int ret=tabNet.Open(app_no,13502);

    if(ret<0)

    {

        cout<<"Open 13502 app_no="<<app_no<<" is error ret=="<<ret<<endl;;

        return false;

    }

    ret=tabNet.TableGetByKey((char*)&cb_id,"id,name,line_no",(char*)&tmp_data,sizeof(tmp_data_stru));

    if(ret<0)

    {

        return false;

    }

    cb_rec.id=tmp_data.id;

    cb_rec.line_no=tmp_data.line_no;

    strcpy(cb_rec.name,tmp_data.name);

    return true;

}

void TermParamWindow::exec_slot(const QSet<int> &code_set,const int &exec_type)

{



    T_Terminal_Para act_para;

    QList<TWarnData>  warn_data_lst;

    if(!init_term_act_para(code_set,act_para,warn_data_lst))

    {

        return;

    }

    if(exec_type==ACT_EXEC)

    {

        on_pushBtn_Act_clicked(code_set,act_para,warn_data_lst);

    }

    else if(exec_type==ACT_CANCEL)

    {

        for(int zz=0;zz<warn_data_lst.count();++zz)

        {



            TWarnData warn_data=warn_data_lst[zz];

            warn_data.warn_type=MENU_OPT_PARA_CANCEL;

            QString op_str=warn_data.op_str.c_str();

            op_str.replace("[]",tr("取消激活参数"));

            warn_data.op_str=op_str.toStdString();

            sendWarn(warn_data);

        }

        for( int i = 0; i < act_para.terminal_para_seq.size(); i++ )

        {

            Terminal_Para_Info info = act_para.terminal_para_seq[i];

            insertParamHisByPara(info,5,-1,m_write_type==2?1:99);

            if(m_write_type==1)

            {

                updateSysParamStatus(info.data_type,1);

            }

        }

        sprintf(g_log_buf,"取消激活参数...");

        CLogManager::Instance()->WriteLineT(g_log_buf);





        QMessageBox::warning( this, tr("激活参数:"), tr("取消激活参数...") );

#if 0

        TWarnData data;

        DMS_TERMINAL_INFO_STRUCT struDmsTerminalInfo;

        m_rtdbOper.ReadTerminalInfo(struDmsTerminalInfo,m_term_dev_id);

        //data.combined_id=struDmsTerminalInfo.feeder_id;

        data.term_id=struDmsTerminalInfo.id;

        data.warn_type=MENU_OPT_PARA_CANCEL;

        data.feeder_id=struDmsTerminalInfo.feeder_id;

        data.op_str="激活参数";

        data.op_str+="取消激活参数";

        sendWarn(data);

#endif





    }

    else if(exec_type==ACT_TIMEOUT)

    {

        for(int zz=0;zz<warn_data_lst.count();++zz)

        {



            TWarnData warn_data=warn_data_lst[zz];

            warn_data.warn_type=MENU_OPT_PARA_TIMEOUT;

            QString op_str=warn_data.op_str.c_str();

            op_str.replace("[]",tr("激活参数超时"));

            warn_data.op_str=op_str.toStdString();

            sendWarn(warn_data);

        }

        for( int i = 0; i < act_para.terminal_para_seq.size(); i++ )

        {

            Terminal_Para_Info info = act_para.terminal_para_seq[i];

            insertParamHisByPara(info,6,-1,m_write_type==2?1:99);

            if(m_write_type==1)

            {

                updateSysParamStatus(info.data_type,1);

            }

        }

        sprintf(g_log_buf,"激活参数超时...");

        CLogManager::Instance()->WriteLineT(g_log_buf);

        QMessageBox::warning( this, tr("激活参数:"), tr("激活参数超时...") );

#if 0

        TWarnData data;

        DMS_TERMINAL_INFO_STRUCT struDmsTerminalInfo;

        m_rtdbOper.ReadTerminalInfo(struDmsTerminalInfo,m_term_dev_id);

        //data.combined_id=struDmsTerminalInfo.feeder_id;

        data.term_id=struDmsTerminalInfo.id;

        data.warn_type=MENU_OPT_PARA_TIMEOUT;

        data.feeder_id=struDmsTerminalInfo.feeder_id;

        data.op_str="激活参数";

        data.op_str+="激活参数超时";

        sendWarn(data);

#endif



    }



}

bool TermParamWindow::getFeederInfo(QListFeederDevice &lstFeederDevice)

{

    QList<QVariantList> lstData;

#ifdef SUPPORT_KINGBASES

    QString strSql=QString("select id,name,st_id,area_id from d5000.dms_feeder_device@emslink");

#else

    QString strSql=QString("select id,name,st_id,area_id from d5000.dms_feeder_device");



#endif

    if(!m_hisdbOper.QueryBySql(strSql,lstData))

    {

        return false;

    }

    for(int ct=0;ct<lstData.count();++ct)

    {

        int col=0;

        DMS_FEEDER_DEVICE_STRUCT feeder_info;

        feeder_info.id=lstData[ct][col++].toLongLong();

        strcpy(feeder_info.name,lstData[ct][col++].toString().toStdString().c_str());

        feeder_info.st_id=lstData[ct][col++].toLongLong();

        feeder_info.area_id=lstData[ct][col++].toLongLong();

        lstFeederDevice.push_back(feeder_info);



    }

    return true;

}



void TermParamWindow::setWidText(const QString &user_name)

{

    QString strTitle = tr("终端参数调阅");



#ifdef _USE_MSG_INV

    strTitle += tr(" - MsgInv");

#else

    strTitle += tr(" - MsgProxy");

#endif



#ifdef WIN_TEXT

    strTitle += tr(" - 南网");

#else

    strTitle += tr(" - 南网");

#endif





    //add by lxf for ningxia

    strTitle+="(操作用户:"+g_strLoginUserName+" 监护用户:"+m_strCustodyName+")";

    //add end

    this->setWindowTitle( strTitle );

}



void TermParamWindow::on_pushBtn_fixedSearch_clicked()

{

    MyWaitCursor mwc;



    TLocate &locate = m_FixedLocate.m_locate;



    void *userData = locate.UserData();

    if( userData != NULL )

    {

        locate.IncreaseIndex();



        //ui->tableWidget_Term->clearSelection();



        QStandardItem *pItem = (QStandardItem *)userData;

        if( pItem != NULL )

        {

            //pItem->setSelected( true );



            QItemSelectionModel::SelectionFlags command = QItemSelectionModel::Rows | QItemSelectionModel::ClearAndSelect;

            QList< QStandardItemModel * >   lstModel;



            QMap<qint32,QTableView* >::iterator it=m_paramType_widget_map.begin();

            for(;it!=m_paramType_widget_map.end();++it)

            {

                QTableView* pTableView=it.value();

                QStandardItemModel *pModel=(QStandardItemModel *)it.value()->model();



                if( pModel == NULL ) continue;



                for( int row = 0; row < pModel->rowCount(); row++ )

                {

                    QStandardItem *pNameItem = pModel->item( row, PARA_COL_NAME );

                    if( pItem == NULL ) continue;

                    if(pNameItem==pItem)

                    {

                        pTableView->setCurrentIndex(pNameItem->index());

                        ui->tabWidget->setCurrentWidget(pTableView->parentWidget());

                        return;



                    }

                    // ui->tabWidget->setCurrentIndex(it.key());



                }

            }

        }

    }





}



void TermParamWindow::on_lineEdit_fixedName_textChanged(const QString &arg1)

{

    MyWaitCursor mwc;



    QString strLocate = ui->lineEdit_fixedName->text().toLower();

    if( strLocate.isEmpty() || strLocate.length() == 0 ) return;



    TLocate &locate = m_FixedLocate.m_locate;



    locate.Clear();



    SMatchItem item;

    foreach( item, m_FixedLocate.m_lstCache )

    {

        QString strHzCode = QString::fromAscii( item.hz_code ).toLower();

        QString strPyCode = QString::fromAscii( item.py_code ).toLower();



        int idx_hz = strHzCode.indexOf( strLocate );

        int idx_py = strPyCode.indexOf( strLocate );



        if( idx_hz >= 0 || idx_py >= 0 )

        {

            if( idx_hz < 0 ) idx_hz = 0x7fffffff;

            if( idx_py < 0 ) idx_py = 0x7fffffff;



            SMatchItem mi( item );

            mi.index = ( ( idx_hz < idx_py ) ? idx_hz : idx_py );



            locate.Append( mi );

        }

    }



    locate.Sort();



    locate.ResetIndex();

}



void TermParamWindow::on_pushButton_TreeGo_mul_clicked()

{

    MyWaitCursor mwc;



    //m_pDevCtrl->locateDev( 0, ui->lineEdit_TreeLocate->text().toLower() );

    //return;



    TLocate &locate = m_TreeDevLocate_mul.m_locate;



    void *userData = locate.UserData();

    if( userData != NULL )

    {

        locate.IncreaseIndex();



        QTreeWidgetItem *pItem = (QTreeWidgetItem *)userData;

        pItem->setSelected( true );



        ui->treeWidget_mul->setCurrentItem( pItem );

        ui->treeWidget_mul->scrollToItem( pItem );

    }

}



void TermParamWindow::on_pushBtn_CallArea_mul_clicked()

{

    clear_callArea();

    ui->comboBox_status->setEnabled(true);

    ui->comboBox_param->setEnabled(true);

    ui->tableView_term_mul->setEnabled(true);

    QStandardItemModel *pModel = NULL;



    pModel = (QStandardItemModel *)ui->tableView_term_mul->model();

    if( pModel == NULL ) return;





    for( int row = 0; row < pModel->rowCount(); row++ )

    {

        QStandardItem *pItemName = pModel->item( row, MUL_TERM_COL_NAME );

        if( pItemName == NULL ) continue;



        pItemName->setData(QVariant(0),Qt::UserRole+14);

    }



    for( int row = 0; row < pModel->rowCount(); row++ )

    {





        QStandardItem *pItemName = pModel->item( row, MUL_TERM_COL_NAME );

        if( pItemName == NULL ) continue;

        qint64 tmp_term_id=pItemName->data(Qt::UserRole+10).toLongLong();

        qint32 area_status=pItemName->data(Qt::UserRole+14).toInt();

        if(pItemName->checkState()==Qt::Checked&&area_status==0)/*未召唤过终端定值区*/

        {

            qint32 status=pItemName->data(Qt::UserRole+11).toInt();

            if(status==1)

            {

                callArea_mul(tmp_term_id);

            }

            else

            {

                ZMessageBox::warning( this, tr("警告:"), tr("未投入终端不能进行操作 终端名称=%1...").arg(pItemName->text()) );

                return;

            }

        }



    }



}

void TermParamWindow::callArea_mul(const qint64 & term_id)

{

    QString strOper = tr("批量终端召唤定值区号");











    if(m_pThread!=NULL)

    {

        delete m_pThread;

        m_pThread=NULL;

    }

    //激活参数

    short serv          = CH_DMS_TERMINAL_DATA;

    short event         = MT_DMS_CALL_SETTING_AREA;

    m_download_reply_event   = MT_DMS_SETTING_AREA_REPLY;

    m_sheet_result=0;

    m_pThread=new MsgBus_RW_Thread();

    m_pThread->init( term_id, m_oper_timeout_slice );





    m_pThread->setRunFlag(0);



    Setting_Area    area = { 0 };

    area.comm_fac_id=term_id;

    sprintf(g_log_buf,"批量终端召唤定值区 comm_fac_id=%ld ",

            area.comm_fac_id);

    cout<<CLogManager::Instance()->WriteLineT(g_log_buf);

    if( m_pThread->init_msg( serv, event, m_download_reply_event, area ) != 0 )

    {

        sprintf(g_log_buf,"批量终端召唤定值区 初始化失败...");

        CLogManager::Instance()->WriteLineT(g_log_buf);

        Setting_Area tmp_reply_area;

        show_area_mul(term_id,-1,tmp_reply_area);

        // QMessageBox::warning( this, tr("召唤定值区:"), tr("召唤定值区 初始化失败...") );

        //初始化失败

        return ;

    }

    m_pThread->setSheetFlag(true);//是否为工单

    m_pThread->start();



    sheet_exec_dlg_start();





    Setting_Area reply_area;

    while(1)

    {



        usleep(5000);

        cout<<"while!!!!!!!!"<<endl;

        m_sheet_result=m_pThread->getAreaReply(reply_area);

        if(m_sheet_result==0)

            continue;

        else

            break;

    }

    if(m_pThread!=NULL)

    {

        delete m_pThread;

        m_pThread=NULL;

    }

    int ret=m_sheet_result;

    //reply_para=m_reply_para;

    if(ret==-1||ret==-2)

    {

        sprintf(g_log_buf,"批量终端定值区号唤 消息总线注册失败...");

        CLogManager::Instance()->WriteLineT(g_log_buf);



        //QMessageBox::warning( this, tr("定值区号召唤:"), tr("定值区号召唤 消息总线注册失败...") );



    }

    else if(ret==-3)

    {

        sprintf(g_log_buf,"批量终端定值区召唤 消息请求发送失败...");

        CLogManager::Instance()->WriteLineT(g_log_buf);



    }



    else if(ret==-5)

    {

        sprintf(g_log_buf,"批量终端定值区召唤 收到的消息长度小于0...");

        CLogManager::Instance()->WriteLineT(g_log_buf);



        return;

    }

    else if(ret==-6)//超时

    {

        sprintf(g_log_buf,"批量终端定值区召唤 超时...");

        CLogManager::Instance()->WriteLineT(g_log_buf);

    }

    else if(ret==-7)//中止

    {

        sprintf(g_log_buf,"批量终端定值区召唤 中止...");

        CLogManager::Instance()->WriteLineT(g_log_buf);





    }

    else//ret==1

    {

        update_progress_dlg(m_oper_timeout_slice);//接收完成

        SheetStopProgressStep();

        sprintf(g_log_buf,"批量终端定值区召唤返回结果 comm_fac_id=%ld cur_area=%d min_area=%d max_area=%d",

                reply_area.comm_fac_id,reply_area.cur_serial_num,reply_area.min_serial_num,reply_area.max_serial_num);

        cout<<CLogManager::Instance()->WriteLineT(g_log_buf);

        show_area_mul(term_id,ret,reply_area);

        return ;

    }

    show_area_mul(term_id,ret,reply_area);

    return  ;





}

QString get_call_area_result(int result)

{

    switch(result)

    {

    case 1:

        return QString("召唤成功");

    case -7:

        return QString("召唤失败 中止");

    case -6:

        return QString("召唤失败 超时");

    case -2:

    case -1:

        return QString("召唤失败 消息总线注册失败");

    case -3:

        return QString("召唤失败 消息发送失败");

    case -5:

        return QString("召唤失败 消息长度为0");

    default:

        return QString("未标识返回值");



    }

}

void TermParamWindow::show_area_mul(const qint64 &term_id,const int &result,const Setting_Area &area)

{

    QStandardItemModel *pModel = NULL;



    pModel = (QStandardItemModel *)ui->tableView_term_mul->model();

    if( pModel == NULL ) return;



    for( int row = 0; row < pModel->rowCount(); row++ )

    {

        QStandardItem *pItem = pModel->item( row, MUL_TERM_COL_CALL_AREA );

        if( pItem == NULL ) continue;



        QStandardItem *pItemName = pModel->item( row, MUL_TERM_COL_NAME );

        if( pItemName == NULL ) continue;

        qint64 tmp_term_id=pItemName->data(Qt::UserRole+10).toLongLong();

        if(tmp_term_id==term_id&&pItemName->checkState()==Qt::Checked)

        {

            QString strResult=get_call_area_result(result);

            qDebug()<<"reslut_str"<<strResult<<endl;

            pItem->setText(strResult);

            if(result==1)

            {

                pItem->setData((qint64)area.cur_serial_num,Qt::UserRole+10);

                pItem->setToolTip(QString("当前定值区为:%1").arg(area.cur_serial_num));

                pItemName->setData(QVariant(1),Qt::UserRole+14);



                QStandardItem *pValItem = pModel->item( row, MUL_TERM_COL_CALL_AREA_VAL);

                if( pValItem == NULL ) continue;

                pValItem->setText(QString("%1").arg(area.cur_serial_num));



            }

            else

            {

                pItem->setToolTip(QString(""));

                pItemName->setCheckState(Qt::Unchecked);

                pItemName->setData(QVariant(-1),Qt::UserRole+14);

            }

        }

    }

}



void TermParamWindow::on_pushBtn_Write_mul_clicked()

{

    clear_downloadParam();



#ifdef USER_PRIV_CHECK

    if(!delJudgePriv(PRIV_TERM_PARAM_OP,g_strLoginUserName))

    {

        return;

    }

    setWidText(g_strLoginUserName);

    if(QMessageBox::warning(this,tr("提示:"),tr("执行该操作需要监护用户登录进行允许操作,是否继续?"),QMessageBox::Yes|QMessageBox::No)==QMessageBox::Yes)

    {

        int expire_time = 0;

        QDateTime dtStartExeTime;

        QString userName;

        if (doLogin(userName, expire_time, dtStartExeTime)){





            if(!delJudgePriv(PRIV_TERM_PARAM_CUSTODY,userName))

            {

                return;

            }

            m_strCustodyName=userName;

        }

        else

        {

            return;

        }

    }

    else

    {

        return;

    }

    setWidText(g_strLoginUserName);

#else

    QString loginName=g_strLoginUserName;

    if(!delJudgePriv(PRIV_TERM_PARAM_OP,loginName,true))

    {

        return;

    }

    g_strLoginUserName=loginName;

    setWidText(g_strLoginUserName);



    QString userName=m_strCustodyName;

    if(!delJudgePriv(PRIV_TERM_PARAM_CUSTODY,userName))

    {

        return;

    }

    m_strCustodyName=userName;

    setWidText(g_strLoginUserName);

#endif



    QStandardItemModel *pModel = NULL;



    pModel = (QStandardItemModel *)ui->tableView_term_mul->model();

    if( pModel == NULL ) return;



    for( int row = 0; row < pModel->rowCount(); row++ )

    {





        QStandardItem *pItemName = pModel->item( row, MUL_TERM_COL_NAME );

        if( pItemName == NULL ) continue;



        if(pItemName->checkState()==Qt::Checked)

        {

            qint64 tmp_term_id=pItemName->data(Qt::UserRole+10).toLongLong();

            qint32 line_no=pItemName->data(Qt::UserRole+12).toInt();

            downloadParam_mul(tmp_term_id,line_no);

        }



    }

}

qint32 TermParamWindow::init_term_write_para_mul(const qint64 &  term_id, T_Terminal_Para &para,QList<TWarnData> &warn_data_lst,const qint32 & line_no )

{





    QStandardItemModel *pModel = NULL;



    pModel = (QStandardItemModel *)ui->tableView_term_mul->model();

    if( pModel == NULL ) return -1;



    for( int row = 0; row < pModel->rowCount(); row++ )

    {





        QStandardItem *pItemName = pModel->item( row, MUL_TERM_COL_NAME );

        if( pItemName == NULL ) continue;



        QStandardItem *pItemArea = pModel->item( row, MUL_TERM_COL_CALL_AREA );

        if( pItemArea == NULL ) continue;



        qint64 tmp_term_id=pItemName->data(Qt::UserRole+10).toLongLong();

        qint64 tmp_xh_id=pItemName->data(Qt::UserRole+13).toLongLong();

        qint32 tmp_line_no=pItemName->data(Qt::UserRole+12).toInt();

        if(tmp_term_id==term_id&&tmp_line_no==line_no)

        {

            DMS_TERMINAL_INFO_STRUCT struDmsTerminalInfo;

            m_rtdbOper.ReadTerminalInfo(struDmsTerminalInfo,term_id);



            qint32 area_no=pItemArea->data(Qt::UserRole+10).toInt();



            if(area_no==-1)

            {

                return -2;

            }



            QString value_str=ui->comboBox_status->itemData(ui->comboBox_status->currentIndex()).toString();



            QMap<qint32,TermParamRec> code_rec_map;

            readRecordHis(term_id, tmp_xh_id, line_no+TERM_PARAM_LINE_NO_BASE, area_no,code_rec_map);//line_no 本体终端为1  开关为TERM_PARAM_LINE_NO_BASE+





            QString code_str=ui->comboBox_param->itemData(ui->comboBox_param->currentIndex()).toString();

            qDebug()<<"code_str=="<<code_str;

            DMS_TERM_PARAM_DICT_STRUCT param_rec=m_code_paramDict_map[code_str];

            Terminal_Para_Info pi = { 0 };

            qint32 code=QString(code_str).toInt( NULL, 16 );

            pi.comm_fac_id  = term_id;

            pi.line_no      = line_no;

            pi.serial_num   = area_no;

            pi.para_type    = param_rec.param_type;

            pi.value_type   =8;// param_rec.data_type;

            pi.data_type    = code;

            pi.result       = 104;

            if(code_rec_map.find(code)==code_rec_map.end())

            {

                return -3;

            }



            if(!string_to_param_value( value_str, pi ))

            {

                return -1;

            }



            para.terminal_para_seq.push_back( pi );



            TWarnData data;



            //data.combined_id=struDmsTerminalInfo.feeder_id;

            data.term_id=struDmsTerminalInfo.id;

            data.feeder_id=struDmsTerminalInfo.feeder_id;

            data.warn_type=MENU_OPT_PARA_DOWNLOAD;

            QString  param_name_str=param_rec.name;

            QString  param_cur_value=code_rec_map[code].m_value;

            QString param_change_value=value_str;

            if(param_cur_value.indexOf(tr("投入"))!=-1)

                param_cur_value=QString("1");

            else if(param_cur_value.indexOf(tr("退出"))!=-1)

                param_cur_value=QString("0");



            sprintf(g_log_buf,"comm_fac_id=%ld line_no=%d area_no=%d para_type=%d value_type=%d data_type=%d result=%d value=%s",

                    pi.comm_fac_id,pi.line_no,pi.serial_num,pi.para_type,pi.value_type,pi.data_type,pi.result,param_change_value.toStdString().c_str());

            CLogManager::Instance()->WriteLineT(g_log_buf);

            QString op_str=QString(" 参数名称:%1 信息体:%2 由原值:%3  改为:%4").arg(param_name_str).arg(code).arg(param_cur_value).arg(param_change_value);

            data.op_str+="下装参数 []"+op_str.toStdString();

            // sendWarn(data);

            warn_data_lst.push_back(data);

            break;

        }



    }

    para.num = para.terminal_para_seq.size();

    return 1;





}



void TermParamWindow::downloadParam_mul(const qint64 & term_id,const qint32 & line_no)

{

    QString strOper = tr("批量终端下装参数");





    if(m_pThread!=NULL)

    {

        delete m_pThread;

        m_pThread=NULL;

    }

    //用户权限验证

#if 0

    if(!delJudgePriv(PRIV_TERM_PARAM_OP,g_strLoginUserName))

    {

        return;

    }

    setWidText(g_strLoginUserName);

    if(QMessageBox::warning(this,tr("提示:"),tr("执行该操作需要监护用户登录进行允许操作,是否继续?"),QMessageBox::Yes|QMessageBox::No)==QMessageBox::Yes)

    {

        int expire_time = 0;

        QDateTime dtStartExeTime;

        QString userName;

        if (doLogin(userName, expire_time, dtStartExeTime)){





            if(!delJudgePriv(PRIV_TERM_PARAM_CUSTODY,userName))

            {

                return;

            }

            m_strCustodyName=userName;

        }

        else

        {

            return;

        }

    }

    else

    {

        return;

    }

#endif



    T_Terminal_Para modify_para;

    QList<TWarnData>  warn_data_lst;

    int result=init_term_write_para_mul(term_id,modify_para,warn_data_lst,line_no);

    if(result==-1)

    {

        sprintf(g_log_buf,"批量终端参数转换失败 不允许下装...");

        CLogManager::Instance()->WriteLineT(g_log_buf);

        download_show_mul(term_id,-10,line_no);

        // QMessageBox::warning( this, tr("id = %1").arg(term_id), tr("参数转换失败 不允许下装...") );

        return ;

    }

    else if(result==-2)

    {

        sprintf(g_log_buf,"批量终端下装失败未找到关联定值区...");

        CLogManager::Instance()->WriteLineT(g_log_buf);

        download_show_mul(term_id,-11,line_no);

        //QMessageBox::warning( this, tr("id = %1").arg(term_id), tr("下装失败包含有不允许修改的参数...") );

        return ;

    }

    else if(result==-3)

    {

        sprintf(g_log_buf,"批量终端获取当前定值数据失败...");

        CLogManager::Instance()->WriteLineT(g_log_buf);

        download_show_mul(term_id,-12,line_no);

        // QMessageBox::warning( this, tr("id = %1").arg(term_id), tr("获取当前定值数据失败...") );

        return ;

    }

    else if(result==-4)

    {



        // QMessageBox::warning( this, tr("id = %1").arg(term_id), tr("参数值有区间限制 不允许下装...") );

        return ;

    }



    if( modify_para.num == 0 )

    {

        sprintf(g_log_buf,"批量终端请选择需要下装的参数记录...");

        CLogManager::Instance()->WriteLineT(g_log_buf);

        download_show_mul(term_id,-13,line_no);

        //ZMessageBox::warning( this, strOper, tr("请勾选需要下装的参数记录...") );

        return;

    }



    cout<<"----------国网参数预下装-----------"<<endl;

    short serv          = CH_DMS_TERMINAL_DATA;

    short event         = MT_DMS_TERM_PARA_MODIFY_PRE;

    m_download_reply_event  = MT_DMS_TERM_PARA_MODIFY_REPLY;

    m_sheet_result=0;

    m_pThread=new MsgBus_RW_Thread();

    m_pThread->init( term_id, m_oper_timeout_slice );

    m_pThread->setRunFlag(0);

    if( m_pThread->init_msg( serv, event, m_download_reply_event, modify_para ) != 0 )

    {

        //初始化失败

        sprintf(g_log_buf,"批量终端下装参数 初始化失败...");

        CLogManager::Instance()->WriteLineT(g_log_buf);

        download_show_mul(term_id,-14,line_no);

        //QMessageBox::warning( this, tr("下装参数:"), tr("下装参数 初始化失败...") );

        return ;

    }

    m_pThread->setSheetFlag(true);

    //connect( m_pThread, SIGNAL( Post_SheetParaCall(short,QByteArray,qint32)), this, SLOT( On_MsgBus_SheetParaCall(short,QByteArray,qint32)) );

    m_pThread->start();



    sheet_exec_dlg_start();

    T_Terminal_Para reply_para;



    while(1)

    {



        usleep(5000);

        cout<<"while!!!!!!!!"<<endl;

        m_sheet_result=m_pThread->getParamReply(reply_para);

        if(m_sheet_result==0)

            continue;

        else

            break;

    }

    if(m_pThread!=NULL)

    {

        delete m_pThread;

        m_pThread=NULL;

    }

    int ret=m_sheet_result;

    if(ret==-1||ret==-2)

    {

        sprintf(g_log_buf,"批量终端下装参数 消息总线注册失败...");

        CLogManager::Instance()->WriteLineT(g_log_buf);



        //QMessageBox::warning( this, tr("下装参数:"), tr("下装参数 消息总线注册失败...") );



    }

    else if(ret==-3)

    {

        sprintf(g_log_buf,"批量终端下装参数 消息请求发送失败...");

        CLogManager::Instance()->WriteLineT(g_log_buf);

        // QMessageBox::warning( this, tr("下装参数:"), tr("下装参数 消息请求发送失败...") );



    }

    /* else if(ret==-4)

    {



    }*/

    else if(ret==-5)

    {

        sprintf(g_log_buf,"批量终端下装参数 收到的消息长度小于0...");

        CLogManager::Instance()->WriteLineT(g_log_buf);

        //QMessageBox::warning( this, tr("下装参数:"), tr("下装参数 收到的消息长度小于0...") );



    }

    else if(ret==-6)//超时

    {







        for( int i = 0; i < modify_para.terminal_para_seq.size(); i++ )

        {

            Terminal_Para_Info &info = modify_para.terminal_para_seq[i];



            insertParamHisByPara_mul(line_no,term_id,info,2,-1,1);





        }



        sprintf(g_log_buf,"批量终端下装参数 超时...");

        CLogManager::Instance()->WriteLineT(g_log_buf);

        // QMessageBox::warning( this, tr("下装参数:"), tr("下装参数 超时...") );

    }

    else if(ret==-7)//中止

    {

        for( int i = 0; i < modify_para.terminal_para_seq.size(); i++ )

        {

            Terminal_Para_Info &info = modify_para.terminal_para_seq[i];



            insertParamHisByPara_mul(line_no,term_id,info,2,-1,1);





        }

        sprintf(g_log_buf,"批量终端下装参数 中止...");

        CLogManager::Instance()->WriteLineT(g_log_buf);

        // QMessageBox::warning( this, tr("下装参数:"), tr("下装参数 中止...") );

    }

    else//ret==1

    {

        update_progress_dlg(m_oper_timeout_slice);//接收完成

        SheetStopProgressStep();

        for( int i = 0; i < reply_para.terminal_para_seq.size(); i++ )//只返回一个结果

        {

            Terminal_Para_Info &info = reply_para.terminal_para_seq[i];



            sprintf(g_log_buf,"批量终端下装返回结果:result=%d",info.result);

            CLogManager::Instance()->WriteLineT(g_log_buf);



            for(int zz=0;zz<warn_data_lst.count();++zz)

            {

                QString strResult = get_result_desc( info.result );



                TWarnData warn_data=warn_data_lst[zz];

                QString op_str=warn_data.op_str.c_str();

                op_str.replace("[]",strResult);

                warn_data.op_str=op_str.toStdString();

                sendWarn(warn_data);

            }

            if(info.result==2)

            {

                for( int kk = 0; kk < modify_para.terminal_para_seq.size(); kk++ )

                {

                    Terminal_Para_Info modify_info = modify_para.terminal_para_seq[kk];



                    insertParamHisByPara_mul(line_no,term_id,modify_info,2,1,1);

                }

                download_show_mul(term_id,ret,line_no);



            }

            else

            {

                for( int kk = 0; kk < modify_para.terminal_para_seq.size(); kk++ )

                {

                    Terminal_Para_Info modify_info = modify_para.terminal_para_seq[kk];

                    insertParamHisByPara_mul(line_no,term_id,modify_info,2,-1,1);



                }

                download_show_mul(term_id,-8,line_no);

            }



        }

        return;

    }

    download_show_mul(term_id,ret,line_no);

    return ;



}

QString get_download_result(int result)

{

    switch(result)

    {

    case 1:

        return QString("下装成功");

    case -8:

        return QString("下装失败 返回失败");

    case -7:

        return QString("下装失败 中止");

    case -6:

        return QString("下装失败 超时");

    case -2:

    case -1:

        return QString("下装失败 消息总线注册失败");

    case -3:

        return QString("下装失败 消息发送失败");

    case -5:

        return QString("下装失败 消息长度为0");

    case -10:

        return QString("下装失败 参数转换失败不允许下装");

    case -11:

        return QString("下装失败 获取定值区失败");

    case -12:

        return QString("下装失败 获取当前定值数据失败");

    case -13:

        return QString("下装失败 请选择需要下装的参数记录");

    case -14:

        return QString("下装失败 初始化失败");

    case -15:

        return QString("下装失败");

    case -16:

        return QString("下装失败");

    case -17:

        return QString("下装失败");

    default:

        return QString("未标识返回值");



    }

}

void TermParamWindow::download_show_mul(const qint64 &term_id,const int &result,const qint32 & line_no)

{

    QStandardItemModel *pModel = NULL;



    pModel = (QStandardItemModel *)ui->tableView_term_mul->model();

    if( pModel == NULL ) return;



    for( int row = 0; row < pModel->rowCount(); row++ )

    {

        QStandardItem *pItem = pModel->item( row, MUL_TERM_COL_PARAM_DOWNLOAD);

        if( pItem == NULL ) continue;



        QStandardItem *pItemName = pModel->item( row, MUL_TERM_COL_NAME );

        if( pItemName == NULL ) continue;

        qint64 tmp_term_id=pItemName->data(Qt::UserRole+10).toLongLong();

        qint32 tmp_line_no=pItemName->data(Qt::UserRole+12).toInt();

        if(pItemName->checkState()==Qt::Checked&&tmp_term_id==term_id&&tmp_line_no==line_no)

        {



            QString strResult=get_download_result(result);

            qDebug()<<"reslut_str"<<strResult<<endl;

            pItem->setText(strResult);

            if(result!=1)

                pItemName->setCheckState(Qt::Unchecked);

            {

                ui->pushBtn_act_mul->setEnabled(true);

                ui->comboBox_status->setEnabled(false);

                ui->comboBox_param->setEnabled(false);

                ui->tableView_term_mul->setEnabled(false);

            }



        }

    }

}



void TermParamWindow::on_pushBtn_act_mul_clicked()

{

    clear_actParam();



#ifdef USER_PRIV_CHECK

    if(!delJudgePriv(PRIV_TERM_PARAM_OP,g_strLoginUserName))

    {

        return;

    }

    setWidText(g_strLoginUserName);

    if(QMessageBox::warning(this,tr("提示:"),tr("执行该操作需要监护用户登录进行允许操作,是否继续?"),QMessageBox::Yes|QMessageBox::No)==QMessageBox::Yes)

    {

        int expire_time = 0;

        QDateTime dtStartExeTime;

        QString userName;

        if (doLogin(userName, expire_time, dtStartExeTime)){





            if(!delJudgePriv(PRIV_TERM_PARAM_CUSTODY,userName))

            {

                return;

            }

            m_strCustodyName=userName;

        }

        else

        {

            return;

        }

    }

    else

    {

        return;

    }

    setWidText(g_strLoginUserName);

#else

    QString loginName=g_strLoginUserName;

    if(!delJudgePriv(PRIV_TERM_PARAM_OP,loginName,true))

    {

        return;

    }

    g_strLoginUserName=loginName;

    setWidText(g_strLoginUserName);



    QString userName=m_strCustodyName;

    if(!delJudgePriv(PRIV_TERM_PARAM_CUSTODY,userName))

    {

        return;

    }

    m_strCustodyName=userName;

    setWidText(g_strLoginUserName);

#endif

    QStandardItemModel *pModel = NULL;



    pModel = (QStandardItemModel *)ui->tableView_term_mul->model();

    if( pModel == NULL ) return;



    for( int row = 0; row < pModel->rowCount(); row++ )

    {

        QStandardItem *pItemAct = pModel->item( row, MUL_TERM_COL_PARAM_ACT );

        if( pItemAct == NULL ) continue;



        pItemAct->setData(QVariant(0),Qt::UserRole+14);

    }

    for( int row = 0; row < pModel->rowCount(); row++ )

    {

        QStandardItem *pItemName = pModel->item( row, MUL_TERM_COL_NAME );

        if( pItemName == NULL ) continue;



        QStandardItem *pItemAct = pModel->item( row, MUL_TERM_COL_PARAM_ACT );

        if( pItemAct == NULL ) continue;



        qint32 act_status=pItemAct->data(Qt::UserRole+14).toInt();

        if(pItemName->checkState()==Qt::Checked&&act_status==0)

        {

            qint64 tmp_term_id=pItemName->data(Qt::UserRole+10).toLongLong();

            qint32 tmp_line_no=pItemName->data(Qt::UserRole+12).toInt();

            actParam_mul(tmp_term_id,tmp_line_no);

        }



    }



    ui->pushBtn_act_mul->setEnabled(false);

    ui->comboBox_status->setEnabled(true);

    ui->comboBox_param->setEnabled(true);

    ui->tableView_term_mul->setEnabled(true);

}

void TermParamWindow::actParam_mul(const qint64 & term_id,const qint32 & line_no)

{

    QString strOper = tr("批量终端激活参数");

    if(m_pThread!=NULL)

    {

        delete m_pThread;

        m_pThread=NULL;

    }



    //用户权限验证

#if 0

    if(!delJudgePriv(PRIV_TERM_PARAM_OP,g_strLoginUserName))

    {

        return;

    }

    setWidText(g_strLoginUserName);

    if(QMessageBox::warning(this,tr("提示:"),tr("执行该操作需要监护用户登录进行允许操作,是否继续?"),QMessageBox::Yes|QMessageBox::No)==QMessageBox::Yes)

    {

        int expire_time = 0;

        QDateTime dtStartExeTime;

        QString userName;

        if (doLogin(userName, expire_time, dtStartExeTime)){





            if(!delJudgePriv(PRIV_TERM_PARAM_CUSTODY,userName))

            {

                return;

            }

            m_strCustodyName=userName;

        }

        else

        {

            return;

        }

    }

    else

    {

        return;

    }

#endif

    T_Terminal_Para act_para;

    QList<TWarnData> warn_data_lst ;

    init_term_act_para_mul(term_id,act_para,warn_data_lst);

    // QMessageBox::warning(this,tr("提示:"),tr("执行该操作需要监护用户登录进行允许操作,是否继续? ct=%1").arg(warn_data_lst.count()));

    short serv          = CH_DMS_TERMINAL_DATA;

    short event         = MT_DMS_TERM_PARA_MODIFY;

    m_download_reply_event   = MT_DMS_TERM_PARA_MODIFY_CONF;









    T_Terminal_Op para;

    //qint32 line_no=get_line_no_mul(term_id);

    qint32 area_no=get_area_no_mul(term_id,line_no);





    Terminal_Para_Op item = { 0 };

    item.comm_fac_id    =term_id;

    item.line_no        = -1;/*不是必要参数*/

    item.serial_num     = area_no;

    item.para_type      = 0;    //0，运行参数 1，动作定值参数，

    //文档中有此说明，但前置未处理，即同时激活运行参数和动作定值

    para.terminal_op_seq.push_back( item );

    para.num=para.terminal_op_seq.size();



    sprintf(g_log_buf,"批量终端激活参数:\ncomm_fac_id=%ld line_no=%d area_no=%d para_type=%d ",

            item.comm_fac_id,item.line_no,item.serial_num);

    CLogManager::Instance()->WriteLineT(g_log_buf);





    //T_Terminal_Para act_para;

    //QList<TWarnData>  warn_data_lst;

    //init_term_act_para( act_para,warn_data_lst);





    m_sheet_result=0;

    m_pThread=new MsgBus_RW_Thread();

    m_pThread->init( term_id, m_oper_timeout_slice );

    m_pThread->setRunFlag(0);

    if( m_pThread->init_msg( serv, event, m_download_reply_event, para ) != 0 )

    {

        //初始化失败

        sprintf(g_log_buf,"批量终端激活参数 初始化失败...");

        CLogManager::Instance()->WriteLineT(g_log_buf);

        act_show_mul(term_id,-14,line_no);

        // QMessageBox::warning( this, tr("激活参数:"), tr("激活参数 初始化失败...") );



        return ;

    }

    m_pThread->setSheetFlag(true);

    // connect( m_pThread, SIGNAL( Post_SheetParaCall(short,QByteArray,qint32)), this, SLOT( On_MsgBus_SheetParaCall(short,QByteArray,qint32)) );

    m_pThread->start();



    sheet_exec_dlg_start();



    T_Terminal_Op reply_op;

    while(1)

    {



        usleep(5000);

        cout<<"while!!!!!!!!"<<endl;

        m_sheet_result=m_pThread->getParamReply(reply_op);

        if(m_sheet_result==0)

            continue;

        else

            break;

    }

    if(m_pThread!=NULL)

    {

        delete m_pThread;

        m_pThread=NULL;

    }

    int ret=m_sheet_result;





    if(ret==-1||ret==-2)

    {

        sprintf(g_log_buf,"批量终端激活参数 消息总线注册失败...");

        CLogManager::Instance()->WriteLineT(g_log_buf);

        //QMessageBox::warning( this, tr("激活参数:"), tr("激活参数 消息总线注册失败...") );





    }

    else if(ret==-3)

    {

        sprintf(g_log_buf,"批量终端激活参数 消息请求发送失败...");

        CLogManager::Instance()->WriteLineT(g_log_buf);

        // QMessageBox::warning( this, tr("激活参数:"), tr("激活参数 消息请求发送失败...") );



    }

    /* else if(ret==-4)

    {



    }*/

    else if(ret==-5)

    {

        sprintf(g_log_buf,"批量终端激活参数 收到的消息长度小于0...");

        CLogManager::Instance()->WriteLineT(g_log_buf);

        // QMessageBox::warning( this, tr("激活参数:"), tr("激活参数 收到的消息长度小于0...") );



    }

    else if(ret==-6)//超时

    {



        for( int i = 0; i < act_para.terminal_para_seq.size(); i++ )

        {

            Terminal_Para_Info &info = act_para.terminal_para_seq[i];

            insertParamHisByPara_mul(act_para.terminal_para_seq[i].line_no,term_id,info,3,-1,1);







        }

        sprintf(g_log_buf,"批量终端激活参数 超时...");

        CLogManager::Instance()->WriteLineT(g_log_buf);

        // QMessageBox::warning( this, tr("激活参数:"), tr("激活参数 超时...") );



    }

    else if(ret==-7)//中止

    {



        for( int i = 0; i < act_para.terminal_para_seq.size(); i++ )

        {

            Terminal_Para_Info &info = act_para.terminal_para_seq[i];

            // update_oper_result( 106);

            insertParamHisByPara_mul(act_para.terminal_para_seq[i].line_no,term_id,info,3,-1,1);





        }

        sprintf(g_log_buf,"批量终端激活参数 中止...");

        CLogManager::Instance()->WriteLineT(g_log_buf);

        // QMessageBox::warning( this, tr("激活参数:"), tr("激活参数 中止...") );



    }

    else//ret==1

    {

        update_progress_dlg(m_oper_timeout_slice);//接收完成

        SheetStopProgressStep();



        for( int i = 0; i < reply_op.terminal_op_seq.size(); i++ )

        {

            Terminal_Para_Op &item = reply_op.terminal_op_seq[i];





            QString strText = tr("返回值=%1 ").arg(item.result);

            sprintf(g_log_buf,"批量终端激活返回结果:result=%d",item.result);

            CLogManager::Instance()->WriteLineT(g_log_buf);



            for(int zz=0;zz<warn_data_lst.count();++zz)

            {

                QString strResult;

                if(item.result==2)

                {

                    strResult = QString("激活成功(%1)").arg(item.result);



                }

                else

                {

                    strResult = QString("激活失败(%1)").arg(item.result);



                }

                TWarnData warn_data=warn_data_lst[zz];

                warn_data.warn_type=MENU_OPT_PARA_ACTIVATE;

                QString op_str=warn_data.op_str.c_str();

                op_str.replace("[]",strResult);

                warn_data.op_str=op_str.toStdString();

                sendWarn(warn_data);

                sleep(1);

            }





            if(item.result==2)

            {

                for( int i = 0; i < act_para.terminal_para_seq.size(); i++ )

                {

                    Terminal_Para_Info &info = act_para.terminal_para_seq[i];



                    insertParamHisByPara_mul(info.line_no,term_id,info,3,1,1);

                    act_show_mul(term_id,ret,info.line_no);



                }

            }

            else

            {

                for( int i = 0; i < act_para.terminal_para_seq.size(); i++ )

                {

                    Terminal_Para_Info &info = act_para.terminal_para_seq[i];

                    insertParamHisByPara_mul(info.line_no,term_id,info,3,-1,1);

                    act_show_mul(term_id,-8,info.line_no);

                }

            }



#if 1

            switch( item.result )

            {

            case 0:///711

                strText = tr("默认(%1)").arg(item.result);

                // update_oper_result( 711);

                //ZMessageBox::warning( this, tr("激活参数:"), strText );

                break;

            case 1://712

                strText = tr("激活失败(%1)").arg(item.result);

                //update_oper_result( 712);

                //ZMessageBox::critical( this, tr("激活参数:"), strText );

                break;

            case 2://713

                strText = tr("激活成功(%1)").arg(item.result);

                //update_oper_result( 713);

                // update_reply_act_para(act_para);

                //on_pushBtn_check_clicked();

                for( int i = 0; i < act_para.terminal_para_seq.size(); i++ )

                {

                    Terminal_Para_Info &info = act_para.terminal_para_seq[i];



                    checkParam_mul(term_id,info.line_no);

                }



                //ZMessageBox::information( this, tr("激活参数:"), strText );

                break;



            default:

                //update_oper_result( 714);

                strText=tr("激活失败(%1)......").arg(item.result);

                //ZMessageBox::warning( this, tr("激活参数:"), strText );

                break;

            }

#endif

        }

        return;

    }

    for( int i = 0; i < act_para.terminal_para_seq.size(); i++ )

    {

        Terminal_Para_Info &info = act_para.terminal_para_seq[i];

        act_show_mul(term_id,ret,info.line_no);



    }



    return ;



}

qint32 TermParamWindow::init_term_check_para_mul(const qint64 &  term_id, T_Terminal_Para &para,const qint32 &line_no )

{

    QStandardItemModel *pModel = NULL;



    pModel = (QStandardItemModel *)ui->tableView_term_mul->model();

    if( pModel == NULL ) return -1;



    for( int row = 0; row < pModel->rowCount(); row++ )

    {





        QStandardItem *pItemName = pModel->item( row, MUL_TERM_COL_NAME );

        if( pItemName == NULL ) continue;



        QStandardItem *pItemArea = pModel->item( row, MUL_TERM_COL_CALL_AREA );

        if( pItemArea == NULL ) continue;



        qint64 tmp_term_id=pItemName->data(Qt::UserRole+10).toLongLong();

        qint64 tmp_xh_id=pItemName->data(Qt::UserRole+13).toLongLong();

        qint32 tmp_line_no=pItemName->data(Qt::UserRole+12).toInt();

        if(tmp_term_id==term_id&&line_no==tmp_line_no)

        {

            //

            qint32 area_no=pItemArea->data(Qt::UserRole+10).toInt();

            QString code_str=ui->comboBox_param->itemData(ui->comboBox_param->currentIndex()).toString();

            DMS_TERM_PARAM_DICT_STRUCT param_rec=m_code_paramDict_map[code_str];

            qint32 code=QString(code_str).toInt( NULL, 16 );



            Terminal_Para_Info pi = { 0 };

            pi.comm_fac_id  = term_id;

            pi.line_no      = line_no;

            pi.serial_num   = area_no;

            pi.para_type    = param_rec.param_type;

            pi.value_type   =param_rec.data_type;

            pi.data_type    = code;

            pi.result       = 104;

            para.terminal_para_seq.push_back(pi);

            sprintf(g_log_buf,"comm_fac_id=%ld line_no=%d area_no=%d para_type=%d value_type=%d data_type=%d result=%d ",

                    pi.comm_fac_id,pi.line_no,pi.serial_num,pi.para_type,pi.value_type,pi.data_type,pi.result);

            CLogManager::Instance()->WriteLineT(g_log_buf);

            break;



        }



    }

    para.num = para.terminal_para_seq.size();

    return 1;



}

void TermParamWindow::checkParam_mul(const qint64 &  term_id,const qint32 &line_no)

{

    QString strOper = tr("批量终端校验参数");





    if(m_pThread!=NULL)

    {

        delete m_pThread;

        m_pThread=NULL;

    }



    T_Terminal_Para check_para;

    init_term_check_para_mul(term_id, check_para,line_no );



    if( check_para.num == 0 )

    {

        sprintf(g_log_buf,"批量终端校验参数 请选择需要召唤的参数记录...");

        CLogManager::Instance()->WriteLineT(g_log_buf);

        Terminal_Para_Info tmp_info;

        check_show_mul(term_id,-13,tmp_info,line_no);

        //ZMessageBox::warning( this, strOper, tr("请勾选需要召唤的参数记录...") );

        return;

    }

    //激活参数

    short serv          = CH_DMS_TERMINAL_DATA;

    short event         = MT_DMS_TERM_PARA_CALL;;

    m_download_reply_event   = MT_DMS_TERM_PARA_REPLY;

    m_sheet_result=0;

    m_pThread=new MsgBus_RW_Thread();

    m_pThread->init( term_id, m_oper_timeout_slice );



    m_pThread->setRunFlag(0);

    if( m_pThread->init_msg( serv, event, m_download_reply_event, check_para ) != 0 )

    {

        sprintf(g_log_buf,"批量终端校验参数 初始化失败...");

        CLogManager::Instance()->WriteLineT(g_log_buf);

        Terminal_Para_Info tmp_info;

        check_show_mul(term_id,-14,tmp_info,line_no);

        //QMessageBox::warning( this, tr("校验参数:"), tr("校验参数 初始化失败...") );

        //初始化失败

        return ;

    }

    m_pThread->setSheetFlag(true);



    m_pThread->start();

    sheet_exec_dlg_start();

    T_Terminal_Para reply_para;

    while(1)

    {



        usleep(5000);

        cout<<"while!!!!!!!!"<<endl;

        m_sheet_result=m_pThread->getParamReply(reply_para);

        if(m_sheet_result==0)

            continue;

        else

            break;

    }

    if(m_pThread!=NULL)

    {

        delete m_pThread;

        m_pThread=NULL;

    }

    int ret=m_sheet_result;

    QMap<qint32,Terminal_Para_Info> recv_code_map;

    CLogManager::Instance()->WriteLineT("批量终端校验参数返回结果:");

    for( int i = 0; i < reply_para.terminal_para_seq.size(); i++ )

    {

        Terminal_Para_Info pi = reply_para.terminal_para_seq[i];

        sprintf(g_log_buf,"comm_fac_id=%ld line_no=%d area_no=%d para_type=%d value_type=%d data_type=%d value_f=%f value_i=%d result=%d ",

                pi.comm_fac_id,pi.line_no,pi.serial_num,pi.para_type,pi.value_type,pi.data_type,pi.value_f,pi.value_i,pi.result);

        CLogManager::Instance()->WriteLineT(g_log_buf);

        //  CLogManager::Instance()->WriteLineT(QString::fromUtf8(pi.value_s).toStdString());

    }

    if(ret==-1||ret==-2)

    {

        sprintf(g_log_buf,"批量终端校验参数 消息总线注册失败...");

        CLogManager::Instance()->WriteLineT(g_log_buf);

        //QMessageBox::warning( this, tr("校验参数:"), tr("校验参数 消息总线注册失败...") );



    }

    else if(ret==-3)

    {

        sprintf(g_log_buf,"批量终端校验参数 消息请求发送失败...");

        CLogManager::Instance()->WriteLineT(g_log_buf);

        //QMessageBox::warning( this, tr("校验参数:"), tr("校验参数 消息请求发送失败...") );



    }

    /* else if(ret==-4)

    {



    }*/

    else if(ret==-5)

    {

        sprintf(g_log_buf,"批量终端校验参数 收到的消息长度小于0...");

        CLogManager::Instance()->WriteLineT(g_log_buf);

        //QMessageBox::warning( this, tr("校验参数:"), tr("校验参数 收到的消息长度小于0...") );



    }

    else if(ret==-6)//超时

    {





        for( int i = 0; i < check_para.terminal_para_seq.size(); i++ )

        {

            Terminal_Para_Info &info = check_para.terminal_para_seq[i];





            insertParamHisByPara_mul(line_no,term_id,info,4,-1,1);



        }

        sprintf(g_log_buf,"批量终端校验参数 超时...");

        CLogManager::Instance()->WriteLineT(g_log_buf);



        // QMessageBox::warning( this, tr("校验参数:"), tr("校验参数 超时...") );

    }

    else if(ret==-7)//中止

    {

        for( int i = 0; i < check_para.terminal_para_seq.size(); i++ )

        {

            Terminal_Para_Info &info = check_para.terminal_para_seq[i];

            insertParamHisByPara_mul(line_no,term_id,info,4,-1,1);

        }

        sprintf(g_log_buf,"批量终端校验参数 中止...");

        CLogManager::Instance()->WriteLineT(g_log_buf);

        // QMessageBox::warning( this, tr("校验参数:"), tr("校验参数 中止...") );



    }

    else//ret==1

    {



        update_progress_dlg(m_oper_timeout_slice);//接收完成

        SheetStopProgressStep();

        cout<<"reply_size=="<<reply_para.terminal_para_seq.size()<<endl;

        // QSet<qint32> recv_code;

        for( int i = 0; i < reply_para.terminal_para_seq.size(); i++ )

        {

            Terminal_Para_Info &info = reply_para.terminal_para_seq[i];



            recv_code_map[info.data_type]=info;

            check_show_mul(term_id,ret,info,line_no);

            //show_check_para_info( info );

            //update_oper_result( info.data_type, 10/*info.result*/ );  //召唤的返回结果不可用

        }

        update_reply_check_para_mul(term_id, reply_para ,line_no);  //更新进商用数据库

        return ;

    }



    update_reply_check_para_mul(term_id, reply_para ,line_no);  //更新进商用数据库

    Terminal_Para_Info tmp_info;

    check_show_mul(term_id,ret,tmp_info,line_no);

    return ;

}

void TermParamWindow::update_reply_check_para_mul(const qint64 &term_id, T_Terminal_Para &para,const qint32 &line_no )

{

    int count = para.terminal_para_seq.size();

    //Q_ASSERT( para.num == count );



    int area_no = get_area_no_mul(term_id,line_no);

    //int line_no = get_line_no_mul(term_id);

    qint64 term_xh=get_xh_id_mul(term_id);

    QListTermParamRec lstParamRec;



    for( int m = 0; m < para.terminal_para_seq.size(); m++ )

    {

        Terminal_Para_Info &info = para.terminal_para_seq[m];







        DMS_TERM_PARAM_DICT_STRUCT param_rec = m_code_paramDict_map[QString::number(info.data_type,16)];

        if( QString(param_rec.param_code).toInt( NULL, 16 ) == info.data_type )

        {





            TermParamRec update_rec;

            update_rec.m_term_id        = term_id;                              //终端ID

            update_rec.m_xh_id          = term_xh;                               //型号ID

            update_rec.m_line_no        = line_no+TERM_PARAM_LINE_NO_BASE;                                          //微机保护线路号

            update_rec.m_area           = area_no;//info.serial_num;                        //定值区 这里取召唤的定值区号 因为上送的有问题

            update_rec.m_code           = QString::fromStdString( param_rec.param_code );   //参数代码(16进制不带0x)

            update_rec.m_code_10        = update_rec.m_code.toInt( NULL, 16 );              //10进制参数代码

            update_rec.m_name           = QString::fromStdString(param_rec.name );          //参数名称

            update_rec.m_type           = param_rec.param_type;                             //参数类别(0-运行参数;1-动作定值;2-固有参数)

            info.para_type              = param_rec.param_type;     //上送的参数类别不可信 以定义的为准

            update_rec.m_value          = param_value_to_string( info );                    //参数值

            update_rec.m_data_type      = param_rec.data_type;                              //参数值数据类型(1-类型不确定;2-int;3-float;4-short;5-unsigned short;6-unsigned int;7-string)

            update_rec.m_unit           = QString::fromStdString( param_rec.unit );         //参数单位

            update_rec.m_coe            = param_rec.coe;                                    //参数系数

            update_rec.m_data_source    = 1;                                                //数据来源(1-1区;2-四区;3-其他)

            update_rec.m_update_time    = QDateTime::currentDateTime();                     //最近更新时间



            lstParamRec.append( update_rec );



            break;

        }



    }

    //校验召唤上来的参数写库 但不更新界面

    TermParam_Write_Thread *pWriteThread = new TermParam_Write_Thread( this );

    pWriteThread->setUserName(g_strLoginUserName);

    connect( pWriteThread, SIGNAL(finished()), pWriteThread, SLOT( deleteLater() ) );

    // connect( pWriteThread, SIGNAL(WriteFinished(int)), this, SLOT( OnParamWriteFinished(int) ), Qt::DirectConnection );

    pWriteThread->init( lstParamRec );

    pWriteThread->start();

}

QString get_check_result(int result)

{

    switch(result)

    {

    case 1:

        return QString("激活成功");

    case -8:

        return QString("激活失败 校验返回失败");

    case -9:

        return QString("激活失败 校验失败");

    case -7:

        return QString("激活失败 校验中止");

    case -6:

        return QString("激活失败 校验超时");

    case -2:

    case -1:

        return QString("激活失败 校验消息总线注册失败");

    case -3:

        return QString("激活失败 校验消息发送失败");

    case -5:

        return QString("激活失败 校验消息长度为0");

    case -13:

        return QString("激活失败 请选择需要下装的参数记录");

    case -14:

        return QString("激活失败 初始化失败");

    default:

        return QString("未标识校验返回值");



    }

}

void TermParamWindow::check_show_mul(const qint64 &term_id,const int &result,const Terminal_Para_Info &info,const qint32 &line_no)

{

    QStandardItemModel *pModel = NULL;



    pModel = (QStandardItemModel *)ui->tableView_term_mul->model();

    if( pModel == NULL ) return;



    for( int row = 0; row < pModel->rowCount(); row++ )

    {

        QStandardItem *pItem = pModel->item( row, MUL_TERM_COL_PARAM_ACT);

        if( pItem == NULL ) continue;



        QStandardItem *pItemName = pModel->item( row, MUL_TERM_COL_NAME );

        if( pItemName == NULL ) continue;

        qint64 tmp_term_id=pItemName->data(Qt::UserRole+10).toLongLong();

        qint64 tmp_line_no=pItemName->data(Qt::UserRole+12).toInt();

        if(pItemName->checkState()==Qt::Checked&&tmp_term_id==term_id&&line_no==tmp_line_no)

        {

            QString strResult=get_check_result(result);

            qDebug()<<"reslut_str"<<strResult<<endl;

            pItem->setText(strResult);

            if(result==1)

            {

                QStandardItem *pItemValue = pModel->item( row, MUL_TERM_COL_STATUS );

                if( pItemValue == NULL ) continue;

                if(info.value_i==1)

                    pItemValue->setText(tr("投入"));

                else

                    pItemValue->setText(tr("退出"));





                TWarnData data;

                DMS_TERMINAL_INFO_STRUCT struDmsTerminalInfo;

                m_rtdbOper.ReadTerminalInfo(struDmsTerminalInfo,term_id);

                //data.combined_id=struDmsTerminalInfo.feeder_id;

                data.term_id=struDmsTerminalInfo.id;

                data.warn_type=MENU_OPT_PARA_CHECK;

                data.feeder_id=struDmsTerminalInfo.feeder_id;

                data.op_str+="校验参数 ";







                QString value_str=ui->comboBox_status->currentText();

                QString check_value_str=pItemValue->text();

                DMS_TERM_PARAM_DICT_STRUCT param_rec=m_code_paramDict_map[QString::number(info.data_type,16)];

                if(value_str!=check_value_str)

                {



                    insertParamHisByPara_mul(line_no,term_id,info,4,-1,1);

                    pItem->setText(get_check_result(-9));

                    sprintf(g_log_buf,"校验参数失败 参数名称:%s 信息体:%d 下装激活值:%s 本次召唤上来的值:%s",

                            param_rec.name,info.data_type,value_str.toStdString().c_str(),check_value_str.toStdString().c_str());

                    CLogManager::Instance()->WriteLineT(g_log_buf);

                    data.op_str+=g_log_buf;

                    sendWarn(data);



                }

                else

                {

                    insertParamHisByPara_mul(line_no,term_id,info,4,1,1);



                    sprintf(g_log_buf,"校验参数成功 参数名称:%s 信息体:%d 下装激活值:%s 本次召唤上来的值:%s",

                            param_rec.name,info.data_type,value_str.toStdString().c_str(),check_value_str.toStdString().c_str());

                    CLogManager::Instance()->WriteLineT(g_log_buf);

                    data.op_str+=g_log_buf;

                    sendWarn(data);



                }





            }



        }

    }

}

QString get_act_result(int result)

{

    switch(result)

    {

    case 1:

        return QString("激活成功");

    case -8:

        return QString("激活失败 返回失败");

    case -7:

        return QString("激活失败 中止");

    case -6:

        return QString("激活失败 超时");

    case -2:

    case -1:

        return QString("激活失败 消息总线注册失败");

    case -3:

        return QString("激活失败 消息发送失败");

    case -5:

        return QString("激活失败 消息长度为0");

    case -14:

        return QString("激活失败 初始化失败");

    default:

        return QString("未标识返回值");



    }

}

void TermParamWindow::act_show_mul(const qint64 &term_id,const int &result,const qint32 &line_no)

{

    QStandardItemModel *pModel = NULL;



    pModel = (QStandardItemModel *)ui->tableView_term_mul->model();

    if( pModel == NULL ) return;



    for( int row = 0; row < pModel->rowCount(); row++ )

    {

        QStandardItem *pItem = pModel->item( row, MUL_TERM_COL_PARAM_ACT);

        if( pItem == NULL ) continue;



        QStandardItem *pItemName = pModel->item( row, MUL_TERM_COL_NAME );

        if( pItemName == NULL ) continue;

        qint64 tmp_term_id=pItemName->data(Qt::UserRole+10).toLongLong();

        qint32 tmp_line_no=pItemName->data(Qt::UserRole+12).toLongLong();

        if(pItemName->checkState()==Qt::Checked&&tmp_term_id==term_id&&line_no==tmp_line_no)

        {

            QString strResult=get_act_result(result);

            qDebug()<<"reslut_str"<<strResult<<endl;

            pItem->setText(strResult);

            pItem->setData(QVariant(1),Qt::UserRole+14);

            if(result!=1)

            {

                pItemName->setCheckState(Qt::Unchecked);

            }



        }

    }

}



qint32 TermParamWindow::get_line_no_mul(const qint64 &  term_id)

{

    QStandardItemModel *pModel = NULL;



    pModel = (QStandardItemModel *)ui->tableView_term_mul->model();

    if( pModel == NULL ) return -1;



    for( int row = 0; row < pModel->rowCount(); row++ )

    {





        QStandardItem *pItemName = pModel->item( row, MUL_TERM_COL_NAME );

        if( pItemName == NULL ) continue;



        QStandardItem *pItemArea = pModel->item( row, MUL_TERM_COL_CALL_AREA );

        if( pItemArea == NULL ) continue;



        qint64 tmp_term_id=pItemName->data(Qt::UserRole+10).toLongLong();

        qint64 tmp_xh_id=pItemName->data(Qt::UserRole+13).toLongLong();

        if(tmp_term_id==term_id)

        {

            DMS_TERMINAL_INFO_STRUCT struDmsTerminalInfo;

            m_rtdbOper.ReadTerminalInfo(struDmsTerminalInfo,term_id);

            qint32 line_no=pItemName->data(Qt::UserRole+12).toInt();

            qint32 area_no=pItemArea->data(Qt::UserRole+10).toInt();

            return line_no;

        }

    }

    return -1;

}

qint32 TermParamWindow::get_area_no_mul(const qint64 &  term_id,const qint32 &line_no)

{

    QStandardItemModel *pModel = NULL;



    pModel = (QStandardItemModel *)ui->tableView_term_mul->model();

    if( pModel == NULL ) return -1;



    for( int row = 0; row < pModel->rowCount(); row++ )

    {





        QStandardItem *pItemName = pModel->item( row, MUL_TERM_COL_NAME );

        if( pItemName == NULL ) continue;



        QStandardItem *pItemArea = pModel->item( row, MUL_TERM_COL_CALL_AREA );

        if( pItemArea == NULL ) continue;



        qint64 tmp_term_id=pItemName->data(Qt::UserRole+10).toLongLong();

        qint64 tmp_xh_id=pItemName->data(Qt::UserRole+13).toLongLong();

        qint32 tmp_line_no=pItemName->data(Qt::UserRole+12).toInt();

        if(tmp_term_id==term_id&&line_no==tmp_line_no)

        {

            DMS_TERMINAL_INFO_STRUCT struDmsTerminalInfo;

            m_rtdbOper.ReadTerminalInfo(struDmsTerminalInfo,term_id);



            qint32 area_no=pItemArea->data(Qt::UserRole+10).toInt();

            return area_no;

        }

    }

    return -1;

}

qint64 TermParamWindow::get_xh_id_mul(const qint64 &  term_id)

{

    QStandardItemModel *pModel = NULL;



    pModel = (QStandardItemModel *)ui->tableView_term_mul->model();

    if( pModel == NULL ) return -1;



    for( int row = 0; row < pModel->rowCount(); row++ )

    {





        QStandardItem *pItemName = pModel->item( row, MUL_TERM_COL_NAME );

        if( pItemName == NULL ) continue;



        QStandardItem *pItemArea = pModel->item( row, MUL_TERM_COL_CALL_AREA );

        if( pItemArea == NULL ) continue;



        qint64 tmp_term_id=pItemName->data(Qt::UserRole+10).toLongLong();

        qint64 tmp_xh_id=pItemName->data(Qt::UserRole+13).toLongLong();

        if(tmp_term_id==term_id)

        {

            DMS_TERMINAL_INFO_STRUCT struDmsTerminalInfo;

            m_rtdbOper.ReadTerminalInfo(struDmsTerminalInfo,term_id);

            qint32 line_no=pItemName->data(Qt::UserRole+12).toInt();

            qint32 area_no=pItemArea->data(Qt::UserRole+10).toInt();

            return tmp_xh_id;

        }

    }

    return -1;

}

qint32 TermParamWindow::init_term_act_para_mul(const qint64 &  term_id, T_Terminal_Para &para,QList<TWarnData> &warn_data_lst )

{





    QStandardItemModel *pModel = NULL;



    pModel = (QStandardItemModel *)ui->tableView_term_mul->model();

    if( pModel == NULL ) return -1;



    for( int row = 0; row < pModel->rowCount(); row++ )

    {





        QStandardItem *pItemName = pModel->item( row, MUL_TERM_COL_NAME );

        if( pItemName == NULL ) continue;



        QStandardItem *pItemArea = pModel->item( row, MUL_TERM_COL_CALL_AREA );

        if( pItemArea == NULL ) continue;



        qint64 tmp_term_id=pItemName->data(Qt::UserRole+10).toLongLong();

        qint64 tmp_xh_id=pItemName->data(Qt::UserRole+13).toLongLong();

        if(tmp_term_id==term_id)

        {

            DMS_TERMINAL_INFO_STRUCT struDmsTerminalInfo;

            m_rtdbOper.ReadTerminalInfo(struDmsTerminalInfo,term_id);

            qint32 line_no=pItemName->data(Qt::UserRole+12).toInt();

            qint32 area_no=pItemArea->data(Qt::UserRole+10).toInt();

            QString value_str=ui->comboBox_status->itemData(ui->comboBox_status->currentIndex()).toString();



            QMap<qint32,TermParamRec> code_rec_map;

            readRecordHis(term_id, tmp_xh_id, line_no+TERM_PARAM_LINE_NO_BASE, area_no,code_rec_map);//line_no 本体终端为1  开关为TERM_PARAM_LINE_NO_BASE+





            QString code_str=ui->comboBox_param->itemData(ui->comboBox_param->currentIndex()).toString();

            qDebug()<<"code_str=="<<code_str;

            DMS_TERM_PARAM_DICT_STRUCT param_rec=m_code_paramDict_map[code_str];

            Terminal_Para_Info pi = { 0 };

            qint32 code=QString(code_str).toInt( NULL, 16 );

            pi.comm_fac_id  = term_id;

            pi.line_no      = line_no;

            pi.serial_num   = area_no;

            pi.para_type    = param_rec.param_type;

            pi.value_type   =8;// param_rec.data_type;

            pi.data_type    = code;

            pi.result       = 104;

            if(code_rec_map.find(code)==code_rec_map.end())

            {

                return -3;

            }



            if(!string_to_param_value( value_str, pi ))

            {

                return -1;

            }



            para.terminal_para_seq.push_back( pi );



            TWarnData data;



            //data.combined_id=struDmsTerminalInfo.feeder_id;

            data.term_id=struDmsTerminalInfo.id;

            data.feeder_id=struDmsTerminalInfo.feeder_id;

            data.warn_type=MENU_OPT_PARA_DOWNLOAD;

            QString  param_name_str=param_rec.name;

            QString  param_cur_value=code_rec_map[code].m_value;

            QString param_change_value=value_str;

            if(param_cur_value.indexOf(tr("投入"))!=-1)

                param_cur_value=QString("1");

            else if(param_cur_value.indexOf(tr("退出"))!=-1)

                param_cur_value=QString("0");



            sprintf(g_log_buf,"comm_fac_id=%ld line_no=%d area_no=%d para_type=%d value_type=%d data_type=%d result=%d value=%s",

                    pi.comm_fac_id,pi.line_no,pi.serial_num,pi.para_type,pi.value_type,pi.data_type,pi.result,param_change_value.toStdString().c_str());

            CLogManager::Instance()->WriteLineT(g_log_buf);

            QString op_str=QString(" 参数名称:%1 信息体:%2 由原值:%3  改为:%4").arg(param_name_str).arg(code).arg(param_cur_value).arg(param_change_value);

            data.op_str+="下装参数 []"+op_str.toStdString();

            // sendWarn(data);

            warn_data_lst.push_back(data);



        }



    }

    para.num = para.terminal_para_seq.size();

    return 1;





}



void TermParamWindow::on_splitter_3_splitterMoved(int pos, int index)

{

    m_treeWidget_width_mul = ui->widgetTree_mul->width();

}



void TermParamWindow::on_pushBtn_Read_mul_clicked()

{

    clear_callParam();

    ui->comboBox_status->setEnabled(true);

    ui->comboBox_param->setEnabled(true);

    ui->tableView_term_mul->setEnabled(true);

    QStandardItemModel *pModel = NULL;



    pModel = (QStandardItemModel *)ui->tableView_term_mul->model();

    if( pModel == NULL ) return;



    for( int row = 0; row < pModel->rowCount(); row++ )

    {





        QStandardItem *pItemName = pModel->item( row, MUL_TERM_COL_NAME );

        if( pItemName == NULL ) continue;



        if(pItemName->checkState()==Qt::Checked)

        {

            qint64 tmp_term_id=pItemName->data(Qt::UserRole+10).toLongLong();

            qint32 line_no=pItemName->data(Qt::UserRole+12).toInt();

            callParam_mul(tmp_term_id,line_no);

        }



    }

}

qint32 TermParamWindow::init_term_call_para_mul(const qint64 &  term_id, T_Terminal_Para &para,const qint32 &line_no )

{

    QStandardItemModel *pModel = NULL;



    pModel = (QStandardItemModel *)ui->tableView_term_mul->model();

    if( pModel == NULL ) return -1;



    for( int row = 0; row < pModel->rowCount(); row++ )

    {





        QStandardItem *pItemName = pModel->item( row, MUL_TERM_COL_NAME );

        if( pItemName == NULL ) continue;



        QStandardItem *pItemArea = pModel->item( row, MUL_TERM_COL_CALL_AREA );

        if( pItemArea == NULL ) continue;



        qint64 tmp_term_id=pItemName->data(Qt::UserRole+10).toLongLong();

        qint64 tmp_xh_id=pItemName->data(Qt::UserRole+13).toLongLong();

        qint32 tmp_line_no=pItemName->data(Qt::UserRole+12).toInt();

        if(tmp_term_id==term_id&&line_no==tmp_line_no)

        {

            //

            qint32 area_no=pItemArea->data(Qt::UserRole+10).toInt();

            if(area_no==-1)

            {

                return -2;

            }

            QString code_str=ui->comboBox_param->itemData(ui->comboBox_param->currentIndex()).toString();

            DMS_TERM_PARAM_DICT_STRUCT param_rec=m_code_paramDict_map[code_str];

            qint32 code=QString(code_str).toInt( NULL, 16 );



            Terminal_Para_Info pi = { 0 };

            pi.comm_fac_id  = term_id;

            pi.line_no      = line_no;

            pi.serial_num   = area_no;

            pi.para_type    = param_rec.param_type;

            pi.value_type   =param_rec.data_type;

            pi.data_type    = code;

            pi.result       = 104;

            para.terminal_para_seq.push_back(pi);

            sprintf(g_log_buf,"comm_fac_id=%ld line_no=%d area_no=%d para_type=%d value_type=%d data_type=%d result=%d ",

                    pi.comm_fac_id,pi.line_no,pi.serial_num,pi.para_type,pi.value_type,pi.data_type,pi.result);

            CLogManager::Instance()->WriteLineT(g_log_buf);

            break;



        }



    }

    para.num = para.terminal_para_seq.size();

    return 1;



}

void TermParamWindow::callParam_mul(const qint64 &  term_id,const qint32 &line_no)

{

    QString strOper = tr("批量终端召唤参数");





    if(m_pThread!=NULL)

    {

        delete m_pThread;

        m_pThread=NULL;

    }



    T_Terminal_Para call_para;

    int tmp_ret=init_term_call_para_mul(term_id, call_para,line_no );



    if(tmp_ret==-2)

    {

        sprintf(g_log_buf,"批量终端召唤失败未找到关联定值区...");

        CLogManager::Instance()->WriteLineT(g_log_buf);

        Terminal_Para_Info tmp_info;

        call_show_mul(term_id,-11,tmp_info,line_no);

        return;

    }



    if( call_para.num == 0 )

    {

        sprintf(g_log_buf,"批量终端召唤参数 请选择需要召唤的参数记录...");

        CLogManager::Instance()->WriteLineT(g_log_buf);

        Terminal_Para_Info tmp_info;

        check_show_mul(term_id,-13,tmp_info,line_no);

        //ZMessageBox::warning( this, strOper, tr("请勾选需要召唤的参数记录...") );

        return;

    }

    //激活参数

    short serv          = CH_DMS_TERMINAL_DATA;

    short event         = MT_DMS_TERM_PARA_CALL;;

    m_download_reply_event   = MT_DMS_TERM_PARA_REPLY;

    m_sheet_result=0;

    m_pThread=new MsgBus_RW_Thread();

    m_pThread->init( term_id, m_oper_timeout_slice );



    m_pThread->setRunFlag(0);

    if( m_pThread->init_msg( serv, event, m_download_reply_event, call_para ) != 0 )

    {

        sprintf(g_log_buf,"批量终端召唤参数 初始化失败...");

        CLogManager::Instance()->WriteLineT(g_log_buf);

        Terminal_Para_Info tmp_info;

        check_show_mul(term_id,-14,tmp_info,line_no);

        //QMessageBox::warning( this, tr("校验参数:"), tr("校验参数 初始化失败...") );

        //初始化失败

        return ;

    }

    m_pThread->setSheetFlag(true);



    m_pThread->start();

    sheet_exec_dlg_start();

    T_Terminal_Para reply_para;

    while(1)

    {



        usleep(5000);

        cout<<"while!!!!!!!!"<<endl;

        m_sheet_result=m_pThread->getParamReply(reply_para);

        if(m_sheet_result==0)

            continue;

        else

            break;

    }

    if(m_pThread!=NULL)

    {

        delete m_pThread;

        m_pThread=NULL;

    }

    int ret=m_sheet_result;

    QMap<qint32,Terminal_Para_Info> recv_code_map;

    CLogManager::Instance()->WriteLineT("批量终端召唤参数返回结果:");

    for( int i = 0; i < reply_para.terminal_para_seq.size(); i++ )

    {

        Terminal_Para_Info pi = reply_para.terminal_para_seq[i];

        sprintf(g_log_buf,"comm_fac_id=%ld line_no=%d area_no=%d para_type=%d value_type=%d data_type=%d value_f=%f value_i=%d result=%d ",

                pi.comm_fac_id,pi.line_no,pi.serial_num,pi.para_type,pi.value_type,pi.data_type,pi.value_f,pi.value_i,pi.result);

        CLogManager::Instance()->WriteLineT(g_log_buf);

        //  CLogManager::Instance()->WriteLineT(QString::fromUtf8(pi.value_s).toStdString());

    }

    if(ret==-1||ret==-2)

    {

        sprintf(g_log_buf,"批量终端召唤参数 消息总线注册失败...");

        CLogManager::Instance()->WriteLineT(g_log_buf);

        //QMessageBox::warning( this, tr("校验参数:"), tr("校验参数 消息总线注册失败...") );



    }

    else if(ret==-3)

    {

        sprintf(g_log_buf,"批量终端召唤参数 消息请求发送失败...");

        CLogManager::Instance()->WriteLineT(g_log_buf);

        //QMessageBox::warning( this, tr("校验参数:"), tr("校验参数 消息请求发送失败...") );



    }

    /* else if(ret==-4)

    {



    }*/

    else if(ret==-5)

    {

        sprintf(g_log_buf,"批量终端召唤参数 收到的消息长度小于0...");

        CLogManager::Instance()->WriteLineT(g_log_buf);

        //QMessageBox::warning( this, tr("校验参数:"), tr("校验参数 收到的消息长度小于0...") );



    }

    else if(ret==-6)//超时

    {





        for( int i = 0; i < call_para.terminal_para_seq.size(); i++ )

        {

            Terminal_Para_Info &info = call_para.terminal_para_seq[i];





            insertParamHisByPara_mul(line_no,term_id,info,1,-1,1);



        }

        sprintf(g_log_buf,"批量终端召唤参数 超时...");

        CLogManager::Instance()->WriteLineT(g_log_buf);



        // QMessageBox::warning( this, tr("校验参数:"), tr("校验参数 超时...") );

    }

    else if(ret==-7)//中止

    {

        for( int i = 0; i < call_para.terminal_para_seq.size(); i++ )

        {

            Terminal_Para_Info &info = call_para.terminal_para_seq[i];

            insertParamHisByPara_mul(line_no,term_id,info,1,-1,1);

        }

        sprintf(g_log_buf,"批量终端召唤参数 中止...");

        CLogManager::Instance()->WriteLineT(g_log_buf);

        // QMessageBox::warning( this, tr("校验参数:"), tr("校验参数 中止...") );



    }

    else//ret==1

    {



        update_progress_dlg(m_oper_timeout_slice);//接收完成

        SheetStopProgressStep();

        cout<<"reply_size=="<<reply_para.terminal_para_seq.size()<<endl;

        // QSet<qint32> recv_code;

        for( int i = 0; i < reply_para.terminal_para_seq.size(); i++ )

        {

            Terminal_Para_Info &info = reply_para.terminal_para_seq[i];



            recv_code_map[info.data_type]=info;

            call_show_mul(term_id,ret,info,line_no);

            insertParamHisByPara_mul(line_no,term_id,info,1,1,1);

            //show_check_para_info( info );

            //update_oper_result( info.data_type, 10/*info.result*/ );  //召唤的返回结果不可用

        }

        update_reply_call_para_mul(term_id, reply_para ,line_no);  //更新进商用数据库

        return ;

    }



    update_reply_call_para_mul(term_id, reply_para ,line_no);  //更新进商用数据库

    Terminal_Para_Info tmp_info;

    call_show_mul(term_id,ret,tmp_info,line_no);

    return ;

}

void TermParamWindow::update_reply_call_para_mul(const qint64 &term_id, T_Terminal_Para &para,const qint32 &line_no )

{

    int count = para.terminal_para_seq.size();

    //Q_ASSERT( para.num == count );



    int area_no = get_area_no_mul(term_id,line_no);

    //int line_no = get_line_no_mul(term_id);

    qint64 term_xh=get_xh_id_mul(term_id);

    QListTermParamRec lstParamRec;



    for( int m = 0; m < para.terminal_para_seq.size(); m++ )

    {

        Terminal_Para_Info &info = para.terminal_para_seq[m];







        DMS_TERM_PARAM_DICT_STRUCT param_rec = m_code_paramDict_map[QString::number(info.data_type,16)];

        if( QString(param_rec.param_code).toInt( NULL, 16 ) == info.data_type )

        {





            TermParamRec update_rec;

            update_rec.m_term_id        = term_id;                              //终端ID

            update_rec.m_xh_id          = term_xh;                               //型号ID

            update_rec.m_line_no        = line_no+TERM_PARAM_LINE_NO_BASE;                                          //微机保护线路号

            update_rec.m_area           = area_no;//info.serial_num;                        //定值区 这里取召唤的定值区号 因为上送的有问题

            update_rec.m_code           = QString::fromStdString( param_rec.param_code );   //参数代码(16进制不带0x)

            update_rec.m_code_10        = update_rec.m_code.toInt( NULL, 16 );              //10进制参数代码

            update_rec.m_name           = QString::fromStdString(param_rec.name );          //参数名称

            update_rec.m_type           = param_rec.param_type;                             //参数类别(0-运行参数;1-动作定值;2-固有参数)

            info.para_type              = param_rec.param_type;     //上送的参数类别不可信 以定义的为准

            update_rec.m_value          = param_value_to_string( info );                    //参数值

            update_rec.m_data_type      = param_rec.data_type;                              //参数值数据类型(1-类型不确定;2-int;3-float;4-short;5-unsigned short;6-unsigned int;7-string)

            update_rec.m_unit           = QString::fromStdString( param_rec.unit );         //参数单位

            update_rec.m_coe            = param_rec.coe;                                    //参数系数

            update_rec.m_data_source    = 1;                                                //数据来源(1-1区;2-四区;3-其他)

            update_rec.m_update_time    = QDateTime::currentDateTime();                     //最近更新时间



            lstParamRec.append( update_rec );



            break;

        }



    }

    //校验召唤上来的参数写库 但不更新界面

    TermParam_Write_Thread *pWriteThread = new TermParam_Write_Thread( this );

    pWriteThread->setUserName(g_strLoginUserName);

    connect( pWriteThread, SIGNAL(finished()), pWriteThread, SLOT( deleteLater() ) );

    // connect( pWriteThread, SIGNAL(WriteFinished(int)), this, SLOT( OnParamWriteFinished(int) ), Qt::DirectConnection );

    pWriteThread->init( lstParamRec );

    pWriteThread->start();

}

QString get_call_result(int result)

{

    switch(result)

    {

    case 1:

        return QString("召唤成功");

    case -8:

        return QString("召唤失败 返回失败");

    case -9:

        return QString("召唤失败 失败");

    case -7:

        return QString("召唤失败 中止");

    case -6:

        return QString("召唤失败 超时");

    case -2:

    case -1:

        return QString("召唤失败 消息总线注册失败");

    case -3:

        return QString("召唤失败 消息发送失败");

    case -5:

        return QString("召唤失败 消息长度为0");

    case -11:

        return QString("召唤失败 获取定值区失败");

    case -13:

        return QString("召唤失败 请选择需要下装的参数记录");

    case -14:

        return QString("召唤失败 初始化失败");

    default:

        return QString("未标识校验返回值");



    }

}

void TermParamWindow::call_show_mul(const qint64 &term_id,const int &result,const Terminal_Para_Info &info,const qint32 &line_no)

{

#ifdef SHOW_MUL_CALL_RESULT

    QStandardItemModel *pModel = NULL;



    pModel = (QStandardItemModel *)ui->tableView_term_mul->model();

    if( pModel == NULL ) return;



    for( int row = 0; row < pModel->rowCount(); row++ )

    {



        QStandardItem *pItem = pModel->item( row, MUL_TERM_COL_CALL_PARA);

        if( pItem == NULL ) continue;





        QStandardItem *pItemName = pModel->item( row, MUL_TERM_COL_NAME );

        if( pItemName == NULL ) continue;

        qint64 tmp_term_id=pItemName->data(Qt::UserRole+10).toLongLong();

        qint64 tmp_line_no=pItemName->data(Qt::UserRole+12).toInt();

        if(pItemName->checkState()==Qt::Checked&&tmp_term_id==term_id&&line_no==tmp_line_no)

        {

            QString strResult=get_call_result(result);

            qDebug()<<"reslut_str"<<strResult<<endl;

            pItem->setText(strResult);

            if(result==1)

            {

                QStandardItem *pItemValue = pModel->item( row, MUL_TERM_COL_STATUS );

                if( pItemValue == NULL ) continue;

                if(info.value_i==1)

                    pItemValue->setText(tr("投入"));

                else

                    pItemValue->setText(tr("退出"));





            }



        }

    }

#endif

}

QString TermParamWindow::getDataName(const qint32 value)

{

   static QMap<qint32,SYS_MENU_INFO_STRUCT> value_info_map;

   if(value_info_map.find(value)!=value_info_map.end())

   {

       return QString("%1(%2)").arg(value_info_map[value].display_value).arg(value) ;

   }

    QListSysMenuInfo lstSysMenuInfo;

    if( ! m_rtdbOper.GetSysMenuInfo( lstSysMenuInfo, tr("终端参数值数据类型") ) ) return "null";



    SYS_MENU_INFO_STRUCT menu;

    foreach( menu, lstSysMenuInfo )

    {

        value_info_map[menu.actual_value]=menu;

    }



    if(value_info_map.find(value)!=value_info_map.end())

    {

         return QString("%1(%2)").arg(value_info_map[value].display_value).arg(value) ;

    }

    else

    {

        return QString("null(%1)").arg(value);

    }



}
