
var login = function(req, res) {

    console.log("/Process/login Call...!!!");

    var paramId = req.body.id || req.query.id;
    var paramPassword = req.body.password || req.query.password;

    console.log('req param : ' + paramId + ', ' + paramPassword);

    var database = req.app.get('database');
    var pool = database.db;

    if(database.db) {
        
        authUser(paramId, paramPassword, pool, function (err, docs) {

            if(err) throw err;

            if(docs) {

                var cnt = docs[0].cnt;

                if(cnt == 1) {
                    req.session.user_id = paramId;
                    res.writeHead('200', {'Content-Type':'application/json;charset=utf8'});
                    res.write('success$');
                    res.write(docs[0].id + "$" + docs[0].name
                        + "$" + docs[0].addr + "$" + docs[0].tel + "$" + docs[0].email);
                    res.end();

                } else {

                    res.writeHead('200', {'Content-Type':'text/html;charset=utf8'});
                    res.write('fail');
                    res.end();
                }

            }
        });
    }
};

var authUser = function(id, password, pool, callback) {

    console.log("authUser Call...!!!");

    pool.getConnection(function(err, conn){

        if(err) {
            if(conn) {
                conn.release();
            }

            callback(err, null);
            return;
        }

        console.log('DB Connect Thread id : ' + conn.threadId);

        var exec = conn.query("select count(*) cnt, id, name, addr, tel, email from user where email=? and pwd=? group by user.id", [id, password], function(err, rows) {

            conn.release();

            console.log('exec target SQL : ' + exec.sql);
            console.log(rows);

            if(err) {
                console.log(err);
                return err;
            }

            if(rows) {

                console.log('Find user');
                callback(err, rows);

            } else {

                console.log("Can not find Product");
                callback(null, null);
            }
        })
    });
};

module.exports.login = login;