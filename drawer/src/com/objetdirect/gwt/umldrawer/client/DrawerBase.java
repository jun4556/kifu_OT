package com.objetdirect.gwt.umldrawer.client;

import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
//add saito
import com.google.gwt.user.client.Timer;
//import java.util.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DeckPanel;
//20251014
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.view.client.SingleSelectionModel;
import com.objetdirect.gwt.umlapi.client.artifacts.UMLArtifact;
import com.objetdirect.gwt.umlapi.client.helpers.Session;
import com.objetdirect.gwt.umlapi.client.helpers.UMLCanvas;
import com.objetdirect.gwt.umlapi.client.mylogger.MyLoggerExecute;
import com.objetdirect.gwt.umlapi.client.umlcomponents.UMLDiagram;
import com.objetdirect.gwt.umlapi.client.umlcomponents.UMLLink.LinkKind;
import com.objetdirect.gwt.umlapi.client.yamazaki.replace.AbstDrawReplaceAddDelete;
import com.objetdirect.gwt.umldrawer.client.beans.DesignPattern;
import com.objetdirect.gwt.umldrawer.client.beans.EditEvent;
import com.objetdirect.gwt.umldrawer.client.beans.Exercise;
import com.objetdirect.gwt.umldrawer.client.canvas.CanvasService;
import com.objetdirect.gwt.umldrawer.client.canvas.CanvasServiceAsync;
import com.objetdirect.gwt.umldrawer.client.drawerparts.yamazaki.DiffReplacePanel;
import com.objetdirect.gwt.umldrawer.client.exercise.ExerciseService;
import com.objetdirect.gwt.umldrawer.client.exercise.ExerciseServiceAsync;
import com.objetdirect.gwt.umldrawer.client.helpers.DrawerSession;
import com.objetdirect.gwt.umldrawer.client.helpers.WebSocketClient;
import com.objetdirect.gwt.umldrawer.client.saito.SimService;
import com.objetdirect.gwt.umldrawer.client.saito.SimServiceAsync;
import com.objetdirect.gwt.umldrawer.client.yamazaki.diffservice.DiffService;
import com.objetdirect.gwt.umldrawer.client.yamazaki.diffservice.DiffServiceAsync;
import com.objetdirect.gwt.umldrawer.client.yamazaki.thread.feedback.CatchSourceCodeChengeState;
import com.objetdirect.gwt.umldrawer.client.yamazaki.thread.feedback.IDrawerBaseConectThread;



public class DrawerBase extends DockPanel implements IDrawerBaseConectThread{
	private SimplePanel centerPanel;
	private VerticalPanel leftSideBar;
	private VerticalPanel rightSideBar;
	private HorizontalPanel northBar;
	private HorizontalPanel southBar;

	private UMLCanvas uMLCanvas;
	private DrawerPanel drawerPanel;

	private WebSocketClient webSocketClient;
	private Timer syncTimer;
	private String lastCanvasUrl = "";
	private boolean isUpdating = false;
	private SingleSelectionModel<String> selectionModel	= new SingleSelectionModel<String>();

	private EditEventPanel editEventPanel;
	private Exercise exercise;
	private DeckPanel mainPanel = new DeckPanel();
	private String DEFAULT_MODEL = "PDA+XUNsYXNzJCg1OCwxMTUpIeODpuODvOOCtiEhLeODpuODvOOCtklEJS3jg5"
			+ "Hjgrnjg6/jg7zjg4klLeawj+WQjSUt5L2P5omAJS3nmbvpjLLml6XmmYIlITs8MT5dQ2xhc3MkKDM2MCw3Mykh5"
			+ "ZWG5ZOBISEt5ZWG5ZOB5ZCNJS3ljZjkvqElLeiqrOaYjiUt55m76Yyy5pel5pmCJS3llYblk4Hnlarlj7clLeWcqOW6qy"
			+ "Ut5rOo5paH55Wq5Y+3JS3ms6jmlofml6XmmYIlLeWAi+aVsCUhOzwyPl1DbGFzcyQoMjA0LDM0OCkh44Os44OT44"
			+ "Ol44O844GZ44KLISEt44Os44OT44Ol44O8JS3ngrnmlbAlLeODrOODk+ODpeODvOipleS+oSUhOzwzPl1DbGFzc1Jlb"
			+ "GF0aW9uTGluayQ8Mj4hPDA+IVNpbXBsZVJlbGF0aW9uISFTb2xpZCFOb25lITAuLjEhISFOb25lITEhITs8ND5dQ2xhc3"
			+ "NSZWxhdGlvbkxpbmskPDI+ITwxPiFTaW1wbGVSZWxhdGlvbiEhU29saWQhTm9uZSEwLi4qISEhTm9uZSExISE7PDU+"
			+ "XUNsYXNzUmVsYXRpb25MaW5rJDwxPiE8MD4hU2ltcGxlUmVsYXRpb24hIVNvbGlkIU5vbmUhMC4uKiEhIU5vbmUhMSEhOw==";

	private String DEFAULT_MODEL2 = "PDA+XUNsYXNzJCgxNDUsMTM0KSHnpL7lk6EhIS3npL7lk6FJRCUt44OR44K544Ov44O844OJJS3jg"
			+ "6bjg7zjgrbjgr/jgqTjg5clITs8MT5dQ2xhc3MkKDQzMywxMDgpIeODl+ODreOCuOOCp+OCr+ODiOeZu+mMsiEhLeODl+ODreOCu"
			+ "OOCp+OCr+ODiOWQjSUt6ZaL5aeL5pel5pmCJS3jg57jg43jg7zjgrjjg6MlLeODl+ODreOCsOODqeODniUt44OH44K244Kk44OKJS3"
			+ "jg4bjgrnjgr/jg7wlITs8NT5dQ2xhc3NSZWxhdGlvbkxpbmskPDA+ITwxPiFTaW1wbGVSZWxhdGlvbiEhU29saWQhTm9uZSEwLi4qIS"
			+ "EhTm9uZSExISE7";
	//追加
	private Label label = new Label(DrawerTextResource.NOT_SELECT_PATTERN_MESSAGE1.getMessage());
	private Label label2 = new Label();
	//private ListBox patternlist = new ListBox();
	private DesignPattern pattern = new DesignPattern();

	// add Yamazaki
	private final String SURPLUS = "余剰";
	private final String NOTHAS = "欠損";
	private Button diff_button_;
	private final CatchSourceCodeChengeState chathcodestate_;

	//	add Saito
	private final String MATCH = "一致";
	private final String MISMATCH = "不一致";

	public static Timer countTimer;
    private long lastCalledTime = 0;

    private final double diffSim = 0.05;
    private double lastTimeSim = 0;
    private double thisTimeSim;

