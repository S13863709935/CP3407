-- Run this after importing xm-secondhand.sql into the hosted MySQL database.
-- Replace both example domains before executing the statements.

SET @backend_url = 'https://YOUR-BACKEND.up.railway.app';
SET @frontend_url = 'https://YOUR-FRONTEND.vercel.app';

UPDATE `admin`
SET `avatar` = REPLACE(`avatar`, 'http://localhost:9090', @backend_url);

UPDATE `circles`
SET `img` = REPLACE(`img`, 'http://localhost:9090', @backend_url);

UPDATE `goods`
SET `img` = REPLACE(`img`, 'http://localhost:9090', @backend_url),
    `content` = REPLACE(`content`, 'http://localhost:9090', @backend_url);

UPDATE `help`
SET `img` = REPLACE(`img`, 'http://localhost:9090', @backend_url);

UPDATE `orders`
SET `goods_img` = REPLACE(`goods_img`, 'http://localhost:9090', @backend_url);

UPDATE `posts`
SET `img` = REPLACE(`img`, 'http://localhost:9090', @backend_url),
    `content` = REPLACE(
        REPLACE(`content`, 'http://localhost:9090', @backend_url),
        'http://localhost:8080',
        @frontend_url
    );

UPDATE `user`
SET `avatar` = REPLACE(`avatar`, 'http://localhost:9090', @backend_url);
