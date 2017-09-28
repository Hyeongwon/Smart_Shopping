/**
 * Created by byunhyeongwon on 2017. 9. 9..
 */

var fs = require('fs');

var addUser = function(req, res) {
    console.log('process/add_User call...!!!');

    var param_pName = req.body.pName || req.query.pName;
    var param_pAddr = req.body.pAddr || req.query.pAddr;
    var param_pTel = req.body.pTel || req.query.pTel;
    var param_pPassword = req.body.pPassword || req.query.pPassword;
    var param_pEmail = req.body.pEmail || req.query.pEmail;

    var param = [param_pName, param_pAddr, param_pTel, param_pPassword, param_pEmail];

    console.log("req email = " + param_pEmail);
    console.log("req passwor = " + param_pPassword);
    console.log("req tel = " + param_pTel);
    console.log("req addr = " + param_pAddr);
    console.log("req name = " + param_pName);

    var database = req.app.get('database');
    var pool = database.db;

    if (database.db) {
        adduser(param, pool, function(err, addedProduct) {
            // 동일한 id로 추가하려는 경우 에러 발생 - 클라이언트로 에러 전송

            // 결과 객체 있으면 성공 응답 전송
            if (addedProduct) {
                console.dir(addedProduct);

                console.log('inserted ' + addedProduct.affectedRows + ' rows');

                var insertId = addedProduct.insertId;
                console.log('추가한 레코드의 아이디 : ' + insertId);

                res.writeHead('200', {'Content-Type':'text/html;charset=utf8'});
                res.write('<h2>Add Success</h2>');
                res.end();

            } else {
                res.writeHead('200', {'Content-Type':'text/html;charset=utf8'});
                res.write('<h2>Add Fail</h2>');
                res.end();
            }
        });
    } else {  // 데이터베이스 객체가 초기화되지 않은 경우 실패 응답 전송
        res.writeHead('200', {'Content-Type':'text/html;charset=utf8'});
        res.write('<h2>데이터베이스 연결 실패</h2>');
        res.end();
    }

};

var getData = function(req, res) {

    console.log('/process/get_product_name call...!!!');

    var param_pName = req.body.pName || req.query.pName;

    console.log("req param : " + param_pName);

    var database = req.app.get('database');
    var pool = database.db;

    if (database.db) {

        getdata(param_pName, pool, function (err, rows) {

            if (err) {
                console.error('Search Error' + err.stack);
                res.writeHead('200', {'Content-Type': 'text/html;charset=utf8'});
                res.write('<h2>Error</h2>');
                res.write('<p>' + err.stack + '</p>');
                res.end();

                return;
            }

            if (rows) {

                console.dir(rows);

                var product_name = rows[0].product_name;

                res.writeHead('200', {'Content-Type': 'text/html;charset=utf8'});
                res.write('<h1>Get Item</h1>');
                res.write('<div><p>Product_name : ' + product_name + '</p></div>');
                res.end();
            }

        })
    }
};

var getJson = function(req, res) {

    console.log('/process/get_product_name_json...!!!');

    var param_pName = req.body.pName || req.query.pName;

    console.log("req param : " + param_pName);

    var database = req.app.get('database');
    var pool = database.db;
    if (database.db) {

        getjson(param_pName, pool, function(err, rows) {

            if(err) {
                console.error('Search Error' + err.stack);
                res.writeHead('200', {'Content-Type':'application/json;charset=utf8'});
                res.write('<h2>Error</h2>');
                res.write('<p>' + err.stack  + '</p>');
                res.end();

                return;
            }

            if(rows) {

                console.dir(rows);

                var product_id = rows[0].id;
                console.log(product_id);


                res.writeHead('200', {'Content-Type' : 'application/json;charset=utf8'});
                res.write(JSON.stringify(rows));
                res.end();
            }

        })
    }
};

