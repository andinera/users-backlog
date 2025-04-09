import 'package:flutter/material.dart';

void main() {
  runApp(MaterialApp(
    title: 'Flutter Demo',
    theme: ThemeData(
      primarySwatch: Colors.blue,
      visualDensity: VisualDensity.adaptivePlatformDensity,
    ),
    home: MyApp(),
  ));
}

class MyApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) {

    Widget titleSection = Container(
      padding: const EdgeInsets.all(32),
      child: Row(children: [
        Expanded(
            child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Container(
                padding: const EdgeInsets.only(bottom: 8),
                child: Text('Child 1 container',
                    style: TextStyle(fontWeight: FontWeight.bold))),
            Text('Child 2 text', style: TextStyle(color: Colors.grey[500])),
          ],
        )),
        FavoriteWidget(),
      ])
    );

    Color color = Theme.of(context).primaryColor;
    Column _buildButtonColumn(Color color, IconData icon, String label) {
      return Column(
        mainAxisSize: MainAxisSize.min,
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          Icon(icon, color: color),
          Container(
            margin: const EdgeInsets.only(top: 8),
            child: Text(
              label,
              style: TextStyle(
                fontSize: 12,
                fontWeight: FontWeight.w400,
                color: color,
              )
            )
          )
        ],
      );
    }

    Widget buttonSection = Container(
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceEvenly,
        children: [
          _buildButtonColumn(color, Icons.call, 'CALL'),
          _buildButtonColumn(color, Icons.near_me, 'ROUTE'),
          _buildButtonColumn(color, Icons.share, 'SHARE'),
        ],
      )
    );

    Widget textSection = Container(
      padding: const EdgeInsets.all(32),
      child: Text(
        'Some text',
        softWrap: true,
      )
    );

    return Scaffold(
      appBar: AppBar(
        title: Text('Flutter Demo Home Page'),
      ),
      body: ListView(
        children: [
          Hero(
            tag: 'imageHero',
            child: Image.asset(
              'images/lake.jpg',
              width: 600,
              height: 240,
              fit: BoxFit.cover,
            ),
          ),
          titleSection,
          buttonSection,
          textSection,
          ElevatedButton(
            child: Text('Open route'),
            onPressed: () {
              Navigator.push(
                context,
                MaterialPageRoute(builder: (context) => SecondRoute()),
              );
            }
          ),
        ],
      ),
    );
  }
}

class SecondRoute extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('Second Route'),
      ),
      body: ListView(
        children: [
          Hero(
            tag: 'imageHero',
            child: Image.asset(
              'images/lake.jpg',
              width: 600,
              height: 240,
              fit: BoxFit.cover,
            ),
          ),
          ElevatedButton(
            onPressed: () {
              Navigator.pop(context);
            },
            child: Text('Go back!'),
          )
        ]
      )
    );
  }
}

class FavoriteWidget extends StatefulWidget {
  FavoriteWidget({Key key, this.title}) : super(key: key);

  final String title;

  @override
  _FavoriteWidgetState createState() => _FavoriteWidgetState();
}

class _FavoriteWidgetState extends State<FavoriteWidget> {
  bool _isFavorited = true;
  int _favoriteCount = 41;

  void _toggleFavorite() {
    setState(() {
      if (_isFavorited) {
        _favoriteCount -= 1;
        _isFavorited = false;
      } else {
        _favoriteCount += 1;
        _isFavorited = true;
      }
    });
  }

  @override
  Widget build(BuildContext context) {
    return Row(
      mainAxisSize: MainAxisSize.min,
      children: [
        Container(
          padding: EdgeInsets.all(0),
          child: IconButton(
            padding: EdgeInsets.all(0),
            alignment: Alignment.centerRight,
            icon: (_isFavorited ? Icon(Icons.star) : Icon(Icons.star_border)),
            color: Colors.red[500],
            onPressed: _toggleFavorite,
          ),
        ),
        SizedBox(
          width: 18,
          child: Container(
            child: Text('$_favoriteCount')
          )
        )
      ],
    );
  }
}