	public DrawerBase() {
		super();

		// コードの状態を取得してくれるRPC
		chathcodestate_ = new CatchSourceCodeChengeState(this);
//		scheduleFixedDelay_Yamazaki();

		//		this.sinkEvents(Event.ONCONTEXTMENU);
		//	    this.addHandler(
		//	      new ContextMenuHandler() {
		//	        @Override
		//	        public void onContextMenu(ContextMenuEvent event) {
		//	          event.preventDefault();
		//	          event.stopPropagation();
		//	    }
		//	    }, ContextMenuEvent.getType());
		this.setHorizontalAlignment(ALIGN_LEFT);
		this.centerPanel = new SimplePanel();
		this.leftSideBar = new VerticalPanel();
		this.rightSideBar = new VerticalPanel();
		this.northBar = new HorizontalPanel();
		this.southBar = new HorizontalPanel();
		leftSideBar.setWidth("100px");
		rightSideBar.setWidth("150px");



		// add Yamazaki
	/*	diff_button_ = new Button(DrawerTextResource.DIFF_MESSAGE.getMessage());
		diff_button_.setSize("95px", "44px");
		diff_button_.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent arg0) {
				// TODO 自動生成されたメソッド・スタブ
				//　通知後に差分確認したらテキストを元に戻す
				setDiffButtonDisplayedSentence(DrawerTextResource.DIFF_MESSAGE.getMessage());
				chathcodestate_.changedCodeStatetoTrue();

				//　ダイアログ
				DetectionBetweenDiagramCode();
			}
		});
		leftSideBar.add(diff_button_);  */

		//クラス図用ボタン群
		if(Session.diagramType == UMLDiagram.Type.CLASS){
			//*********************************************************************************************************
			Button addNewClass = new Button(DrawerTextResource.ADD_CLASS.getMessage());
			addNewClass.setSize("95px", "44px");
			addNewClass.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					uMLCanvas.addNewClass();
				}
			});
			leftSideBar.add(addNewClass);


		}


		Button addNewRelation = new Button(DrawerTextResource.RELATION_BUTTON.getMessage());
		addNewRelation.setSize("95px", "44px");
		addNewRelation.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				uMLCanvas.toLinkMode(LinkKind.getRelationKindFromName("SimpleRelation"));

			}
		});
		leftSideBar.add(addNewRelation);

		Button undo = new Button(DrawerTextResource.UNDO.getMessage());
		undo.setSize("95px", "44px");
		undo.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				String canvasUrl=null;
				int idCount=0;
				CanvasServiceAsync async = (CanvasServiceAsync)GWT.create(CanvasService.class);
				ServiceDefTarget entryPoint = (ServiceDefTarget) async;
				String entryURL = GWT.getModuleBaseURL() + "undo";
				entryPoint.setServiceEntryPoint(entryURL);

				@SuppressWarnings("rawtypes")
				AsyncCallback callback = new AsyncCallback(){
					@SuppressWarnings({ "unchecked", "deprecation" })
					public void onSuccess(Object result){

						if( !( (EditEvent) result == null) ){
							Session.setMode("load");
							Session.getActiveCanvas().clearCanvas();
							Session.getActiveCanvas().fromURL( ((EditEvent) result).getCanvasUrl() , false);
							uMLCanvas = Session.getActiveCanvas();
							Session.setMode("drawer");
							//							int preEventId, String editEvent, String eventType,
							//							String targetType, int targetId, String linkKind, int rightObjectId, int leftObjectId,
							//							String targetPart, String beforeEdit, String afterEdit, String canvasUrl
							MyLoggerExecute.registEditEvent(-1, ""+((EditEvent) result ).getEditEventId(), "Undo",
									null, -1, null, -1, -1,
									null, null, null, Session.getActiveCanvas().toUrl(), UMLArtifact.getIdCount());
						}
					}

					public void onFailure(Throwable caught){
						System.out.println(caught);
					}
				};

				async.undo(Session.studentId, Session.exerciseId, callback);
			}
		});
		leftSideBar.add(undo);
/*
		Button checkedButton = new Button(DrawerTextResource.SC_BUTTON.getMessage());
		checkedButton.setSize("95px", "48px");
		checkedButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
					CheckPanel checkPanel = new CheckPanel();
					checkPanel.setPopupPosition(10, 180);
					checkPanel.show();
				//				int preEventId, String editEvent, String eventType,
				//				String targetType, int targetId, String linkKind, int rightObjectId, int leftObjectId,
				//				String targetPart, String beforeEdit, String afterEdit, String canvasUrl
				//					MyLoggerExecute.registEditEvent(-1, "Check", "Check",
				//							null, -1, null, -1, -1,
				//							null, null, null,Session.getActiveCanvas().toUrl(), UMLArtifact.getIdCount());
			}
		});
		leftSideBar.add(checkedButton);
		*/
//
		leftSideBar.setSpacing(20); //ボタンの間隔
		//**************************************************************************************************
		//クラス図用ボタン終わり

		Button save = new Button(DrawerTextResource.SAVE.getMessage());
		save.setSize("95px", "88px");
		save.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				//								int preEventId, String editEvent, String eventType,
				//				String targetType, int targetId, String linkKind, int rightObjectId, int leftObjectId,
				//				String targetPart, String beforeEdit, String afterEdit, String canvasUrl
				MyLoggerExecute.registEditEvent(-1, "Save", "Save",
						null, -1, null, -1, -1,
						null, null, null,Session.getActiveCanvas().toUrl(), UMLArtifact.getIdCount());
				Window.alert("図を保存しました！");

			}
		});
		leftSideBar.add(save);
		
		//20251014**********************************************************************

		Button mergeButton = new Button("マージ"); // ボタンのテキスト
		mergeButton.setSize("95px", "48px");
		mergeButton.addClickHandler(new ClickHandler() {
		    @Override
		    public void onClick(ClickEvent event) {
		        // ボタンが押されたら、マージ用のダイアログを表示する
		        showMergeDialog();
		    }
		});
		leftSideBar.add(mergeButton); // 左サイドバーにボタンを追加

		//拡張部分
		//**************************************************************************************************
/*		final ListBox patternlist = new ListBox();
		patternlist.addItem("prototype");
		patternlist.addItem("template method");
		patternlist.addItem("Adapter");


		patternlist.setVisibleItemCount(1);

		leftSideBar.add(patternlist);
		patternlist.setEnabled(true);

		patternlist.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				listIndex = patternlist.getSelectedIndex();
			}
		});

        // メニューバー選択時コマンド
        final Command command = new Command() {
            @Override
            public void execute() {
            	getItemIndex(getSelectedItem);
                Window.alert("項目が選択されました。");
            }
        };

*/
        // パターンのリストをメニューバーで実装
//        final MenuBar patternBar = new MenuBar(true);
//        final MenuBar spotBar = new MenuBar(true);
//		PatternServiceAsync async = (PatternServiceAsync)GWT.create(PatternService.class);
//		ServiceDefTarget entryPoint = (ServiceDefTarget) async;
//		String entryURL = GWT.getModuleBaseURL() + "getPatternNameList";
//		entryPoint.setServiceEntryPoint(entryURL);
//
//		@SuppressWarnings("rawtypes")
//		AsyncCallback callback = new AsyncCallback(){
//			@SuppressWarnings({ "unchecked", "deprecation" })
//			public void onSuccess(Object result){
//				final List<String> list = new ArrayList<String>();
//				list.addAll((List<String>) result);
//				pattern.setPatternListSize(list.size());
//
//				for(final String str : list) {
//					patternBar.addItem(str, new Command() {
//			        	public void execute() {
//			        		pattern.setPatternIndex(list.indexOf(str)+1);
//			        		northBar.clear();
/*
			        		spotBar.clearItems();
							PatternServiceAsync async2 = (PatternServiceAsync)GWT.create(PatternService.class);
							ServiceDefTarget entryPoint = (ServiceDefTarget) async2;
							String entryURL = GWT.getModuleBaseURL() + "getPatternSpotList";
							entryPoint.setServiceEntryPoint(entryURL);

							@SuppressWarnings("rawtypes")
							AsyncCallback callback2 = new AsyncCallback(){
								@Override
								public void onSuccess(Object result) {
									// TODO 自動生成されたメソッド・スタブ
									final List<String> list = new ArrayList<String>();
									list.addAll((List<String>) result);
									for(final String str : list) {
										spotBar.addItem(str, new Command() {
								        	public void execute() {
								        		setSpotName(str);
								        	}
										});
									}
								}

								@Override
								public void onFailure(Throwable caught) {
									// TODO 自動生成されたメソッド・スタブ
									System.out.println(caught);
								}
							};
							async2.getPatternSpotList(patternIndex, callback2);
							*/
