'--------------------------------------------------------------------
'  VBA実行用共通モジュール
'  引数  ： <エクセルファイル> <マクロ名>
'  実行例： runExcelMacro "C:\\xxx/yyy/zzz.xlsm" Macro1
'
'--------------------------------------------------------------------

Dim obj
Set obj=WScript.CreateObject("Excel.Application")

obj.Visible=False
obj.Workbooks.Open WScript.Arguments(0)
obj.Application.Run WScript.Arguments(1)

obj.Workbooks(1).Saved = True
obj.Quit
Set obj = Nothing