var getImage = function (req, res) {

    var param_pId = req.body.pId || req.query.pId;

    var database = req.app.get('database');
    var pool = database.db;
    if (database.db) {

        getimage(param_pId, pool, function (err, rows) {

            if (err) {
                console.error('Search Error' + err.stack);
                res.writeHead('200', {'Content-Type': 'application/json;charset=utf8'});
                res.write('<h2>Error</h2>');
                res.write('<p>' + err.stack + '</p>');
                res.end();

                return;
            }

            if (rows) {

                console.log(rows);
                var image_pathes = [];
                for (var i = 0; i < rows.length; i++) {
                    image_pathes[i] = rows[i].image_path;
                    console.log(image_pathes[i]);
                }

                fs.readFile(rows[0].image_path, function (err,data){
                    res.writeHead('200', {'Content-Type': 'image/png;charset=utf8'});
                    res.write(data);
                    res.end();
                });
            }
        });
    }
};

var adduser = function(param, pool, callback) {

    console.log("add user Call..");

    pool.getConnection(function(err, conn) {

        if(err) {
            if(conn) {
                conn.release();
            }

            callback(err, null);
            return;
        }

        console.log('DB Connect Thread id : ' + conn.threadId);
        console.log(param.length);

        var exec = conn.query('insert into user (name, addr, tel, pwd, email) values (?, ?, ?, ?, ?)', [param[3], param[1], param[2], param[0], param[4]], function(err, result) {

            conn.release();
            console.log('exec target SQL : ' + exec.sql);

            if(err) {

                console.log('SQL exec err');
                console.dir(err);

                callback(err, null);

                return;
            }

            callback(null , result);
        });

        conn.on('error', function(err) {
            console.log('데이터베이스 연결 시 에러 발생함.');
            console.dir(err);

            callback(err, null);
        });
    });
};

var getdata = function(product_name, pool, callback) {

    console.log("getData Call...!!!");

    pool.getConnection(function(err, conn){

        if(err) {
            if(conn) {
                conn.release();
            }

            callback(err, null);
            return;
        }

        console.log('DB Connect Thread id : ' + conn.threadId);

        var table = 'Product';

        //START SQL
        //select * from Product INNER JOIN product_store on Product.id = product_store.product_id and Product.product_name = "빵";
        var exec = conn.query("select * from Product INNER JOIN product_store" +
            "on Product.id = product_store.product_id where product_name = ?", [product_name], function(err, rows) {

            conn.release();
            console.log('exec target SQL : ' + exec.sql);
            console.log(rows);
            console.log(err);
            if(rows.length > 0 ) {

                console.log('Find Product [%s]', product_name);
                callback(err, rows);

            } else {

                console.log("Can not find Product");
                callback(null, null);
            }
        })
    });
};

var getjson = function(product_name, pool, callback) {

    console.log("getJson Call...!!!");

    pool.getConnection(function(err, conn){

        if(err) {
            if(conn) {
                conn.release();
            }

            callback(err, null);
            return;
        }

        console.log('DB Connect Thread id : ' + conn.threadId);

        //var table = 'Product';

        //START SQL

        var exec = conn.query("select * from Product INNER JOIN product_store " +
            "on Product.id = product_store.product_id " +
            "INNER JOIN store on product_store.store_id " +
            "= store.id where product_name = ?",[product_name], function(err, rows) {

            conn.release();
            console.log('exec target SQL : ' + exec.sql);
            console.log(rows);
            console.log(err);
            if(rows.length > 0 ) {

                console.log('Find Product');
                callback(err, rows);

            } else {

                console.log("Can not find Product");
                callback(null, null);
            }
        })
    });
};

var getimage = function(product_id, pool, callback) {

    console.log("getImage Call...!!!");

    pool.getConnection(function(err, conn){

        if(err) {
            if(conn) {
                conn.release();
            }

            callback(err, null);
            return;
        }

        console.log('DB Connect Thread id : ' + conn.threadId);

        //var table = 'Product';

        //START SQL

        var exec = conn.query("select * from product_image " +
            "where product_id = ?",[product_id], function(err, rows) {

            conn.release();
            console.log('exec target SQL : ' + exec.sql);
            //console.log(rows);
            //console.log(err);
            if(rows.length > 0 ) {

                console.log('Find Product');
                callback(err, rows);

            } else {

                console.log("Can not find Product");
                callback(null, null);
            }
        })
    });
};

module.exports.addUser = addUser;
module.exports.getData = getData;
module.exports.getJson = getJson;
module.exports.getImage = getImage;