//			        	}
//					});
//				}
//			}
//			public void onFailure(Throwable caught){
//				System.out.println(caught);
//			}
//		};
//		async.getPatternNameList(callback);
		/*
        patternBar.addItem("Prototype", new Command() {
        	@Override
        	public void execute() {
        		setPatternIndex(1);
        		//classcount = 3;
        	}
        });
        patternBar.addItem("Adapter", new Command() {
        	@Override
        	public void execute() {
        		setPatternIndex(2);
        		//classcount = 4;
        	}
        });
        patternBar.addItem("template method", new Command() {
        	@Override
        	public void execute() {
        		setPatternIndex(3);
        		//classcount = 2;
        	}
        });
*/
//        final MenuBar patternSelectBar = new MenuBar(false);
//        patternSelectBar.addItem(DrawerTextResource.SELECT_PATTERN.getMessage(), patternBar);
//        patternSelectBar.setSize("95px", "44px");
//
//        leftSideBar.add(patternSelectBar);
/*
        //パターンのホットスポット部の選択リスト
        final MenuBar spotSelectBar = new MenuBar(false);
        spotSelectBar.addItem(DrawerTextResource.SELECT_SPOT.getMessage(), spotBar);
        //leftSideBar.add(spotSelectBar);


        //既存のクラスの選択をメニューバーで実装
		final MenuBar classBar = new MenuBar(true);
		//final MenuBar classBarB = new MenuBar(true);
		final MenuBar classSelectBar = new MenuBar(false);

		//既存のクラス図の取得を行うボタンの実装
		final Button class_set = new Button(DrawerTextResource.SET_TO_CLASS.getMessage());
		class_set.setSize("95px", "44px");
		class_set.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				classBar.clearItems();

				//label.setText("合成元のクラスを選択してください");
				//leftSideBar.add(label);

				PatternServiceAsync async = (PatternServiceAsync)GWT.create(PatternService.class);
				ServiceDefTarget entryPoint = (ServiceDefTarget) async;
				String entryURL = GWT.getModuleBaseURL() + "getUMLClassDiagramList";
				entryPoint.setServiceEntryPoint(entryURL);

				//final String str = "<0>]Class$(186,196)!Client!!!;<1>]Class$(357,184)!Prototype!!!+ConcreteClone() : void%;<2>]Class$(369,344)!ConcretePrototype!!!+ConcreteClone() : void%;<15>]ClassRelationLink$<1>!<2>!SimpleRelation!Name!Solid!None!None!!!None!None!!;<16>]ClassRelationLink$<0>!<1>!SimpleRelation!Name!Solid!None!None!!!None!None!!;";

				@SuppressWarnings("rawtypes")
				AsyncCallback callback = new AsyncCallback(){
					@SuppressWarnings({ "unchecked", "deprecation" })
					public void onSuccess(Object result){

						for(final String str : (List<String>) result) {

							classBar.addItem(str, new Command() {
								public void execute() {
									pattern.setClassName(str);
			        			}
							});
						}
/*
				        classSelectBar.addItem("クラス選択", classBarA);
				        classSelectBar.addItem("パターンクラス選択", classBarB);

				        leftSideBar.add(classSelectBar);
					}

					public void onFailure(Throwable caught){
						System.out.println(caught);
					}
				};
				async.getUMLClassDiagramList(Session.studentId, Session.exerciseId, callback);
				class_set.setEnabled(true);

		        //classSelectBar.addItem(DrawerTextResource.SELECT_CLASS.getMessage(), classBar);
		        //classSelectBar.addItem("パターンクラス選択", classBarB);
			}
		});
		classSelectBar.addItem(DrawerTextResource.SELECT_CLASS.getMessage(), classBar);
		leftSideBar.add(class_set);
        leftSideBar.add(classSelectBar);
        */

        //合成箇所選択ボタン(PopupPanelで実装)
  /* Button spotSelect = new Button(DrawerTextResource.SELECT_SPOT.getMessage());
        spotSelect.setSize("95px", "44px");
        spotSelect.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent arg0) {
				// TODO 自動生成されたメソッド・スタブ
				if(pattern.getPatternIndex() != -1) {
					//Window.alert(String.valueOf(pattern.getPatternIndex()));
					SpotSelectPanel spotSelectPanel = new SpotSelectPanel(pattern.getPatternIndex(), pattern.getClassName());
					spotSelectPanel.setPopupPosition(10, 350);
					spotSelectPanel.show();
				}
				else {
					Window.alert(DrawerTextResource.NOT_SELECT_PATTERN_MESSAGE1.getMessage() + "\n" + DrawerTextResource.NOT_SELECT_PATTERN_MESSAGE2.getMessage());
				}
				if(pattern.getFlag()) {
					pattern.setPatternIndex(-1);
				}
			}
        });
        leftSideBar.add(spotSelect);   */

        //合成ボタンの実装
        /*
		final Button combine = new Button(DrawerTextResource.COMBINE.getMessage());
		combine.setSize("95px", "44px");

		combine.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if(pattern.getPatternIndex() != -1 && ! (pattern.getClassName().equals(""))) {
					//                                               UMLId = UMLArtifact.getIdCount();
					//s = String.valueOf(UMLId);
					//Window.alert(s);
					PatternServiceAsync async = (PatternServiceAsync)GWT.create(PatternService.class);
					ServiceDefTarget entryPoint = (ServiceDefTarget) async;
					String entryURL = GWT.getModuleBaseURL() + "getCombineUrl2";
					entryPoint.setServiceEntryPoint(entryURL);

					//final String str = "<0>]Class$(186,196)!Client!!!;<1>]Class$(357,184)!Prototype!!!+ConcreteClone() : void%;<2>]Class$(369,344)!ConcretePrototype!!!+ConcreteClone() : void%;<15>]ClassRelationLink$<1>!<2>!SimpleRelation!Name!Solid!None!None!!!None!None!!;<16>]ClassRelationLink$<0>!<1>!SimpleRelation!Name!Solid!None!None!!!None!None!!;";

					@SuppressWarnings("rawtypes")
					AsyncCallback callback = new AsyncCallback(){
						@SuppressWarnings({ "unchecked", "deprecation" })
						public void onSuccess(Object result){
							//Window.alert((String) result);
							if( !( (String) result == null) ){
							Session.setMode("load");
							Session.getActiveCanvas().clearCanvas();
							Session.getActiveCanvas().fromURL( ((String) result) , false);
							uMLCanvas = Session.getActiveCanvas();
							Session.setMode("drawer");
							//							int preEventId, String editEvent, String eventType,
							//							String targetType, int targetId, String linkKind, int rightObjectId, int leftObjectId,
							//							String targetPart, String beforeEdit, String afterEdit, String canvasUrl
							MyLoggerExecute.registEditEvent(-1, "" + ((String) result ), "GetCombineUrl2",
									null, -1, null, -1, -1,
									null, null, null, Session.getActiveCanvas().toUrl(), UMLArtifact.getIdCount());
							}

						}

						public void onFailure(Throwable caught){
							System.out.println(caught);
						}
					};
					async.getCombineUrl(Session.studentId, Session.exerciseId, pattern.getPatternIndex(), pattern.getClassName(), pattern.getSpotItem(), callback);
					pattern.setPatternIndex(-1);

					//combine.setEnabled(false);
				}
				else {
					if(pattern.getPatternIndex() == -1 && ! (pattern.getClassName().equals(""))) {
						Window.alert(DrawerTextResource.MESSAGE1_1.getMessage() + "\n" + DrawerTextResource.MESSAGE1_2.getMessage());
					}
					else if(pattern.getPatternIndex() != -1 && pattern.getClassName().equals("")) {
						Window.alert(DrawerTextResource.MESSAGE2_1.getMessage() + "\n" + DrawerTextResource.MESSAGE2_2.getMessage());
					}
					else {
						Window.alert(DrawerTextResource.MESSAGE3_1.getMessage() + "\n" + DrawerTextResource.MESSAGE3_2.getMessage());
					}

				}

			}
		});

		leftSideBar.add(combine);
*/
        //パターン追加ボタン
	/*	Button add_pattern = new Button(DrawerTextResource.PATTERN.getMessage());
		add_pattern.setSize("95px", "44px");
		add_pattern.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if(pattern.getPatternIndex() != -1) {

					PatternServiceAsync async = (PatternServiceAsync)GWT.create(PatternService.class);
					ServiceDefTarget entryPoint = (ServiceDefTarget) async;
					String entryURL = GWT.getModuleBaseURL() + "getPatternList";
					entryPoint.setServiceEntryPoint(entryURL);

					//final String str = "<0>]Class$(186,196)!Client!!!;<1>]Class$(357,184)!Prototype!!!+ConcreteClone() : void%;<2>]Class$(369,344)!ConcretePrototype!!!+ConcreteClone() : void%;<15>]ClassRelationLink$<1>!<2>!SimpleRelation!Name!Solid!None!None!!!None!None!!;<16>]ClassRelationLink$<0>!<1>!SimpleRelation!Name!Solid!None!None!!!None!None!!;";

					@SuppressWarnings("rawtypes")
					AsyncCallback callback = new AsyncCallback(){
						@SuppressWarnings({ "unchecked", "deprecation" })
						public void onSuccess(Object result){

							if( result != null) {
								Session.setMode("load");
								//Session.getActiveCanvas().clearCanvas();
								Session.getActiveCanvas().fromURL( (String) result, false);
								uMLCanvas = Session.getActiveCanvas();
								Session.setMode("drawer");

								//int preEventId, String editEvent, String eventType,
								//String targetType, int targetId, String linkKind, int rightObjectId, int leftObjectId,
								//String targetPart, String beforeEdit, String afterEdit, String canvasUrl
								MyLoggerExecute.registEditEvent(-1, ""+((String) result ), "GetPatternList",
										null, -1, null, -1, -1,
										null, null, null, Session.getActiveCanvas().toUrl(), UMLArtifact.getIdCount());
							}
						}

						public void onFailure(Throwable caught){
							System.out.println(caught);
						}
					};

					async.getPatternList(Session.studentId, Session.exerciseId, pattern.getPatternIndex(), callback);
					pattern.setPatternIndex(-1);
				} else {
					Window.alert(DrawerTextResource.NOT_SELECT_PATTERN_MESSAGE1.getMessage() + "\n" + DrawerTextResource.NOT_SELECT_PATTERN_MESSAGE2.getMessage());
				}

				//Window.alert(patternlist.getItemText(patternlist.getSelectedIndex()));
			}
		});
		leftSideBar.add(add_pattern);  */

		/*
        Button spotSelect2 = new Button(DrawerTextResource.SELECT_SPOT2.getMessage());
        spotSelect2.setSize("95px", "44px");
        spotSelect2.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent arg0) {
				// TODO 自動生成されたメソッド・スタブ
				SpotSelectPanel spotSelectPanel = new SpotSelectPanel();
				spotSelectPanel.setPopupPosition(1000, 10);
				spotSelectPanel.show();

			}
        });
        rightSideBar.add(spotSelect2);
		*/

		//パターン登録ボタン(PopupPanelで実装)
		/*Button pattern_save = new Button(DrawerTextResource.SAVE_PATTERN.getMessage());
		pattern_save.setSize("95px", "44px");
		pattern_save.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent arg0) {
				// TODO 自動生成されたメソッド・スタブ
				PatternSavePanel patternSavePanel = new PatternSavePanel(pattern.getPatternListSize());
				patternSavePanel.setPopupPosition(450, 250);
				patternSavePanel.show();

				
				PatternServiceAsync async = (PatternServiceAsync)GWT.create(PatternService.class);
				ServiceDefTarget entryPoint = (ServiceDefTarget) async;
				String entryURL = GWT.getModuleBaseURL() + "savePatternList";
				entryPoint.setServiceEntryPoint(entryURL);

				AsyncCallback callback = new AsyncCallback() {

					@Override
					public void onFailure(Throwable caught) {
						// TODO 自動生成されたメソッド・スタブ
						System.out.println(caught);
					}

					@Override
					public void onSuccess(Object result) {
						// TODO 自動生成されたメソッド・スタブ

					}
				};

//				async.savePatternList(getPatternIndex(), callback);
				 
			}
		});
		leftSideBar.add(pattern_save);
		*/
		
