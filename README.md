# pcars2LocalDataCollector

赛车计划2本地比赛数据搜集

只是搜集本地游戏,比赛结果数据.

使用

1.首先要安装jdk：https://pan.baidu.com/s/14MkKvQxoYBFXwBWF-IsTCw

2.启动游戏后，运行startup.bat

3.正式比赛完成后可以查看结果
比赛结果http://127.0.0.1:8099/result/raceRank
最快圈速http://127.0.0.1:8099/result/fastRank

4.可以自己创建一个xx.bat文件运行应用,内容为java -jar pcars2Collector.jar --log.level=info --log.home=./logs

注意：如果直接关闭应用请查看任务管理器是否有CREST2.exe进程驻留
