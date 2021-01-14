function SaveAsCsv([String] $source, [String] $target) {
    $xl = New-Object -ComObject Excel.Application
    $xl.Visible = $false
    $xl.displayalerts = $false

    $wb = $xl.Workbooks.Open($source)
    $wb.SaveAs($target, 6)
    $wb.Close()

    $xl.Quit()
}