//add saito***************************************************************

		//実験群用
		Button layout = new Button(DrawerTextResource.LAYOUT_CHANGE.getMessage());
        layout.setSize("95px", "48px");
        layout.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
            	 lastCalledTime = System.currentTimeMillis();

            	layoutChange();
            	//DBにデータが反映される前に座標を読み込まないように1秒待つ
            	Timer timer = new Timer() {
                    @Override
                    public void run() {
                    	reloadCanvas();
                    	//Canvasを書き換える前に色をつけるのを防ぐ
                    	Timer timer = new Timer() {
                            @Override
                            public void run() {
                            	colorChange();
                            }
                        };
                        timer.schedule(1000);
                    }
                };

                timer.schedule(1000);
            }
        });
        leftSideBar.add(layout);

        //saito  統制群用

//		Button simColor = new Button(DrawerTextResource.COLOR_CHANGE.getMessage());
//        simColor.setSize("95px", "48px");
//        simColor.addClickHandler(new ClickHandler() {
//            @Override
//            public void onClick(ClickEvent event) {
//            	removeCanvas();
//                //DBにデータが反映される前に座標を読み込まないように1秒待つ
//            	Timer timer = new Timer() {
//                    @Override
//                    public void run() {
//                    	reloadCanvas();
//                    	//Canvasを書き換える前に色をつけるのを防ぐ
//                    	Timer timer = new Timer() {
//                            @Override
//                            public void run() {
//
//                            	colorChange();
//
//                            }
//                        };
//                        timer.schedule(1000);
//                    }
//                };
//
//                timer.schedule(1000);
//            }
//        });
//        leftSideBar.add(simColor);

      //saito 2024 6月修士実験用
     if(Session.exerciseId == 21) {

    	 countTimer = new Timer() {
             @Override
             public void run() {
             	 if (System.currentTimeMillis() - lastCalledTime > 60000) {
             		 getSim();

                  	if(simCheck()) {

                  	}else {
                  		Window.alert("チェックを開始します");
                  		//以下layoutボタンと同じ処理
                  		layoutChange();
                       	Timer timer = new Timer() {
                               @Override
                               public void run() {
                               	reloadCanvas();
                               	Timer timer = new Timer() {
                                       @Override
                                       public void run() {
                                       	colorChange();
                                       }
                                   };
                                   timer.schedule(1000);
                               }
                           };

                           timer.schedule(1000);
                  	}
                  	lastTimeSim=thisTimeSim;
             		lastCalledTime = System.currentTimeMillis();

             	 }
             }
         };
         countTimer.scheduleRepeating(5000);
     }



        //**************************************************************************************************

		//教授者用
		if(DrawerSession.student.getType()==1){
			Button saveAsAnswer = new Button("解答例として保存");
			saveAsAnswer.setSize("100px", "88px");
			saveAsAnswer.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					Window.alert("Save as an answer");
					CanvasServiceAsync async = (CanvasServiceAsync)GWT.create(CanvasService.class);
					ServiceDefTarget entryPoint = (ServiceDefTarget) async;
					String entryURL = GWT.getModuleBaseURL() + "saveCanvasAsAnswer";
					entryPoint.setServiceEntryPoint(entryURL);

					@SuppressWarnings("rawtypes")
					AsyncCallback callback = new AsyncCallback(){
						@SuppressWarnings({ "unchecked", "deprecation" })
						public void onSuccess(Object result){
							if((Boolean)result)
								Window.alert("Saved as an answer");
						}

						public void onFailure(Throwable caught){
							System.out.println(caught);
						}
					};

					async.saveCanvasAsAnswer(Session.studentId, Session.exerciseId, uMLCanvas.toUrl() , callback);
				}

			});
			leftSideBar.add(saveAsAnswer);
		}
		//rightSideBar.setSpacing(5);

		Button button1 = new Button("Drawer");
		button1.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				mainPanel.showWidget(0);
				MyLoggerExecute.registEditEvent(-1, "LookDrawer", "LookDrawer",
						null, -1, null, -1, -1,
						null, null, null, null, UMLArtifact.getIdCount());
			}
		});

		Button button2 = new Button("Task");
		button2.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				mainPanel.showWidget(1);
				MyLoggerExecute.registEditEvent(-1, "LookTask", "LookTask",
						null, -1, null, -1, -1,
						null, null, null, null, UMLArtifact.getIdCount());
			}
		});
		//northBar.add(button1);
		//northBar.add(button2);

		//Button setting END

		getExercise(Session.exerciseId);

		// WebSocketの接続処理を開始！
		// まず、後でWebSocketClientで使えるようにdrawerPanelを生成して変数に入れておく
		this.drawerPanel = new DrawerPanel(this);
		mainPanel.add(this.drawerPanel);

		// ★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★
		// ★★★ ここからが修正箇所だぞ！ ★★★
		// ★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★★

		// 演習IDを使ってWebSocketに接続するメソッドを呼び出す！
		connectToExerciseChannel(String.valueOf(Session.exerciseId));

		// ★★★ OT方式を採用したため、全体同期タイマーを無効化 ★★★
		// 理由: 0.5秒ごとの全体同期がOT操作を上書きし、共同編集が不安定になるため
		// OT方式では操作単位で同期するため、全体同期は不要
		/*
		// 監視タイマーをセットアップするぞ！
		this.syncTimer = new Timer() {
		    @Override
		    public void run() {
		        // もし他の人からの更新を反映している最中じゃなければ、監視を実行する
		        if (!isUpdating && Session.getActiveCanvas() != null) {
		            String currentUrl = Session.getActiveCanvas().toUrl();
		            // 前回の状態と比べて、変化があったか？
		            if (!currentUrl.equals(lastCanvasUrl)) {
		                // 変化があったら、その情報を"荷物"として全員に送る！
		                String message = "{\"action\":\"sync\", \"url\":\"" + currentUrl + "\"}";
		                if(webSocketClient != null){
		                	webSocketClient.send(message);
		                }
		                // 今の状態を記憶しておく
		                lastCanvasUrl = currentUrl;
		            }
		        }
		    }
		};
		// 0.5秒ごとに"監視"を実行するんだ！
		this.syncTimer.scheduleRepeating(500);
		*/

		// UMLCanvasが持つ"契約者"の宝箱に、我こそが契約者だと名乗り出る！
		UMLCanvas.webSocketSender = new com.objetdirect.gwt.umlapi.client.helpers.WebSocketSender() {
		    public void send(String message) {
		        // 契約が実行されたら、実際のWebSocketクライアントに荷物を渡す
		        if (webSocketClient != null) {
		            webSocketClient.send(message);
		        }
		    }
		    
		    public void sendTextChangeWithOT(String elementId, String partId, String beforeText, String afterText) {
		        // OT方式でテキスト変更を送信
		        if (drawerPanel != null) {
		            drawerPanel.sendTextChangeWithOT(elementId, partId, beforeText, afterText);
		        }
		    }
		    
    public void sendMoveWithOT(String elementId, int oldX, int oldY, int deltaX, int deltaY) {
        // OT方式で移動を送信
        if (drawerPanel != null) {
            drawerPanel.sendMoveWithOT(elementId, oldX, oldY, deltaX, deltaY);
        }
    }
    
    public void sendMoveWithAbsolutePosition(String elementId, int oldX, int oldY, int newX, int newY) {
        // 絶対座標方式で移動を送信
        if (drawerPanel != null) {
            drawerPanel.sendMoveWithAbsolutePosition(elementId, oldX, oldY, newX, newY);
        }
    }
};

