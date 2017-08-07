# Beacon小助理介面整合
建立了一個叫做BlueToothMethod的方法，不與任何介面綁定，可在任一頁面呼叫藍牙的功能，但因為有些功能又牽涉到介面，所以呼叫時需傳入當前頁面的Context。
## BluetoothMethod使用方法
宣告BluetoothMethod物件，即可使用class底下的method。
```
BluetoothMethod bluetooth = new BluetoothMethod();
bluetooth.BTinit(); // 開始執行藍牙
```
其中有一個public變數叫做bluetoothFunction負責控制當前使用的藍牙功能，該變數等於不同字串時則會執行不同的藍牙功能（因為藍牙會一直掃描，透過改該變數讓程式執行不同的區塊）。
共有三種情況:
1. ```bluetooth.bluetoothFunction = "searchDevice";```
該功能為掃描周圍所有藍牙裝置，並且將掃描到的裝置名稱儲存到Names這個ArrayList當中，MAC地址以及距離資訊則是儲存在Address和Distance這兩個ArrayList，使用bluetooth.Names即可存取該ArrayList。
2. ```bluetooth.bluetoothFunction = "searchItem";```
該功能會搜尋指定的藍牙裝置，指定方式為給出該藍牙裝置的MAC地址，會得到距離以及方位的資訊。
