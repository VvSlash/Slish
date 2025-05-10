// Generated from c:/Users/krzym/Documents/PW-Zadania/Kompilatory/Slish/src/main/antlr4/Slish.g4 by ANTLR 4.13.1

package pl.edu.pw.slish;

import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast", "CheckReturnValue"})
public class SlishParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.13.1", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, T__8=9, 
		T__9=10, T__10=11, T__11=12, T__12=13, T__13=14, T__14=15, T__15=16, T__16=17, 
		T__17=18, T__18=19, T__19=20, T__20=21, T__21=22, T__22=23, T__23=24, 
		T__24=25, T__25=26, T__26=27, T__27=28, T__28=29, T__29=30, T__30=31, 
		T__31=32, T__32=33, T__33=34, T__34=35, T__35=36, T__36=37, T__37=38, 
		T__38=39, UNDERSCORE=40, COMMENT=41, WS=42, INTEGER=43, FLOAT=44, BOOL=45, 
		STRING=46, STRING_WITH_INTERPOLATION=47, STRING_START=48, STRING_MIDDLE=49, 
		STRING_END=50, IDENTIFIER=51;
	public static final int
		RULE_program = 0, RULE_statement = 1, RULE_declaration = 2, RULE_declarationType = 3, 
		RULE_declarationBody = 4, RULE_funcBody = 5, RULE_paramList = 6, RULE_typedParam = 7, 
		RULE_field = 8, RULE_pipelineExpr = 9, RULE_pipeElement = 10, RULE_typeCast = 11, 
		RULE_assignment = 12, RULE_conditionalStatement = 13, RULE_elseBlock = 14, 
		RULE_whileLoop = 15, RULE_forLoop = 16, RULE_returnStmt = 17, RULE_block = 18, 
		RULE_expression = 19, RULE_arrayLiteral = 20, RULE_argumentList = 21, 
		RULE_stringInterpolation = 22, RULE_interpolationPart = 23, RULE_type = 24, 
		RULE_operator = 25, RULE_literal = 26;
	private static String[] makeRuleNames() {
		return new String[] {
			"program", "statement", "declaration", "declarationType", "declarationBody", 
			"funcBody", "paramList", "typedParam", "field", "pipelineExpr", "pipeElement", 
			"typeCast", "assignment", "conditionalStatement", "elseBlock", "whileLoop", 
			"forLoop", "returnStmt", "block", "expression", "arrayLiteral", "argumentList", 
			"stringInterpolation", "interpolationPart", "type", "operator", "literal"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'/'", "'fun'", "'data'", "'('", "')'", "'{'", "'}'", "'='", "','", 
			"':'", "'|'", "'/if'", "'/ret'", "'/else'", "'/while'", "'/for'", "';'", 
			"'['", "']'", "'/read'", "'int'", "'float'", "'string'", "'bool'", "'void'", 
			"'+'", "'-'", "'*'", "'//'", "'and'", "'or'", "'xor'", "'>'", "'<'", 
			"'>='", "'<='", "'=='", "'!='", "'null'", "'_'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, null, null, null, null, null, null, null, null, 
			null, null, null, null, "UNDERSCORE", "COMMENT", "WS", "INTEGER", "FLOAT", 
			"BOOL", "STRING", "STRING_WITH_INTERPOLATION", "STRING_START", "STRING_MIDDLE", 
			"STRING_END", "IDENTIFIER"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}

	@Override
	public String getGrammarFileName() { return "Slish.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public SlishParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ProgramContext extends ParserRuleContext {
		public TerminalNode EOF() { return getToken(SlishParser.EOF, 0); }
		public List<StatementContext> statement() {
			return getRuleContexts(StatementContext.class);
		}
		public StatementContext statement(int i) {
			return getRuleContext(StatementContext.class,i);
		}
		public ProgramContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_program; }
	}

	public final ProgramContext program() throws RecognitionException {
		ProgramContext _localctx = new ProgramContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_program);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(57);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & 2808702454575122L) != 0)) {
				{
				{
				setState(54);
				statement();
				}
				}
				setState(59);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(60);
			match(EOF);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class StatementContext extends ParserRuleContext {
		public DeclarationContext declaration() {
			return getRuleContext(DeclarationContext.class,0);
		}
		public AssignmentContext assignment() {
			return getRuleContext(AssignmentContext.class,0);
		}
		public PipelineExprContext pipelineExpr() {
			return getRuleContext(PipelineExprContext.class,0);
		}
		public ConditionalStatementContext conditionalStatement() {
			return getRuleContext(ConditionalStatementContext.class,0);
		}
		public WhileLoopContext whileLoop() {
			return getRuleContext(WhileLoopContext.class,0);
		}
		public ForLoopContext forLoop() {
			return getRuleContext(ForLoopContext.class,0);
		}
		public ReturnStmtContext returnStmt() {
			return getRuleContext(ReturnStmtContext.class,0);
		}
		public TerminalNode COMMENT() { return getToken(SlishParser.COMMENT, 0); }
		public StatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_statement; }
	}

	public final StatementContext statement() throws RecognitionException {
		StatementContext _localctx = new StatementContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_statement);
		try {
			setState(70);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,1,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(62);
				declaration();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(63);
				assignment();
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(64);
				pipelineExpr();
				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(65);
				conditionalStatement();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(66);
				whileLoop();
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(67);
				forLoop();
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(68);
				returnStmt();
				}
				break;
			case 8:
				enterOuterAlt(_localctx, 8);
				{
				setState(69);
				match(COMMENT);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class DeclarationContext extends ParserRuleContext {
		public DeclarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_declaration; }
	 
		public DeclarationContext() { }
		public void copyFrom(DeclarationContext ctx) {
			super.copyFrom(ctx);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class TypedDeclarationContext extends DeclarationContext {
		public TerminalNode IDENTIFIER() { return getToken(SlishParser.IDENTIFIER, 0); }
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public TerminalNode UNDERSCORE() { return getToken(SlishParser.UNDERSCORE, 0); }
		public DeclarationBodyContext declarationBody() {
			return getRuleContext(DeclarationBodyContext.class,0);
		}
		public TypedDeclarationContext(DeclarationContext ctx) { copyFrom(ctx); }
	}
	@SuppressWarnings("CheckReturnValue")
	public static class FunctionReferenceContext extends DeclarationContext {
		public TerminalNode IDENTIFIER() { return getToken(SlishParser.IDENTIFIER, 0); }
		public FunctionReferenceContext(DeclarationContext ctx) { copyFrom(ctx); }
	}

	public final DeclarationContext declaration() throws RecognitionException {
		DeclarationContext _localctx = new DeclarationContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_declaration);
		try {
			setState(83);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,4,_ctx) ) {
			case 1:
				_localctx = new TypedDeclarationContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(72);
				match(T__0);
				setState(75);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case T__1:
				case T__20:
				case T__21:
				case T__22:
				case T__23:
				case T__24:
				case IDENTIFIER:
					{
					setState(73);
					type(0);
					}
					break;
				case UNDERSCORE:
					{
					setState(74);
					match(UNDERSCORE);
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(77);
				match(IDENTIFIER);
				setState(79);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,3,_ctx) ) {
				case 1:
					{
					setState(78);
					declarationBody();
					}
					break;
				}
				}
				break;
			case 2:
				_localctx = new FunctionReferenceContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(81);
				match(T__0);
				setState(82);
				match(IDENTIFIER);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class DeclarationTypeContext extends ParserRuleContext {
		public DeclarationTypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_declarationType; }
	 
		public DeclarationTypeContext() { }
		public void copyFrom(DeclarationTypeContext ctx) {
			super.copyFrom(ctx);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class FunDeclContext extends DeclarationTypeContext {
		public FunDeclContext(DeclarationTypeContext ctx) { copyFrom(ctx); }
	}
	@SuppressWarnings("CheckReturnValue")
	public static class DataDeclContext extends DeclarationTypeContext {
		public DataDeclContext(DeclarationTypeContext ctx) { copyFrom(ctx); }
	}

	public final DeclarationTypeContext declarationType() throws RecognitionException {
		DeclarationTypeContext _localctx = new DeclarationTypeContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_declarationType);
		try {
			setState(87);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__1:
				_localctx = new FunDeclContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(85);
				match(T__1);
				}
				break;
			case T__2:
				_localctx = new DataDeclContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(86);
				match(T__2);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class DeclarationBodyContext extends ParserRuleContext {
		public DeclarationBodyContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_declarationBody; }
	 
		public DeclarationBodyContext() { }
		public void copyFrom(DeclarationBodyContext ctx) {
			super.copyFrom(ctx);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class AssignmentBodyContext extends DeclarationBodyContext {
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public AssignmentBodyContext(DeclarationBodyContext ctx) { copyFrom(ctx); }
	}
	@SuppressWarnings("CheckReturnValue")
	public static class DataTypeBodyContext extends DeclarationBodyContext {
		public List<FieldContext> field() {
			return getRuleContexts(FieldContext.class);
		}
		public FieldContext field(int i) {
			return getRuleContext(FieldContext.class,i);
		}
		public DataTypeBodyContext(DeclarationBodyContext ctx) { copyFrom(ctx); }
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ParameterDeclarationBodyContext extends DeclarationBodyContext {
		public ParamListContext paramList() {
			return getRuleContext(ParamListContext.class,0);
		}
		public FuncBodyContext funcBody() {
			return getRuleContext(FuncBodyContext.class,0);
		}
		public ParameterDeclarationBodyContext(DeclarationBodyContext ctx) { copyFrom(ctx); }
	}

	public final DeclarationBodyContext declarationBody() throws RecognitionException {
		DeclarationBodyContext _localctx = new DeclarationBodyContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_declarationBody);
		int _la;
		try {
			setState(107);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__3:
				_localctx = new ParameterDeclarationBodyContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(89);
				match(T__3);
				setState(91);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==T__0) {
					{
					setState(90);
					paramList();
					}
				}

				setState(93);
				match(T__4);
				setState(95);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,7,_ctx) ) {
				case 1:
					{
					setState(94);
					funcBody();
					}
					break;
				}
				}
				break;
			case T__5:
				_localctx = new DataTypeBodyContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(97);
				match(T__5);
				setState(101);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==IDENTIFIER) {
					{
					{
					setState(98);
					field();
					}
					}
					setState(103);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(104);
				match(T__6);
				}
				break;
			case T__7:
				_localctx = new AssignmentBodyContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(105);
				match(T__7);
				setState(106);
				expression(0);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class FuncBodyContext extends ParserRuleContext {
		public BlockContext block() {
			return getRuleContext(BlockContext.class,0);
		}
		public FuncBodyContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_funcBody; }
	}

	public final FuncBodyContext funcBody() throws RecognitionException {
		FuncBodyContext _localctx = new FuncBodyContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_funcBody);
		try {
			setState(112);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__7:
				enterOuterAlt(_localctx, 1);
				{
				setState(109);
				match(T__7);
				setState(110);
				block();
				}
				break;
			case T__5:
				enterOuterAlt(_localctx, 2);
				{
				setState(111);
				block();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ParamListContext extends ParserRuleContext {
		public List<TypedParamContext> typedParam() {
			return getRuleContexts(TypedParamContext.class);
		}
		public TypedParamContext typedParam(int i) {
			return getRuleContext(TypedParamContext.class,i);
		}
		public ParamListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_paramList; }
	}

	public final ParamListContext paramList() throws RecognitionException {
		ParamListContext _localctx = new ParamListContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_paramList);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(114);
			typedParam();
			setState(119);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__8) {
				{
				{
				setState(115);
				match(T__8);
				setState(116);
				typedParam();
				}
				}
				setState(121);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class TypedParamContext extends ParserRuleContext {
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public TerminalNode IDENTIFIER() { return getToken(SlishParser.IDENTIFIER, 0); }
		public TypedParamContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_typedParam; }
	}

	public final TypedParamContext typedParam() throws RecognitionException {
		TypedParamContext _localctx = new TypedParamContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_typedParam);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(122);
			match(T__0);
			setState(123);
			type(0);
			setState(124);
			match(IDENTIFIER);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class FieldContext extends ParserRuleContext {
		public TerminalNode IDENTIFIER() { return getToken(SlishParser.IDENTIFIER, 0); }
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public FieldContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_field; }
	}

	public final FieldContext field() throws RecognitionException {
		FieldContext _localctx = new FieldContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_field);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(126);
			match(IDENTIFIER);
			setState(127);
			match(T__9);
			setState(128);
			type(0);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class PipelineExprContext extends ParserRuleContext {
		public List<PipeElementContext> pipeElement() {
			return getRuleContexts(PipeElementContext.class);
		}
		public PipeElementContext pipeElement(int i) {
			return getRuleContext(PipeElementContext.class,i);
		}
		public PipelineExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_pipelineExpr; }
	}

	public final PipelineExprContext pipelineExpr() throws RecognitionException {
		PipelineExprContext _localctx = new PipelineExprContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_pipelineExpr);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(130);
			pipeElement();
			setState(135);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__10) {
				{
				{
				setState(131);
				match(T__10);
				setState(132);
				pipeElement();
				}
				}
				setState(137);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class PipeElementContext extends ParserRuleContext {
		public PipeElementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_pipeElement; }
	 
		public PipeElementContext() { }
		public void copyFrom(PipeElementContext ctx) {
			super.copyFrom(ctx);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class TypeCastPipeElementContext extends PipeElementContext {
		public TypeCastContext typeCast() {
			return getRuleContext(TypeCastContext.class,0);
		}
		public TypeCastPipeElementContext(PipeElementContext ctx) { copyFrom(ctx); }
	}
	@SuppressWarnings("CheckReturnValue")
	public static class VariableAssignPipeElementContext extends PipeElementContext {
		public TerminalNode IDENTIFIER() { return getToken(SlishParser.IDENTIFIER, 0); }
		public List<TerminalNode> UNDERSCORE() { return getTokens(SlishParser.UNDERSCORE); }
		public TerminalNode UNDERSCORE(int i) {
			return getToken(SlishParser.UNDERSCORE, i);
		}
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public VariableAssignPipeElementContext(PipeElementContext ctx) { copyFrom(ctx); }
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ReturnPipeElementContext extends PipeElementContext {
		public ReturnPipeElementContext(PipeElementContext ctx) { copyFrom(ctx); }
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ExpressionPipeElementContext extends PipeElementContext {
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public ExpressionPipeElementContext(PipeElementContext ctx) { copyFrom(ctx); }
	}
	@SuppressWarnings("CheckReturnValue")
	public static class IfPipeElementContext extends PipeElementContext {
		public BlockContext block() {
			return getRuleContext(BlockContext.class,0);
		}
		public IfPipeElementContext(PipeElementContext ctx) { copyFrom(ctx); }
	}

	public final PipeElementContext pipeElement() throws RecognitionException {
		PipeElementContext _localctx = new PipeElementContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_pipeElement);
		try {
			setState(152);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,14,_ctx) ) {
			case 1:
				_localctx = new ExpressionPipeElementContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(138);
				expression(0);
				}
				break;
			case 2:
				_localctx = new TypeCastPipeElementContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(139);
				typeCast();
				}
				break;
			case 3:
				_localctx = new VariableAssignPipeElementContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(140);
				match(T__0);
				setState(143);
				_errHandler.sync(this);
				switch (_input.LA(1)) {
				case T__1:
				case T__20:
				case T__21:
				case T__22:
				case T__23:
				case T__24:
				case IDENTIFIER:
					{
					setState(141);
					type(0);
					}
					break;
				case UNDERSCORE:
					{
					setState(142);
					match(UNDERSCORE);
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(145);
				match(IDENTIFIER);
				setState(146);
				match(T__7);
				setState(147);
				match(UNDERSCORE);
				}
				break;
			case 4:
				_localctx = new IfPipeElementContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(148);
				match(T__11);
				setState(149);
				match(T__7);
				setState(150);
				block();
				}
				break;
			case 5:
				_localctx = new ReturnPipeElementContext(_localctx);
				enterOuterAlt(_localctx, 5);
				{
				setState(151);
				match(T__12);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class TypeCastContext extends ParserRuleContext {
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public TypeCastContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_typeCast; }
	}

	public final TypeCastContext typeCast() throws RecognitionException {
		TypeCastContext _localctx = new TypeCastContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_typeCast);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(154);
			match(T__0);
			setState(155);
			type(0);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class AssignmentContext extends ParserRuleContext {
		public DeclarationContext declaration() {
			return getRuleContext(DeclarationContext.class,0);
		}
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public TerminalNode IDENTIFIER() { return getToken(SlishParser.IDENTIFIER, 0); }
		public AssignmentContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_assignment; }
	}

	public final AssignmentContext assignment() throws RecognitionException {
		AssignmentContext _localctx = new AssignmentContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_assignment);
		try {
			setState(164);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__0:
				enterOuterAlt(_localctx, 1);
				{
				setState(157);
				declaration();
				setState(158);
				match(T__7);
				setState(159);
				expression(0);
				}
				break;
			case IDENTIFIER:
				enterOuterAlt(_localctx, 2);
				{
				setState(161);
				match(IDENTIFIER);
				setState(162);
				match(T__7);
				setState(163);
				expression(0);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ConditionalStatementContext extends ParserRuleContext {
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public BlockContext block() {
			return getRuleContext(BlockContext.class,0);
		}
		public ElseBlockContext elseBlock() {
			return getRuleContext(ElseBlockContext.class,0);
		}
		public ConditionalStatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_conditionalStatement; }
	}

	public final ConditionalStatementContext conditionalStatement() throws RecognitionException {
		ConditionalStatementContext _localctx = new ConditionalStatementContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_conditionalStatement);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(166);
			match(T__11);
			setState(167);
			match(T__3);
			setState(168);
			expression(0);
			setState(169);
			match(T__4);
			setState(170);
			block();
			setState(172);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==T__13) {
				{
				setState(171);
				elseBlock();
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ElseBlockContext extends ParserRuleContext {
		public BlockContext block() {
			return getRuleContext(BlockContext.class,0);
		}
		public ConditionalStatementContext conditionalStatement() {
			return getRuleContext(ConditionalStatementContext.class,0);
		}
		public ElseBlockContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_elseBlock; }
	}

	public final ElseBlockContext elseBlock() throws RecognitionException {
		ElseBlockContext _localctx = new ElseBlockContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_elseBlock);
		try {
			setState(178);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,17,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(174);
				match(T__13);
				setState(175);
				block();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(176);
				match(T__13);
				setState(177);
				conditionalStatement();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class WhileLoopContext extends ParserRuleContext {
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public BlockContext block() {
			return getRuleContext(BlockContext.class,0);
		}
		public WhileLoopContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_whileLoop; }
	}

	public final WhileLoopContext whileLoop() throws RecognitionException {
		WhileLoopContext _localctx = new WhileLoopContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_whileLoop);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(180);
			match(T__14);
			setState(181);
			match(T__3);
			setState(182);
			expression(0);
			setState(183);
			match(T__4);
			setState(184);
			block();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ForLoopContext extends ParserRuleContext {
		public BlockContext block() {
			return getRuleContext(BlockContext.class,0);
		}
		public DeclarationContext declaration() {
			return getRuleContext(DeclarationContext.class,0);
		}
		public List<AssignmentContext> assignment() {
			return getRuleContexts(AssignmentContext.class);
		}
		public AssignmentContext assignment(int i) {
			return getRuleContext(AssignmentContext.class,i);
		}
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public ForLoopContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_forLoop; }
	}

	public final ForLoopContext forLoop() throws RecognitionException {
		ForLoopContext _localctx = new ForLoopContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_forLoop);
		int _la;
		try {
			setState(209);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,21,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(186);
				match(T__15);
				setState(187);
				match(T__3);
				setState(190);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,18,_ctx) ) {
				case 1:
					{
					setState(188);
					declaration();
					}
					break;
				case 2:
					{
					setState(189);
					assignment();
					}
					break;
				}
				setState(192);
				match(T__16);
				setState(194);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 2806503431208978L) != 0)) {
					{
					setState(193);
					expression(0);
					}
				}

				setState(196);
				match(T__16);
				setState(199);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,20,_ctx) ) {
				case 1:
					{
					setState(197);
					expression(0);
					}
					break;
				case 2:
					{
					setState(198);
					assignment();
					}
					break;
				}
				setState(201);
				match(T__4);
				setState(202);
				block();
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(203);
				match(T__15);
				setState(204);
				match(T__3);
				setState(205);
				expression(0);
				setState(206);
				match(T__4);
				setState(207);
				block();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ReturnStmtContext extends ParserRuleContext {
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public ReturnStmtContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_returnStmt; }
	}

	public final ReturnStmtContext returnStmt() throws RecognitionException {
		ReturnStmtContext _localctx = new ReturnStmtContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_returnStmt);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(211);
			match(T__12);
			setState(213);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,22,_ctx) ) {
			case 1:
				{
				setState(212);
				expression(0);
				}
				break;
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class BlockContext extends ParserRuleContext {
		public List<StatementContext> statement() {
			return getRuleContexts(StatementContext.class);
		}
		public StatementContext statement(int i) {
			return getRuleContext(StatementContext.class,i);
		}
		public BlockContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_block; }
	}

	public final BlockContext block() throws RecognitionException {
		BlockContext _localctx = new BlockContext(_ctx, getState());
		enterRule(_localctx, 36, RULE_block);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(215);
			match(T__5);
			setState(219);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & 2808702454575122L) != 0)) {
				{
				{
				setState(216);
				statement();
				}
				}
				setState(221);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(222);
			match(T__6);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ExpressionContext extends ParserRuleContext {
		public ExpressionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expression; }
	 
		public ExpressionContext() { }
		public void copyFrom(ExpressionContext ctx) {
			super.copyFrom(ctx);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class FunctionCallExprContext extends ExpressionContext {
		public DeclarationContext declaration() {
			return getRuleContext(DeclarationContext.class,0);
		}
		public ArgumentListContext argumentList() {
			return getRuleContext(ArgumentListContext.class,0);
		}
		public FunctionCallExprContext(ExpressionContext ctx) { copyFrom(ctx); }
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ReadExprContext extends ExpressionContext {
		public ReadExprContext(ExpressionContext ctx) { copyFrom(ctx); }
	}
	@SuppressWarnings("CheckReturnValue")
	public static class StringInterpExprContext extends ExpressionContext {
		public StringInterpolationContext stringInterpolation() {
			return getRuleContext(StringInterpolationContext.class,0);
		}
		public StringInterpExprContext(ExpressionContext ctx) { copyFrom(ctx); }
	}
	@SuppressWarnings("CheckReturnValue")
	public static class IdentifierExprContext extends ExpressionContext {
		public TerminalNode IDENTIFIER() { return getToken(SlishParser.IDENTIFIER, 0); }
		public IdentifierExprContext(ExpressionContext ctx) { copyFrom(ctx); }
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ArrayAccessExprContext extends ExpressionContext {
		public TerminalNode IDENTIFIER() { return getToken(SlishParser.IDENTIFIER, 0); }
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public ArrayAccessExprContext(ExpressionContext ctx) { copyFrom(ctx); }
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ArrayLiteralExprContext extends ExpressionContext {
		public ArrayLiteralContext arrayLiteral() {
			return getRuleContext(ArrayLiteralContext.class,0);
		}
		public ArrayLiteralExprContext(ExpressionContext ctx) { copyFrom(ctx); }
	}
	@SuppressWarnings("CheckReturnValue")
	public static class BinaryExprContext extends ExpressionContext {
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public OperatorContext operator() {
			return getRuleContext(OperatorContext.class,0);
		}
		public BinaryExprContext(ExpressionContext ctx) { copyFrom(ctx); }
	}
	@SuppressWarnings("CheckReturnValue")
	public static class LiteralExprContext extends ExpressionContext {
		public LiteralContext literal() {
			return getRuleContext(LiteralContext.class,0);
		}
		public LiteralExprContext(ExpressionContext ctx) { copyFrom(ctx); }
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ParenExprContext extends ExpressionContext {
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public ParenExprContext(ExpressionContext ctx) { copyFrom(ctx); }
	}

	public final ExpressionContext expression() throws RecognitionException {
		return expression(0);
	}

	private ExpressionContext expression(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		ExpressionContext _localctx = new ExpressionContext(_ctx, _parentState);
		ExpressionContext _prevctx = _localctx;
		int _startState = 38;
		enterRecursionRule(_localctx, 38, RULE_expression, _p);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(246);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,25,_ctx) ) {
			case 1:
				{
				_localctx = new LiteralExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(225);
				literal();
				}
				break;
			case 2:
				{
				_localctx = new FunctionCallExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(226);
				declaration();
				setState(227);
				match(T__3);
				setState(229);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 2806503431208978L) != 0)) {
					{
					setState(228);
					argumentList();
					}
				}

				setState(231);
				match(T__4);
				}
				break;
			case 3:
				{
				_localctx = new ParenExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(233);
				match(T__3);
				setState(234);
				expression(0);
				setState(235);
				match(T__4);
				}
				break;
			case 4:
				{
				_localctx = new StringInterpExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(237);
				stringInterpolation();
				}
				break;
			case 5:
				{
				_localctx = new IdentifierExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(238);
				match(IDENTIFIER);
				}
				break;
			case 6:
				{
				_localctx = new ArrayAccessExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(239);
				match(IDENTIFIER);
				setState(240);
				match(T__17);
				setState(241);
				expression(0);
				setState(242);
				match(T__18);
				}
				break;
			case 7:
				{
				_localctx = new ReadExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(244);
				match(T__19);
				}
				break;
			case 8:
				{
				_localctx = new ArrayLiteralExprContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(245);
				arrayLiteral();
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(254);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,26,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new BinaryExprContext(new ExpressionContext(_parentctx, _parentState));
					pushNewRecursionContext(_localctx, _startState, RULE_expression);
					setState(248);
					if (!(precpred(_ctx, 5))) throw new FailedPredicateException(this, "precpred(_ctx, 5)");
					setState(249);
					operator();
					setState(250);
					expression(6);
					}
					} 
				}
				setState(256);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,26,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ArrayLiteralContext extends ParserRuleContext {
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public ArrayLiteralContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_arrayLiteral; }
	}

	public final ArrayLiteralContext arrayLiteral() throws RecognitionException {
		ArrayLiteralContext _localctx = new ArrayLiteralContext(_ctx, getState());
		enterRule(_localctx, 40, RULE_arrayLiteral);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(257);
			match(T__17);
			setState(266);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & 2806503431208978L) != 0)) {
				{
				setState(258);
				expression(0);
				setState(263);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__8) {
					{
					{
					setState(259);
					match(T__8);
					setState(260);
					expression(0);
					}
					}
					setState(265);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
			}

			setState(268);
			match(T__18);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class ArgumentListContext extends ParserRuleContext {
		public List<ExpressionContext> expression() {
			return getRuleContexts(ExpressionContext.class);
		}
		public ExpressionContext expression(int i) {
			return getRuleContext(ExpressionContext.class,i);
		}
		public ArgumentListContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_argumentList; }
	}

	public final ArgumentListContext argumentList() throws RecognitionException {
		ArgumentListContext _localctx = new ArgumentListContext(_ctx, getState());
		enterRule(_localctx, 42, RULE_argumentList);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(270);
			expression(0);
			setState(275);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__8) {
				{
				{
				setState(271);
				match(T__8);
				setState(272);
				expression(0);
				}
				}
				setState(277);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class StringInterpolationContext extends ParserRuleContext {
		public TerminalNode STRING_START() { return getToken(SlishParser.STRING_START, 0); }
		public TerminalNode STRING_END() { return getToken(SlishParser.STRING_END, 0); }
		public List<InterpolationPartContext> interpolationPart() {
			return getRuleContexts(InterpolationPartContext.class);
		}
		public InterpolationPartContext interpolationPart(int i) {
			return getRuleContext(InterpolationPartContext.class,i);
		}
		public List<TerminalNode> STRING_MIDDLE() { return getTokens(SlishParser.STRING_MIDDLE); }
		public TerminalNode STRING_MIDDLE(int i) {
			return getToken(SlishParser.STRING_MIDDLE, i);
		}
		public TerminalNode STRING_WITH_INTERPOLATION() { return getToken(SlishParser.STRING_WITH_INTERPOLATION, 0); }
		public StringInterpolationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_stringInterpolation; }
	}

	public final StringInterpolationContext stringInterpolation() throws RecognitionException {
		StringInterpolationContext _localctx = new StringInterpolationContext(_ctx, getState());
		enterRule(_localctx, 44, RULE_stringInterpolation);
		int _la;
		try {
			setState(289);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case STRING_START:
				enterOuterAlt(_localctx, 1);
				{
				setState(278);
				match(STRING_START);
				setState(284);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==T__5) {
					{
					{
					setState(279);
					interpolationPart();
					setState(280);
					match(STRING_MIDDLE);
					}
					}
					setState(286);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(287);
				match(STRING_END);
				}
				break;
			case STRING_WITH_INTERPOLATION:
				enterOuterAlt(_localctx, 2);
				{
				setState(288);
				match(STRING_WITH_INTERPOLATION);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class InterpolationPartContext extends ParserRuleContext {
		public ExpressionContext expression() {
			return getRuleContext(ExpressionContext.class,0);
		}
		public AssignmentContext assignment() {
			return getRuleContext(AssignmentContext.class,0);
		}
		public InterpolationPartContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_interpolationPart; }
	}

	public final InterpolationPartContext interpolationPart() throws RecognitionException {
		InterpolationPartContext _localctx = new InterpolationPartContext(_ctx, getState());
		enterRule(_localctx, 46, RULE_interpolationPart);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(291);
			match(T__5);
			setState(294);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,32,_ctx) ) {
			case 1:
				{
				setState(292);
				expression(0);
				}
				break;
			case 2:
				{
				setState(293);
				assignment();
				}
				break;
			}
			setState(296);
			match(T__6);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class TypeContext extends ParserRuleContext {
		public TypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_type; }
	 
		public TypeContext() { }
		public void copyFrom(TypeContext ctx) {
			super.copyFrom(ctx);
		}
	}
	@SuppressWarnings("CheckReturnValue")
	public static class VoidTypeContext extends TypeContext {
		public VoidTypeContext(TypeContext ctx) { copyFrom(ctx); }
	}
	@SuppressWarnings("CheckReturnValue")
	public static class CustomTypeContext extends TypeContext {
		public TerminalNode IDENTIFIER() { return getToken(SlishParser.IDENTIFIER, 0); }
		public CustomTypeContext(TypeContext ctx) { copyFrom(ctx); }
	}
	@SuppressWarnings("CheckReturnValue")
	public static class ArrayTypeContext extends TypeContext {
		public TypeContext type() {
			return getRuleContext(TypeContext.class,0);
		}
		public ArrayTypeContext(TypeContext ctx) { copyFrom(ctx); }
	}
	@SuppressWarnings("CheckReturnValue")
	public static class BoolTypeContext extends TypeContext {
		public BoolTypeContext(TypeContext ctx) { copyFrom(ctx); }
	}
	@SuppressWarnings("CheckReturnValue")
	public static class StringTypeContext extends TypeContext {
		public StringTypeContext(TypeContext ctx) { copyFrom(ctx); }
	}
	@SuppressWarnings("CheckReturnValue")
	public static class FunctionTypeContext extends TypeContext {
		public FunctionTypeContext(TypeContext ctx) { copyFrom(ctx); }
	}
	@SuppressWarnings("CheckReturnValue")
	public static class IntTypeContext extends TypeContext {
		public IntTypeContext(TypeContext ctx) { copyFrom(ctx); }
	}
	@SuppressWarnings("CheckReturnValue")
	public static class FloatTypeContext extends TypeContext {
		public FloatTypeContext(TypeContext ctx) { copyFrom(ctx); }
	}

	public final TypeContext type() throws RecognitionException {
		return type(0);
	}

	private TypeContext type(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		TypeContext _localctx = new TypeContext(_ctx, _parentState);
		TypeContext _prevctx = _localctx;
		int _startState = 48;
		enterRecursionRule(_localctx, 48, RULE_type, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(306);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case T__20:
				{
				_localctx = new IntTypeContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;

				setState(299);
				match(T__20);
				}
				break;
			case T__21:
				{
				_localctx = new FloatTypeContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(300);
				match(T__21);
				}
				break;
			case T__22:
				{
				_localctx = new StringTypeContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(301);
				match(T__22);
				}
				break;
			case T__23:
				{
				_localctx = new BoolTypeContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(302);
				match(T__23);
				}
				break;
			case T__24:
				{
				_localctx = new VoidTypeContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(303);
				match(T__24);
				}
				break;
			case T__1:
				{
				_localctx = new FunctionTypeContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(304);
				match(T__1);
				}
				break;
			case IDENTIFIER:
				{
				_localctx = new CustomTypeContext(_localctx);
				_ctx = _localctx;
				_prevctx = _localctx;
				setState(305);
				match(IDENTIFIER);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			_ctx.stop = _input.LT(-1);
			setState(313);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,34,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new ArrayTypeContext(new TypeContext(_parentctx, _parentState));
					pushNewRecursionContext(_localctx, _startState, RULE_type);
					setState(308);
					if (!(precpred(_ctx, 2))) throw new FailedPredicateException(this, "precpred(_ctx, 2)");
					setState(309);
					match(T__17);
					setState(310);
					match(T__18);
					}
					} 
				}
				setState(315);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,34,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class OperatorContext extends ParserRuleContext {
		public OperatorContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_operator; }
	}

	public final OperatorContext operator() throws RecognitionException {
		OperatorContext _localctx = new OperatorContext(_ctx, getState());
		enterRule(_localctx, 50, RULE_operator);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(316);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 549688705024L) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	@SuppressWarnings("CheckReturnValue")
	public static class LiteralContext extends ParserRuleContext {
		public TerminalNode INTEGER() { return getToken(SlishParser.INTEGER, 0); }
		public TerminalNode FLOAT() { return getToken(SlishParser.FLOAT, 0); }
		public TerminalNode STRING() { return getToken(SlishParser.STRING, 0); }
		public TerminalNode BOOL() { return getToken(SlishParser.BOOL, 0); }
		public LiteralContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_literal; }
	}

	public final LiteralContext literal() throws RecognitionException {
		LiteralContext _localctx = new LiteralContext(_ctx, getState());
		enterRule(_localctx, 52, RULE_literal);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(318);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & 132491151147008L) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
		switch (ruleIndex) {
		case 19:
			return expression_sempred((ExpressionContext)_localctx, predIndex);
		case 24:
			return type_sempred((TypeContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean expression_sempred(ExpressionContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0:
			return precpred(_ctx, 5);
		}
		return true;
	}
	private boolean type_sempred(TypeContext _localctx, int predIndex) {
		switch (predIndex) {
		case 1:
			return precpred(_ctx, 2);
		}
		return true;
	}

	public static final String _serializedATN =
		"\u0004\u00013\u0141\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002"+
		"\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002"+
		"\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0002"+
		"\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b\u0002"+
		"\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0002\u000f\u0007\u000f"+
		"\u0002\u0010\u0007\u0010\u0002\u0011\u0007\u0011\u0002\u0012\u0007\u0012"+
		"\u0002\u0013\u0007\u0013\u0002\u0014\u0007\u0014\u0002\u0015\u0007\u0015"+
		"\u0002\u0016\u0007\u0016\u0002\u0017\u0007\u0017\u0002\u0018\u0007\u0018"+
		"\u0002\u0019\u0007\u0019\u0002\u001a\u0007\u001a\u0001\u0000\u0005\u0000"+
		"8\b\u0000\n\u0000\f\u0000;\t\u0000\u0001\u0000\u0001\u0000\u0001\u0001"+
		"\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001\u0001"+
		"\u0001\u0001\u0003\u0001G\b\u0001\u0001\u0002\u0001\u0002\u0001\u0002"+
		"\u0003\u0002L\b\u0002\u0001\u0002\u0001\u0002\u0003\u0002P\b\u0002\u0001"+
		"\u0002\u0001\u0002\u0003\u0002T\b\u0002\u0001\u0003\u0001\u0003\u0003"+
		"\u0003X\b\u0003\u0001\u0004\u0001\u0004\u0003\u0004\\\b\u0004\u0001\u0004"+
		"\u0001\u0004\u0003\u0004`\b\u0004\u0001\u0004\u0001\u0004\u0005\u0004"+
		"d\b\u0004\n\u0004\f\u0004g\t\u0004\u0001\u0004\u0001\u0004\u0001\u0004"+
		"\u0003\u0004l\b\u0004\u0001\u0005\u0001\u0005\u0001\u0005\u0003\u0005"+
		"q\b\u0005\u0001\u0006\u0001\u0006\u0001\u0006\u0005\u0006v\b\u0006\n\u0006"+
		"\f\u0006y\t\u0006\u0001\u0007\u0001\u0007\u0001\u0007\u0001\u0007\u0001"+
		"\b\u0001\b\u0001\b\u0001\b\u0001\t\u0001\t\u0001\t\u0005\t\u0086\b\t\n"+
		"\t\f\t\u0089\t\t\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0003\n\u0090"+
		"\b\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0003\n\u0099"+
		"\b\n\u0001\u000b\u0001\u000b\u0001\u000b\u0001\f\u0001\f\u0001\f\u0001"+
		"\f\u0001\f\u0001\f\u0001\f\u0003\f\u00a5\b\f\u0001\r\u0001\r\u0001\r\u0001"+
		"\r\u0001\r\u0001\r\u0003\r\u00ad\b\r\u0001\u000e\u0001\u000e\u0001\u000e"+
		"\u0001\u000e\u0003\u000e\u00b3\b\u000e\u0001\u000f\u0001\u000f\u0001\u000f"+
		"\u0001\u000f\u0001\u000f\u0001\u000f\u0001\u0010\u0001\u0010\u0001\u0010"+
		"\u0001\u0010\u0003\u0010\u00bf\b\u0010\u0001\u0010\u0001\u0010\u0003\u0010"+
		"\u00c3\b\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0003\u0010\u00c8\b"+
		"\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001\u0010\u0001"+
		"\u0010\u0001\u0010\u0001\u0010\u0003\u0010\u00d2\b\u0010\u0001\u0011\u0001"+
		"\u0011\u0003\u0011\u00d6\b\u0011\u0001\u0012\u0001\u0012\u0005\u0012\u00da"+
		"\b\u0012\n\u0012\f\u0012\u00dd\t\u0012\u0001\u0012\u0001\u0012\u0001\u0013"+
		"\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0003\u0013\u00e6\b\u0013"+
		"\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013"+
		"\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013\u0001\u0013"+
		"\u0001\u0013\u0001\u0013\u0001\u0013\u0003\u0013\u00f7\b\u0013\u0001\u0013"+
		"\u0001\u0013\u0001\u0013\u0001\u0013\u0005\u0013\u00fd\b\u0013\n\u0013"+
		"\f\u0013\u0100\t\u0013\u0001\u0014\u0001\u0014\u0001\u0014\u0001\u0014"+
		"\u0005\u0014\u0106\b\u0014\n\u0014\f\u0014\u0109\t\u0014\u0003\u0014\u010b"+
		"\b\u0014\u0001\u0014\u0001\u0014\u0001\u0015\u0001\u0015\u0001\u0015\u0005"+
		"\u0015\u0112\b\u0015\n\u0015\f\u0015\u0115\t\u0015\u0001\u0016\u0001\u0016"+
		"\u0001\u0016\u0001\u0016\u0005\u0016\u011b\b\u0016\n\u0016\f\u0016\u011e"+
		"\t\u0016\u0001\u0016\u0001\u0016\u0003\u0016\u0122\b\u0016\u0001\u0017"+
		"\u0001\u0017\u0001\u0017\u0003\u0017\u0127\b\u0017\u0001\u0017\u0001\u0017"+
		"\u0001\u0018\u0001\u0018\u0001\u0018\u0001\u0018\u0001\u0018\u0001\u0018"+
		"\u0001\u0018\u0001\u0018\u0003\u0018\u0133\b\u0018\u0001\u0018\u0001\u0018"+
		"\u0001\u0018\u0005\u0018\u0138\b\u0018\n\u0018\f\u0018\u013b\t\u0018\u0001"+
		"\u0019\u0001\u0019\u0001\u001a\u0001\u001a\u0001\u001a\u0000\u0002&0\u001b"+
		"\u0000\u0002\u0004\u0006\b\n\f\u000e\u0010\u0012\u0014\u0016\u0018\u001a"+
		"\u001c\u001e \"$&(*,.024\u0000\u0002\u0001\u0000\u001a&\u0002\u0000\'"+
		"\'+.\u015f\u00009\u0001\u0000\u0000\u0000\u0002F\u0001\u0000\u0000\u0000"+
		"\u0004S\u0001\u0000\u0000\u0000\u0006W\u0001\u0000\u0000\u0000\bk\u0001"+
		"\u0000\u0000\u0000\np\u0001\u0000\u0000\u0000\fr\u0001\u0000\u0000\u0000"+
		"\u000ez\u0001\u0000\u0000\u0000\u0010~\u0001\u0000\u0000\u0000\u0012\u0082"+
		"\u0001\u0000\u0000\u0000\u0014\u0098\u0001\u0000\u0000\u0000\u0016\u009a"+
		"\u0001\u0000\u0000\u0000\u0018\u00a4\u0001\u0000\u0000\u0000\u001a\u00a6"+
		"\u0001\u0000\u0000\u0000\u001c\u00b2\u0001\u0000\u0000\u0000\u001e\u00b4"+
		"\u0001\u0000\u0000\u0000 \u00d1\u0001\u0000\u0000\u0000\"\u00d3\u0001"+
		"\u0000\u0000\u0000$\u00d7\u0001\u0000\u0000\u0000&\u00f6\u0001\u0000\u0000"+
		"\u0000(\u0101\u0001\u0000\u0000\u0000*\u010e\u0001\u0000\u0000\u0000,"+
		"\u0121\u0001\u0000\u0000\u0000.\u0123\u0001\u0000\u0000\u00000\u0132\u0001"+
		"\u0000\u0000\u00002\u013c\u0001\u0000\u0000\u00004\u013e\u0001\u0000\u0000"+
		"\u000068\u0003\u0002\u0001\u000076\u0001\u0000\u0000\u00008;\u0001\u0000"+
		"\u0000\u000097\u0001\u0000\u0000\u00009:\u0001\u0000\u0000\u0000:<\u0001"+
		"\u0000\u0000\u0000;9\u0001\u0000\u0000\u0000<=\u0005\u0000\u0000\u0001"+
		"=\u0001\u0001\u0000\u0000\u0000>G\u0003\u0004\u0002\u0000?G\u0003\u0018"+
		"\f\u0000@G\u0003\u0012\t\u0000AG\u0003\u001a\r\u0000BG\u0003\u001e\u000f"+
		"\u0000CG\u0003 \u0010\u0000DG\u0003\"\u0011\u0000EG\u0005)\u0000\u0000"+
		"F>\u0001\u0000\u0000\u0000F?\u0001\u0000\u0000\u0000F@\u0001\u0000\u0000"+
		"\u0000FA\u0001\u0000\u0000\u0000FB\u0001\u0000\u0000\u0000FC\u0001\u0000"+
		"\u0000\u0000FD\u0001\u0000\u0000\u0000FE\u0001\u0000\u0000\u0000G\u0003"+
		"\u0001\u0000\u0000\u0000HK\u0005\u0001\u0000\u0000IL\u00030\u0018\u0000"+
		"JL\u0005(\u0000\u0000KI\u0001\u0000\u0000\u0000KJ\u0001\u0000\u0000\u0000"+
		"LM\u0001\u0000\u0000\u0000MO\u00053\u0000\u0000NP\u0003\b\u0004\u0000"+
		"ON\u0001\u0000\u0000\u0000OP\u0001\u0000\u0000\u0000PT\u0001\u0000\u0000"+
		"\u0000QR\u0005\u0001\u0000\u0000RT\u00053\u0000\u0000SH\u0001\u0000\u0000"+
		"\u0000SQ\u0001\u0000\u0000\u0000T\u0005\u0001\u0000\u0000\u0000UX\u0005"+
		"\u0002\u0000\u0000VX\u0005\u0003\u0000\u0000WU\u0001\u0000\u0000\u0000"+
		"WV\u0001\u0000\u0000\u0000X\u0007\u0001\u0000\u0000\u0000Y[\u0005\u0004"+
		"\u0000\u0000Z\\\u0003\f\u0006\u0000[Z\u0001\u0000\u0000\u0000[\\\u0001"+
		"\u0000\u0000\u0000\\]\u0001\u0000\u0000\u0000]_\u0005\u0005\u0000\u0000"+
		"^`\u0003\n\u0005\u0000_^\u0001\u0000\u0000\u0000_`\u0001\u0000\u0000\u0000"+
		"`l\u0001\u0000\u0000\u0000ae\u0005\u0006\u0000\u0000bd\u0003\u0010\b\u0000"+
		"cb\u0001\u0000\u0000\u0000dg\u0001\u0000\u0000\u0000ec\u0001\u0000\u0000"+
		"\u0000ef\u0001\u0000\u0000\u0000fh\u0001\u0000\u0000\u0000ge\u0001\u0000"+
		"\u0000\u0000hl\u0005\u0007\u0000\u0000ij\u0005\b\u0000\u0000jl\u0003&"+
		"\u0013\u0000kY\u0001\u0000\u0000\u0000ka\u0001\u0000\u0000\u0000ki\u0001"+
		"\u0000\u0000\u0000l\t\u0001\u0000\u0000\u0000mn\u0005\b\u0000\u0000nq"+
		"\u0003$\u0012\u0000oq\u0003$\u0012\u0000pm\u0001\u0000\u0000\u0000po\u0001"+
		"\u0000\u0000\u0000q\u000b\u0001\u0000\u0000\u0000rw\u0003\u000e\u0007"+
		"\u0000st\u0005\t\u0000\u0000tv\u0003\u000e\u0007\u0000us\u0001\u0000\u0000"+
		"\u0000vy\u0001\u0000\u0000\u0000wu\u0001\u0000\u0000\u0000wx\u0001\u0000"+
		"\u0000\u0000x\r\u0001\u0000\u0000\u0000yw\u0001\u0000\u0000\u0000z{\u0005"+
		"\u0001\u0000\u0000{|\u00030\u0018\u0000|}\u00053\u0000\u0000}\u000f\u0001"+
		"\u0000\u0000\u0000~\u007f\u00053\u0000\u0000\u007f\u0080\u0005\n\u0000"+
		"\u0000\u0080\u0081\u00030\u0018\u0000\u0081\u0011\u0001\u0000\u0000\u0000"+
		"\u0082\u0087\u0003\u0014\n\u0000\u0083\u0084\u0005\u000b\u0000\u0000\u0084"+
		"\u0086\u0003\u0014\n\u0000\u0085\u0083\u0001\u0000\u0000\u0000\u0086\u0089"+
		"\u0001\u0000\u0000\u0000\u0087\u0085\u0001\u0000\u0000\u0000\u0087\u0088"+
		"\u0001\u0000\u0000\u0000\u0088\u0013\u0001\u0000\u0000\u0000\u0089\u0087"+
		"\u0001\u0000\u0000\u0000\u008a\u0099\u0003&\u0013\u0000\u008b\u0099\u0003"+
		"\u0016\u000b\u0000\u008c\u008f\u0005\u0001\u0000\u0000\u008d\u0090\u0003"+
		"0\u0018\u0000\u008e\u0090\u0005(\u0000\u0000\u008f\u008d\u0001\u0000\u0000"+
		"\u0000\u008f\u008e\u0001\u0000\u0000\u0000\u0090\u0091\u0001\u0000\u0000"+
		"\u0000\u0091\u0092\u00053\u0000\u0000\u0092\u0093\u0005\b\u0000\u0000"+
		"\u0093\u0099\u0005(\u0000\u0000\u0094\u0095\u0005\f\u0000\u0000\u0095"+
		"\u0096\u0005\b\u0000\u0000\u0096\u0099\u0003$\u0012\u0000\u0097\u0099"+
		"\u0005\r\u0000\u0000\u0098\u008a\u0001\u0000\u0000\u0000\u0098\u008b\u0001"+
		"\u0000\u0000\u0000\u0098\u008c\u0001\u0000\u0000\u0000\u0098\u0094\u0001"+
		"\u0000\u0000\u0000\u0098\u0097\u0001\u0000\u0000\u0000\u0099\u0015\u0001"+
		"\u0000\u0000\u0000\u009a\u009b\u0005\u0001\u0000\u0000\u009b\u009c\u0003"+
		"0\u0018\u0000\u009c\u0017\u0001\u0000\u0000\u0000\u009d\u009e\u0003\u0004"+
		"\u0002\u0000\u009e\u009f\u0005\b\u0000\u0000\u009f\u00a0\u0003&\u0013"+
		"\u0000\u00a0\u00a5\u0001\u0000\u0000\u0000\u00a1\u00a2\u00053\u0000\u0000"+
		"\u00a2\u00a3\u0005\b\u0000\u0000\u00a3\u00a5\u0003&\u0013\u0000\u00a4"+
		"\u009d\u0001\u0000\u0000\u0000\u00a4\u00a1\u0001\u0000\u0000\u0000\u00a5"+
		"\u0019\u0001\u0000\u0000\u0000\u00a6\u00a7\u0005\f\u0000\u0000\u00a7\u00a8"+
		"\u0005\u0004\u0000\u0000\u00a8\u00a9\u0003&\u0013\u0000\u00a9\u00aa\u0005"+
		"\u0005\u0000\u0000\u00aa\u00ac\u0003$\u0012\u0000\u00ab\u00ad\u0003\u001c"+
		"\u000e\u0000\u00ac\u00ab\u0001\u0000\u0000\u0000\u00ac\u00ad\u0001\u0000"+
		"\u0000\u0000\u00ad\u001b\u0001\u0000\u0000\u0000\u00ae\u00af\u0005\u000e"+
		"\u0000\u0000\u00af\u00b3\u0003$\u0012\u0000\u00b0\u00b1\u0005\u000e\u0000"+
		"\u0000\u00b1\u00b3\u0003\u001a\r\u0000\u00b2\u00ae\u0001\u0000\u0000\u0000"+
		"\u00b2\u00b0\u0001\u0000\u0000\u0000\u00b3\u001d\u0001\u0000\u0000\u0000"+
		"\u00b4\u00b5\u0005\u000f\u0000\u0000\u00b5\u00b6\u0005\u0004\u0000\u0000"+
		"\u00b6\u00b7\u0003&\u0013\u0000\u00b7\u00b8\u0005\u0005\u0000\u0000\u00b8"+
		"\u00b9\u0003$\u0012\u0000\u00b9\u001f\u0001\u0000\u0000\u0000\u00ba\u00bb"+
		"\u0005\u0010\u0000\u0000\u00bb\u00be\u0005\u0004\u0000\u0000\u00bc\u00bf"+
		"\u0003\u0004\u0002\u0000\u00bd\u00bf\u0003\u0018\f\u0000\u00be\u00bc\u0001"+
		"\u0000\u0000\u0000\u00be\u00bd\u0001\u0000\u0000\u0000\u00be\u00bf\u0001"+
		"\u0000\u0000\u0000\u00bf\u00c0\u0001\u0000\u0000\u0000\u00c0\u00c2\u0005"+
		"\u0011\u0000\u0000\u00c1\u00c3\u0003&\u0013\u0000\u00c2\u00c1\u0001\u0000"+
		"\u0000\u0000\u00c2\u00c3\u0001\u0000\u0000\u0000\u00c3\u00c4\u0001\u0000"+
		"\u0000\u0000\u00c4\u00c7\u0005\u0011\u0000\u0000\u00c5\u00c8\u0003&\u0013"+
		"\u0000\u00c6\u00c8\u0003\u0018\f\u0000\u00c7\u00c5\u0001\u0000\u0000\u0000"+
		"\u00c7\u00c6\u0001\u0000\u0000\u0000\u00c7\u00c8\u0001\u0000\u0000\u0000"+
		"\u00c8\u00c9\u0001\u0000\u0000\u0000\u00c9\u00ca\u0005\u0005\u0000\u0000"+
		"\u00ca\u00d2\u0003$\u0012\u0000\u00cb\u00cc\u0005\u0010\u0000\u0000\u00cc"+
		"\u00cd\u0005\u0004\u0000\u0000\u00cd\u00ce\u0003&\u0013\u0000\u00ce\u00cf"+
		"\u0005\u0005\u0000\u0000\u00cf\u00d0\u0003$\u0012\u0000\u00d0\u00d2\u0001"+
		"\u0000\u0000\u0000\u00d1\u00ba\u0001\u0000\u0000\u0000\u00d1\u00cb\u0001"+
		"\u0000\u0000\u0000\u00d2!\u0001\u0000\u0000\u0000\u00d3\u00d5\u0005\r"+
		"\u0000\u0000\u00d4\u00d6\u0003&\u0013\u0000\u00d5\u00d4\u0001\u0000\u0000"+
		"\u0000\u00d5\u00d6\u0001\u0000\u0000\u0000\u00d6#\u0001\u0000\u0000\u0000"+
		"\u00d7\u00db\u0005\u0006\u0000\u0000\u00d8\u00da\u0003\u0002\u0001\u0000"+
		"\u00d9\u00d8\u0001\u0000\u0000\u0000\u00da\u00dd\u0001\u0000\u0000\u0000"+
		"\u00db\u00d9\u0001\u0000\u0000\u0000\u00db\u00dc\u0001\u0000\u0000\u0000"+
		"\u00dc\u00de\u0001\u0000\u0000\u0000\u00dd\u00db\u0001\u0000\u0000\u0000"+
		"\u00de\u00df\u0005\u0007\u0000\u0000\u00df%\u0001\u0000\u0000\u0000\u00e0"+
		"\u00e1\u0006\u0013\uffff\uffff\u0000\u00e1\u00f7\u00034\u001a\u0000\u00e2"+
		"\u00e3\u0003\u0004\u0002\u0000\u00e3\u00e5\u0005\u0004\u0000\u0000\u00e4"+
		"\u00e6\u0003*\u0015\u0000\u00e5\u00e4\u0001\u0000\u0000\u0000\u00e5\u00e6"+
		"\u0001\u0000\u0000\u0000\u00e6\u00e7\u0001\u0000\u0000\u0000\u00e7\u00e8"+
		"\u0005\u0005\u0000\u0000\u00e8\u00f7\u0001\u0000\u0000\u0000\u00e9\u00ea"+
		"\u0005\u0004\u0000\u0000\u00ea\u00eb\u0003&\u0013\u0000\u00eb\u00ec\u0005"+
		"\u0005\u0000\u0000\u00ec\u00f7\u0001\u0000\u0000\u0000\u00ed\u00f7\u0003"+
		",\u0016\u0000\u00ee\u00f7\u00053\u0000\u0000\u00ef\u00f0\u00053\u0000"+
		"\u0000\u00f0\u00f1\u0005\u0012\u0000\u0000\u00f1\u00f2\u0003&\u0013\u0000"+
		"\u00f2\u00f3\u0005\u0013\u0000\u0000\u00f3\u00f7\u0001\u0000\u0000\u0000"+
		"\u00f4\u00f7\u0005\u0014\u0000\u0000\u00f5\u00f7\u0003(\u0014\u0000\u00f6"+
		"\u00e0\u0001\u0000\u0000\u0000\u00f6\u00e2\u0001\u0000\u0000\u0000\u00f6"+
		"\u00e9\u0001\u0000\u0000\u0000\u00f6\u00ed\u0001\u0000\u0000\u0000\u00f6"+
		"\u00ee\u0001\u0000\u0000\u0000\u00f6\u00ef\u0001\u0000\u0000\u0000\u00f6"+
		"\u00f4\u0001\u0000\u0000\u0000\u00f6\u00f5\u0001\u0000\u0000\u0000\u00f7"+
		"\u00fe\u0001\u0000\u0000\u0000\u00f8\u00f9\n\u0005\u0000\u0000\u00f9\u00fa"+
		"\u00032\u0019\u0000\u00fa\u00fb\u0003&\u0013\u0006\u00fb\u00fd\u0001\u0000"+
		"\u0000\u0000\u00fc\u00f8\u0001\u0000\u0000\u0000\u00fd\u0100\u0001\u0000"+
		"\u0000\u0000\u00fe\u00fc\u0001\u0000\u0000\u0000\u00fe\u00ff\u0001\u0000"+
		"\u0000\u0000\u00ff\'\u0001\u0000\u0000\u0000\u0100\u00fe\u0001\u0000\u0000"+
		"\u0000\u0101\u010a\u0005\u0012\u0000\u0000\u0102\u0107\u0003&\u0013\u0000"+
		"\u0103\u0104\u0005\t\u0000\u0000\u0104\u0106\u0003&\u0013\u0000\u0105"+
		"\u0103\u0001\u0000\u0000\u0000\u0106\u0109\u0001\u0000\u0000\u0000\u0107"+
		"\u0105\u0001\u0000\u0000\u0000\u0107\u0108\u0001\u0000\u0000\u0000\u0108"+
		"\u010b\u0001\u0000\u0000\u0000\u0109\u0107\u0001\u0000\u0000\u0000\u010a"+
		"\u0102\u0001\u0000\u0000\u0000\u010a\u010b\u0001\u0000\u0000\u0000\u010b"+
		"\u010c\u0001\u0000\u0000\u0000\u010c\u010d\u0005\u0013\u0000\u0000\u010d"+
		")\u0001\u0000\u0000\u0000\u010e\u0113\u0003&\u0013\u0000\u010f\u0110\u0005"+
		"\t\u0000\u0000\u0110\u0112\u0003&\u0013\u0000\u0111\u010f\u0001\u0000"+
		"\u0000\u0000\u0112\u0115\u0001\u0000\u0000\u0000\u0113\u0111\u0001\u0000"+
		"\u0000\u0000\u0113\u0114\u0001\u0000\u0000\u0000\u0114+\u0001\u0000\u0000"+
		"\u0000\u0115\u0113\u0001\u0000\u0000\u0000\u0116\u011c\u00050\u0000\u0000"+
		"\u0117\u0118\u0003.\u0017\u0000\u0118\u0119\u00051\u0000\u0000\u0119\u011b"+
		"\u0001\u0000\u0000\u0000\u011a\u0117\u0001\u0000\u0000\u0000\u011b\u011e"+
		"\u0001\u0000\u0000\u0000\u011c\u011a\u0001\u0000\u0000\u0000\u011c\u011d"+
		"\u0001\u0000\u0000\u0000\u011d\u011f\u0001\u0000\u0000\u0000\u011e\u011c"+
		"\u0001\u0000\u0000\u0000\u011f\u0122\u00052\u0000\u0000\u0120\u0122\u0005"+
		"/\u0000\u0000\u0121\u0116\u0001\u0000\u0000\u0000\u0121\u0120\u0001\u0000"+
		"\u0000\u0000\u0122-\u0001\u0000\u0000\u0000\u0123\u0126\u0005\u0006\u0000"+
		"\u0000\u0124\u0127\u0003&\u0013\u0000\u0125\u0127\u0003\u0018\f\u0000"+
		"\u0126\u0124\u0001\u0000\u0000\u0000\u0126\u0125\u0001\u0000\u0000\u0000"+
		"\u0127\u0128\u0001\u0000\u0000\u0000\u0128\u0129\u0005\u0007\u0000\u0000"+
		"\u0129/\u0001\u0000\u0000\u0000\u012a\u012b\u0006\u0018\uffff\uffff\u0000"+
		"\u012b\u0133\u0005\u0015\u0000\u0000\u012c\u0133\u0005\u0016\u0000\u0000"+
		"\u012d\u0133\u0005\u0017\u0000\u0000\u012e\u0133\u0005\u0018\u0000\u0000"+
		"\u012f\u0133\u0005\u0019\u0000\u0000\u0130\u0133\u0005\u0002\u0000\u0000"+
		"\u0131\u0133\u00053\u0000\u0000\u0132\u012a\u0001\u0000\u0000\u0000\u0132"+
		"\u012c\u0001\u0000\u0000\u0000\u0132\u012d\u0001\u0000\u0000\u0000\u0132"+
		"\u012e\u0001\u0000\u0000\u0000\u0132\u012f\u0001\u0000\u0000\u0000\u0132"+
		"\u0130\u0001\u0000\u0000\u0000\u0132\u0131\u0001\u0000\u0000\u0000\u0133"+
		"\u0139\u0001\u0000\u0000\u0000\u0134\u0135\n\u0002\u0000\u0000\u0135\u0136"+
		"\u0005\u0012\u0000\u0000\u0136\u0138\u0005\u0013\u0000\u0000\u0137\u0134"+
		"\u0001\u0000\u0000\u0000\u0138\u013b\u0001\u0000\u0000\u0000\u0139\u0137"+
		"\u0001\u0000\u0000\u0000\u0139\u013a\u0001\u0000\u0000\u0000\u013a1\u0001"+
		"\u0000\u0000\u0000\u013b\u0139\u0001\u0000\u0000\u0000\u013c\u013d\u0007"+
		"\u0000\u0000\u0000\u013d3\u0001\u0000\u0000\u0000\u013e\u013f\u0007\u0001"+
		"\u0000\u0000\u013f5\u0001\u0000\u0000\u0000#9FKOSW[_ekpw\u0087\u008f\u0098"+
		"\u00a4\u00ac\u00b2\u00be\u00c2\u00c7\u00d1\u00d5\u00db\u00e5\u00f6\u00fe"+
		"\u0107\u010a\u0113\u011c\u0121\u0126\u0132\u0139";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}