mainPanel.showWidget(0);		this.add(mainPanel, DockPanel.CENTER);
		this.add(leftSideBar, DockPanel.WEST);
		//this.add(rightSideBar, DockPanel.EAST);
		this.add(northBar, DockPanel.NORTH);
		this.add(southBar, DockPanel.SOUTH);

		editEventPanel = new EditEventPanel(DrawerSession.student.getStudentId(),Session.exerciseId); //student_id, exercise_id

		CanvasInit();

	} // ★★★★★★★★★ コンストラクタはここで終わりだぞ！ ★★★★★★★★★


	// ★★★★★★★★★ ここにメソッドを移動させたぞ！ ★★★★★★★★★
	/**
	 * 指定された演習IDの"秘境"（WebSocketチャンネル）に接続するための、新しい呪文だ！
	 * @param exerciseId 接続したい演習のID
	 */
	public void connectToExerciseChannel(String exerciseId) {
	    // もし既に別の演習に接続していたら、一度退出する
	    if (this.webSocketClient != null) {
	        this.webSocketClient.disconnect();
	    }

	    // OT方式のための専用WebSocket接続URL
		// ★★★ IPアドレスは君の環境に合わせてくれよな！ "localhost" か "192.168..." の方だ！ ★★★
	    String webSocketURL = "ws://192.168.1.241:8080/KIfU4/collaboration";
	    System.out.println("OT接続先URL: " + webSocketURL); // デバッグ用にコンソールに出力するぞ

	    // 新しいWebSocketクライアントを作成し、接続を開始する
	    this.webSocketClient = new WebSocketClient(this.drawerPanel);
	    this.webSocketClient.connect(webSocketURL);
	    
	    // ★★★ OT方式のヘルパーを初期化する ★★★
	    if (this.drawerPanel != null && DrawerSession.student != null) {
	        this.drawerPanel.initializeOTHelper(
	            DrawerSession.student.getStudentId(), 
	            Integer.parseInt(exerciseId)
	        );
	        System.out.println("OTヘルパー初期化完了: userId=" + DrawerSession.student.getStudentId() + ", exerciseId=" + exerciseId);
	    }
	    
	    // ★★★ UMLCanvasにWebSocketSenderを設定 ★★★
	    UMLCanvas.webSocketSender = this.webSocketClient;
	    System.out.println("UMLCanvas.webSocketSender設定完了");

	    // ★★★ 全体同期タイマーは無効化されているため、何もしない ★★★
	    // OT方式では操作単位で同期するため、定期的な全体同期は不要
	    /*
	    // "監視塔"作戦をリセットして、新しい演習の監視を開始する
	    this.lastCanvasUrl = ""; // 前回の状態をリセット
	    if (this.syncTimer != null) {
	    	this.syncTimer.cancel(); // 念のため一度止めてから
	        this.syncTimer.scheduleRepeating(500); // タイマーを再開
	    }
	    */
	}


	/*
	 * 山崎追加
	 * server側で動いてる通知のThreadから呼ぶために差分検知のasyncコードを分割
	 */
	public void DetectionBetweenDiagramCode()
	{
		DiffServiceAsync async = GWT.create(DiffService.class);
		ServiceDefTarget entryPoint = (ServiceDefTarget) async;
		String entryURL = GWT.getModuleBaseURL() + "diff";
		entryPoint.setServiceEntryPoint(entryURL);

		async.getDiffMap(Session.studentId,Session.exerciseId,new AsyncCallback< Map<String,Map<String,String>>>()
		{
			@Override
			public void onFailure(Throwable caught) {
				// TODO 自動生成されたメソッド・スタブ
				System.out.println(caught);
			}

			@Override
			public void onSuccess(Map<String,Map<String,String>> diffMap) {
				// TODO 自動生成されたメソッド・スタブ
				List<AbstDrawReplaceAddDelete> surplusList = uMLCanvas.addYamazakiDiffSurplus(diffMap.get(SURPLUS));

				List<AbstDrawReplaceAddDelete> nothasList = uMLCanvas.addYamazakiDiffNotHas(diffMap.get(NOTHAS));

				List<AbstDrawReplaceAddDelete> replaceList = uMLCanvas.addYamazakiReplaceDiff(diffMap);

				DiffReplacePanel diffdialog = new DiffReplacePanel(replaceList,nothasList,surplusList);
				diffdialog.show();
			}
		});

	}

