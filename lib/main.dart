import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

void main() => runApp(MyApp());

class MyApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Material App',
      theme: ThemeData.dark(),
      home: Home(),
    );
  }
}

class Home extends StatefulWidget {
  static const platform = const MethodChannel('samples.flutter.dev/battery');

  @override
  State<Home> createState() => _HomeState();
}

class _HomeState extends State<Home> {
  late int battery =0;
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('Material App Bar'),
      ),
      body: Center(
        child: Container(
          child: battery!=0?Text("$battery"):Text("CLick the button"),
        ),
      ),
      floatingActionButton: FloatingActionButton(
        child: Icon(Icons.add),
        onPressed: () async {
          try {
            final int result =
            await Home.platform.invokeMethod('getBatteryLevel');
            setState(() {
              battery=result;
            });
            print(result);
          } on PlatformException catch (e) {}
        },
      ),
    );
  }
}