//	private void scheduleFixedDelay_Yamazaki()
//	{
//		/*
//		 * 山崎追加
//		 * コードの要素が変更されたかのリクエストをThreadで送り続けてみる
//		 * ↓保留
//		 * Thread askFixedCodedThread = new CatchSourceCodeChengeState("コードの状態をClientから取得するスレッド");
//		 * askFixedCodedThread.start();
//		 */
//
//		// 定期的にコードの状態を聞いてくれるTimer
//		Timer timerCodeState = new Timer() {
//			@Override
//			public void run() {
//				//Window.alert("scheduleFixedDelay_YamazakiのTimerより");
//				chathcodestate_.isChangeState();
//			}
//		};
//
//		timerCodeState.scheduleRepeating(4000);
//	}

	/*
	 * ここまで山崎
	 */

	@Override
	public void setDiffButtonDisplayedSentence(String stateCodeChange) {
		// TODO 自動生成されたメソッド・スタブ
		this.diff_button_.setText(stateCodeChange);
	}


	//座標を変えるためのデータを送る 実験群  saito
	void layoutChange() {
		SimServiceAsync async = (SimServiceAsync)GWT.create(SimService.class);
		ServiceDefTarget entryPoint = (ServiceDefTarget) async;
		String entryURL = GWT.getModuleBaseURL() + "urlToCompare";
		entryPoint.setServiceEntryPoint(entryURL);

		@SuppressWarnings("rawtypes")
		AsyncCallback< Map<String,Map<String,String>> > callback = new AsyncCallback< Map<String,Map<String,String>> >(){
			@SuppressWarnings({ "unchecked", "deprecation" })
				public void onFailure(Throwable caught){
				System.out.println(caught);
			}

			public void onSuccess(Map<String,Map<String,String>> simMap){

//				uMLCanvas.addSaitoSimMatch(simMap.get(MATCH));
//				uMLCanvas.addSaitoSimMisMatch(simMap.get(MISMATCH));
			}
		};
		async.urlToCompare(Session.studentId, Session.exerciseId, callback);
	}

	//座標を変えるためのデータを送る 統制群  saito
			public void removeCanvas() {
				SimServiceAsync async = (SimServiceAsync)GWT.create(SimService.class);
				ServiceDefTarget entryPoint = (ServiceDefTarget) async;
				String entryURL = GWT.getModuleBaseURL() + "removeRelation";
				entryPoint.setServiceEntryPoint(entryURL);

				@SuppressWarnings("rawtypes")
				AsyncCallback callback = new AsyncCallback(){
					@SuppressWarnings({ "unchecked", "deprecation" })
					public void onSuccess(Object result){

						if( !( (EditEvent) result == null) ){
//							Window.alert("座標が変わります");
							Session.setMode("load");
							Session.getActiveCanvas().clearCanvas();
							Session.getActiveCanvas().fromURL( ((EditEvent) result).getCanvasUrl() , false);
							uMLCanvas = Session.getActiveCanvas();
							Session.setMode("drawer");
//							MyLoggerExecute.registEditEvent(-1, ""+((EditEvent) result ).getEditEventId(), "Undo",
//									null, -1, null, -1, -1,
//									null, null, null, Session.getActiveCanvas().toUrl(), UMLArtifact.getIdCount());
						}
					}

					public void onFailure(Throwable caught){
						System.out.println(caught);
					}
				};
				async.removeRelation(Session.studentId, Session.exerciseId, callback);
			}

	//一番新しいURLのキャンバスに書き換える saito
	public void reloadCanvas() {
		CanvasServiceAsync async = (CanvasServiceAsync)GWT.create(CanvasService.class);
		ServiceDefTarget entryPoint = (ServiceDefTarget) async;
		String entryURL = GWT.getModuleBaseURL() + "loadCanvas";
		entryPoint.setServiceEntryPoint(entryURL);

		@SuppressWarnings("rawtypes")
		AsyncCallback callback = new AsyncCallback(){
			@SuppressWarnings({ "unchecked", "deprecation" })
			public void onSuccess(Object result){

				if( !( (EditEvent) result == null) ){
//					Window.alert("座標が変わります");
					Session.setMode("load");
					Session.getActiveCanvas().clearCanvas();
					Session.getActiveCanvas().fromURL( ((EditEvent) result).getCanvasUrl() , false);
					uMLCanvas = Session.getActiveCanvas();
					Session.setMode("drawer");
//					MyLoggerExecute.registEditEvent(-1, ""+((EditEvent) result ).getEditEventId(), "Undo",
//							null, -1, null, -1, -1,
//							null, null, null, Session.getActiveCanvas().toUrl(), UMLArtifact.getIdCount());
				}
			}

			public void onFailure(Throwable caught){
				System.out.println(caught);
			}
		};

		async.loadCanvas(Session.studentId, Session.exerciseId, callback);
	}



	//完全一致と完全不一致の要素のMapを受け取り、文字の色を変える saito
	void colorChange() {
		SimServiceAsync async = (SimServiceAsync)GWT.create(SimService.class);
		ServiceDefTarget entryPoint = (ServiceDefTarget) async;
		String entryURL = GWT.getModuleBaseURL() + "addColor";
		entryPoint.setServiceEntryPoint(entryURL);

		@SuppressWarnings("rawtypes")
		AsyncCallback< Map<String,Map<String,String>> > callback = new AsyncCallback< Map<String,Map<String,String>> >(){
			@SuppressWarnings({ "unchecked", "deprecation" })
				public void onFailure(Throwable caught){
				System.out.println(caught);
			}

			public void onSuccess(Map<String,Map<String,String>> simMap){

				uMLCanvas.addSaitoSimMatch(simMap.get(MATCH));
				uMLCanvas.addSaitoSimMisMatch(simMap.get(MISMATCH));
			}
		};
		async.addColor(Session.studentId, Session.exerciseId, callback);
	}


	//現在エディターに存在するクラス図と正解例の類似度を取得する
		void getSim() {
			SimServiceAsync async = (SimServiceAsync)GWT.create(SimService.class);
			ServiceDefTarget entryPoint = (ServiceDefTarget) async;
			String entryURL = GWT.getModuleBaseURL() + "getSim";
			entryPoint.setServiceEntryPoint(entryURL);

			@SuppressWarnings("rawtypes")
			AsyncCallback<Double> callback = new AsyncCallback<Double>(){
				@SuppressWarnings({ "unchecked", "deprecation" })
					public void onFailure(Throwable caught){
						System.out.println(caught);
					}

				@Override
					public void onSuccess(Double classDiagramSim){
//						Window.alert("CDS:" + classDiagramSim / 2);
						thisTimeSim = classDiagramSim / 2;
					}


			};
			async.getSim(Session.studentId, Session.exerciseId, callback);
		}

	//この関数呼び出した後はlastTimeSim=thisTimeSimをする
	//今回の類似度が前回の類似度よりも定数diffSim以上上がっていなければfalseを返す
	boolean simCheck() {

		if(thisTimeSim == 0) {
			return true;
		}else if(thisTimeSim <= lastTimeSim) {
			return false;
		}else if(thisTimeSim - lastTimeSim >= diffSim) {
			 // 現在の類似度と前回の類似度の差が閾値を超えている場合は true を返す
			return true;
		}else {
			return false;
		}
	}

	public void timeCountStop(){
			countTimer.cancel();
	}

	/**********************************************************************/

	private void getExercise(int exerciseId) {
		ExerciseServiceAsync async = (ExerciseServiceAsync)GWT.create(ExerciseService.class);
		ServiceDefTarget entryPoint = (ServiceDefTarget) async;
		String entryURL = GWT.getModuleBaseURL() + "getExercise";
		entryPoint.setServiceEntryPoint(entryURL);

		@SuppressWarnings("rawtypes")
		AsyncCallback callback = new AsyncCallback(){
			@SuppressWarnings({ "unchecked", "deprecation" })
			public void onSuccess(Object result){

				if( !( (Exercise) result == null) ){
					exercise = (Exercise) result;
					//mainPanel.add(new ExerciseViewPanel(exercise));
				}
				else{
//					Window.alert("No exercise was selected (+_+)");
				}
			}

			public void onFailure(Throwable caught){
				Window.alert("Error (+_+)");
				System.out.println(caught);
			}
		};

		async.getExercise(exerciseId, callback);
	}

//	private void CanvasInit(){
//
//		//Canvas init
//		CanvasServiceAsync async = (CanvasServiceAsync)GWT.create(CanvasService.class);
//		ServiceDefTarget entryPoint = (ServiceDefTarget) async;
//		String entryURL = GWT.getModuleBaseURL() + "loadCanvas";
//		entryPoint.setServiceEntryPoint(entryURL);
//
//		@SuppressWarnings("rawtypes")
//		AsyncCallback callback = new AsyncCallback(){
//			@SuppressWarnings({ "unchecked", "deprecation" })
//			public void onSuccess(Object result){
//
//				if( !( (EditEvent) result == null) ){
//					Session.setMode("load");
//					Session.getActiveCanvas().fromURL( ((EditEvent) result).getCanvasUrl() , false);
//					uMLCanvas = Session.getActiveCanvas();
//					UMLArtifact.setIdCount( ((EditEvent) result).getUmlArtifactId() );
//					Session.setMode("drawer");
//				}
//				else{
//					if(Session.exerciseId == 16){
//						UMLArtifact.setIdCount(6);
//						MyLoggerExecute.registEditEvent(-1, "Start", "Start",
//								null, -1, null, -1, -1,
//								null, null, null, DEFAULT_MODEL, 6);
//
//						Session.setMode("load");
//						Session.getActiveCanvas().fromURL( DEFAULT_MODEL , false);
//						uMLCanvas = Session.getActiveCanvas();
//
//						String str = String.valueOf(Session.getActiveCanvas()); //saito
//						Window.alert(str);
//						GWT.log("str::"+str);
//
//						Session.setMode("drawer");
//					}
//					else if(Session.exerciseId == 11){
//						UMLArtifact.setIdCount(6);
//						MyLoggerExecute.registEditEvent(-1, "Start", "Start",
//								null, -1, null, -1, -1,
//								null, null, null, DEFAULT_MODEL2, 6);
//
//						Session.setMode("load");
//						Session.getActiveCanvas().fromURL( DEFAULT_MODEL2 , false);
//						uMLCanvas = Session.getActiveCanvas();
//						Session.setMode("drawer");
//					}
//					else{
//						UMLArtifact.setIdCount(0);
//						MyLoggerExecute.registEditEvent(-1, "Start", "Start",
//								null, -1, null, -1, -1,
//								null, null, null, "AA==", 0);
//					}
//					//TODO
//
//				}
//			}
//
//			public void onFailure(Throwable caught){
//				System.out.println(caught);
//				Window.alert("CanvasInit 失敗");
//			}
//		};
//
//		async.loadCanvas(Session.studentId, Session.exerciseId, callback);
//	}
	
	
	private void CanvasInit(){

		//Canvas init
		CanvasServiceAsync async = (CanvasServiceAsync)GWT.create(CanvasService.class);
		ServiceDefTarget entryPoint = (ServiceDefTarget) async;
		String entryURL = GWT.getModuleBaseURL() + "loadCanvas";
		entryPoint.setServiceEntryPoint(entryURL);

		@SuppressWarnings("rawtypes")
		AsyncCallback callback = new AsyncCallback(){
			@SuppressWarnings({ "unchecked", "deprecation" })
			public void onSuccess(Object result){

				if( !( (EditEvent) result == null) ){
					Session.setMode("load");
					// ▼▼▼ 修正箇所 1/3 (clearCanvas() を追加) ▼▼▼
					Session.getActiveCanvas().clearCanvas();
					Session.getActiveCanvas().fromURL( ((EditEvent) result).getCanvasUrl() , false);
					uMLCanvas = Session.getActiveCanvas();
					UMLArtifact.setIdCount( ((EditEvent) result).getUmlArtifactId() );
					Session.setMode("drawer");
				}
				else{
					if(Session.exerciseId == 16){
						UMLArtifact.setIdCount(6);
						MyLoggerExecute.registEditEvent(-1, "Start", "Start",
								null, -1, null, -1, -1,
								null, null, null, DEFAULT_MODEL, 6);

						Session.setMode("load");
						// ▼▼▼ 修正箇所 2/3 (clearCanvas() を追加) ▼▼▼
						Session.getActiveCanvas().clearCanvas();
						Session.getActiveCanvas().fromURL( DEFAULT_MODEL , false);
						uMLCanvas = Session.getActiveCanvas();

						String str = String.valueOf(Session.getActiveCanvas()); //saito
						Window.alert(str);
						GWT.log("str::"+str);

						Session.setMode("drawer");
					}
					else if(Session.exerciseId == 11){
						UMLArtifact.setIdCount(6);
						MyLoggerExecute.registEditEvent(-1, "Start", "Start",
								null, -1, null, -1, -1,
								null, null, null, DEFAULT_MODEL2, 6);

						Session.setMode("load");
						// ▼▼▼ 修正箇所 3/3 (clearCanvas() を追加) ▼▼▼
						Session.getActiveCanvas().clearCanvas();
						Session.getActiveCanvas().fromURL( DEFAULT_MODEL2 , false);
						uMLCanvas = Session.getActiveCanvas();
						Session.setMode("drawer");
					}
					else{
						UMLArtifact.setIdCount(0);
						MyLoggerExecute.registEditEvent(-1, "Start", "Start",
								null, -1, null, -1, -1,
								null, null, null, "AA==", 0);
					}
					//TODO

				}
			}

			public void onFailure(Throwable caught){
				System.out.println(caught);
				Window.alert("CanvasInit 失敗");
			}
		};

		async.loadCanvas(Session.studentId, Session.exerciseId, callback);
	}
	
	//20251014**********************************************************************
	//マージ用のID入力ダイアログを表示するメソッド
			private void showMergeDialog() {
			    // ダイアログボックスを作成
			    final DialogBox dialogBox = new DialogBox();
			    dialogBox.setText("マージ相手の情報を入力");
			    dialogBox.setAnimationEnabled(true);
			    dialogBox.setGlassEnabled(true);

			    
			    // ダイアログ内の要素を配置するパネル
			    VerticalPanel dialogContents = new VerticalPanel();
			    dialogContents.setSpacing(4);

			    // 相手の学生ID入力欄
			    dialogContents.add(new Label("マージする相手の学生ID:"));
			    final TextBox opponentIdTextBox = new TextBox();
			    dialogContents.add(opponentIdTextBox);

			    // ▼▼▼ 修正箇所 1/3 (相手の演習ID入力欄を追加) ▼▼▼
			    dialogContents.add(new Label("マージする相手の演習ID:"));
			    final TextBox opponentExerciseIdTextBox = new TextBox();
			    dialogContents.add(opponentExerciseIdTextBox);
			    // ▲▲▲ 修正箇所 1/3 ▲▲▲

			    // マージ後の共通ID入力欄
			    dialogContents.add(new Label("マージ後の共通ID:"));
			    final TextBox commonIdTextBox = new TextBox();
			    dialogContents.add(commonIdTextBox);

			    // 実行ボタン
			    Button mergeExecuteButton = new Button("実行", new ClickHandler() {
			        public void onClick(ClickEvent event) {
			            String opponentId = opponentIdTextBox.getText();
			            // ▼▼▼ 修正箇所 2/3 (演習IDの取得とチェック) ▼▼▼
			            String commonId = commonIdTextBox.getText();
			            String opponentExerciseIdStr = opponentExerciseIdTextBox.getText();
			            int opponentExerciseId = -1;

			            if (opponentId.isEmpty() || commonId.isEmpty() || opponentExerciseIdStr.isEmpty()) {
			                Window.alert("IDをすべて入力してください。");
			                return;
			            }

			            try {
			                opponentExerciseId = Integer.parseInt(opponentExerciseIdStr);
			            } catch (NumberFormatException e) {
			                Window.alert("相手の演習IDは数値で入力してください。");
			                return;
			            }
			            // ▲▲▲ 修正箇所 2/3 ▲▲▲

			            // 入力されたIDを使ってマージ処理を開始
			            executeMerge(opponentId, opponentExerciseId, commonId); // 引数を変更
			            dialogBox.hide(); // 処理を開始したらダイアログを閉じる
			        }
			    });
			    
			    Button closeButton = new Button("閉じる", new ClickHandler() {
			    	public void onClick(ClickEvent event) {
			    		dialogBox.hide(); 
			    		}
			    	});
			 // ボタンを横に並べる
			    HorizontalPanel buttonPanel = new HorizontalPanel();
			    buttonPanel.setSpacing(10);
			    buttonPanel.add(mergeExecuteButton);
			    buttonPanel.add(closeButton); 
			    dialogContents.add(buttonPanel); 
			    dialogBox.setWidget(dialogContents);
			    dialogBox.center(); // 画面中央に表示
			}
			
			/**
			 * 相手のURLを取得し、マージ処理を実行する
			 * @param opponentId 相手の学生ID
			 * @param opponentExerciseId 相手の演習ID
			 * @param commonId マージ後の共通ID
			 */
			// ▼▼▼ 修正箇所 3/3 (executeMerge メソッドのシグネチャと呼び出し) ▼▼▼
			private void executeMerge(final String opponentId, final int opponentExerciseId, final String commonId) {
			    // 1. 相手のCanvas URLを取得
			    CanvasServiceAsync loadAsync = (CanvasServiceAsync) GWT.create(CanvasService.class);
			    ServiceDefTarget loadEntryPoint = (ServiceDefTarget) loadAsync;
			    String loadEntryURL = GWT.getModuleBaseURL() + "loadCanvas";
			    loadEntryPoint.setServiceEntryPoint(loadEntryURL);

			    // Session.exerciseId ではなく、引数で渡された opponentExerciseId を使用する
			    loadAsync.loadCanvas(opponentId, opponentExerciseId, new AsyncCallback<EditEvent>() {
			        public void onFailure(Throwable caught) {
			            Window.alert("相手のデータの取得に失敗しました: " + caught.getMessage());
			        }

			        public void onSuccess(EditEvent opponentData) {
			            
			            if (opponentData == null || opponentData.getCanvasUrl() == null || opponentData.getCanvasUrl().isEmpty() || opponentData.getCanvasUrl().equals("AA==")) {
			                Window.alert("指定された相手(ID:" + opponentId + ", 演習ID:" + opponentExerciseId + ")の保存データが見つかりません。");
			                return; // マージ処理を中断
			            }

			            String opponentUrl = opponentData.getCanvasUrl();
			            String myUrl = uMLCanvas.toUrl();

			            // 2. サーバーのマージ機能を呼び出す (ここは変更なし)
			            CanvasServiceAsync mergeAsync = (CanvasServiceAsync) GWT.create(CanvasService.class);
			            ServiceDefTarget mergeEntryPoint = (ServiceDefTarget) mergeAsync;
			            String mergeEntryURL = GWT.getModuleBaseURL() + "mergeCanvas"; 
			            mergeEntryPoint.setServiceEntryPoint(mergeEntryURL);

			            mergeAsync.mergeCanvas(myUrl, opponentUrl, new AsyncCallback<String>() {
			                @Override
			                public void onFailure(Throwable caught) {
			                    Window.alert("マージ処理中にエラーが発生しました: " + caught.getMessage());
			                }

			                @Override
			                public void onSuccess(String mergedUrl) {
			                    Window.alert("マージが完了しました。共通ID「" + commonId + "」で結果を保存します。");
								
								String originalStudentId = Session.studentId; 
								
								try {
									Session.studentId = commonId;
									
									MyLoggerExecute.registEditEvent(
											-1, // preEventId
											"Merge", // editEvent
											"Merge", // eventType
											null, // targetType
											-1, // targetId
											null, // linkKind
											-1, // rightObjectId
											-1, // leftObjectId
											null, // targetPart
											null, // beforeEdit
											null, // afterEdit
											mergedUrl, // canvasUrl (マージされたURL)
											UMLArtifact.getIdCount() // 現在のIDカウント
									);
									
									Window.alert("マージ結果を共通ID「" + commonId + "」でログ保存しました。");
									
								} catch (Exception e) {
									Window.alert("マージログの保存に失敗しました: " + e.getMessage());
								} finally {
									Session.studentId = originalStudentId;
								}
//			                    Session.getActiveCanvas().fromURL(mergedUrl, false); 
			                }
			            });
			        }
			    });
			}
			// ▲▲▲ 修正箇所 3/3 ▲▲▲

		/**
		 * マージ後のURLをサーバーに保存する
		 * @param commonId 共通ID
		 * @param exerciseId 演習ID
		 * @param mergedUrl マージされたURL
		 */
		private void saveMergedCanvas(String commonId, int exerciseId, String mergedUrl) {
		    CanvasServiceAsync async = (CanvasServiceAsync) GWT.create(CanvasService.class);
		 // ▼▼▼ ここからが追加する部分です ▼▼▼
	        ServiceDefTarget entryPoint = (ServiceDefTarget) async;
	        String entryURL = GWT.getModuleBaseURL() + "saveMergedCanvas"; // 呼び出すサービス名を指定
	        entryPoint.setServiceEntryPoint(entryURL);
	        // ▲▲▲ ここまで追加します ▲▲▲

		    async.saveMergedCanvas(commonId, exerciseId, mergedUrl, new AsyncCallback<Void>() {
		        @Override
		        public void onFailure(Throwable caught) {
		            Window.alert("マージ結果の保存に失敗しました。");
		        }

		        @Override
		        public void onSuccess(Void result) {
		            // 保存成功時の処理（必要であれば）
		            // 例えば、マージ結果を現在のキャンバスに読み込むなど
		        }
		    });
		}
		//20251014**********************************************************************↑
	
	/**
	 * @return centerPanel
	 */
	public SimplePanel getCenterPanel() {
		return centerPanel;
	}
	/**
	 * @param centerPanel セットする centerPanel
	 */
	public void setCenterPanel(SimplePanel centerPanel) {
		this.centerPanel = centerPanel;
	}
	/**
	 * @return leftSideBar
	 */
	public VerticalPanel getLeftSideBar() {
		return leftSideBar;
	}
	/**
	 * @param leftSideBar セットする leftSideBar
	 */
	public void setLeftSideBar(VerticalPanel leftSideBar) {
		this.leftSideBar = leftSideBar;
	}
	/**
	 * @return rightSideBar
	 */
	public VerticalPanel getRightSideBar() {
		return rightSideBar;
	}
	/**
	 * @param rightSideBar セットする rightSideBar
	 */
	public void setRightSideBar(VerticalPanel rightSideBar) {
		this.rightSideBar = rightSideBar;
	}
	/**
	 * @return northBar
	 */
	public HorizontalPanel getNorthBar() {
		return northBar;
	}
	/**
	 * @param northBar セットする northBar
	 */
	public void setNorthBar(HorizontalPanel northBar) {
		this.northBar = northBar;
	}
	/**
	 * @return southBar
	 */
	public HorizontalPanel getSouthBar() {
		return southBar;
	}
	/**
	 * @param southBar セットする southBar
	 */
	public void setSouthBar(HorizontalPanel southBar) {
		this.southBar = southBar;
	}
	/**
	 * @return webSocketClient
	 */
	public WebSocketClient getWebSocketClient() {
	    return this.webSocketClient;
	}
	// DrawerBase.java のクラスの一番最後に追加

	/**
	 * 他の冒険者から"世界の更新情報"が届いた時に呼び出される呪文だ！
	 */
	public void syncCanvasFromServer(String url) {
	    // 更新がループしないように、一時的に"更新中"の旗を立てる
	    isUpdating = true;
	    // 監視タイマーも一旦止める！
	    syncTimer.cancel();

	    if (Session.getActiveCanvas() != null) {
	        Session.getActiveCanvas().clearCanvas();
	        Session.getActiveCanvas().fromURL(url, false);
	        // 自分の世界の状態も、受け取った最新の状態に更新しておく
	        lastCanvasUrl = url;
	    }

	    // 1秒後に、再び"監視"を再開するためのタイマーをセットする
	    new Timer() {
	        @Override
	        public void run() {
	            isUpdating = false; // "更新中"の旗を下ろす
	            syncTimer.scheduleRepeating(500); 
	        }
	    }.schedule(1000);
	}
}