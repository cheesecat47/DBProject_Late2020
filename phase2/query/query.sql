--q1
select count(account_identity) from account where account_identity = 'customer';

--q2
select account_name, account_address from account where account_identity = 'manager';

--q3
--select a.ACCOUNT_ID, count(a.ACCOUNT_ID) 
--from account a, rating r, write_rate w 
--where a.ACCOUNT_ID = w.ACCOUNT_ID and w.RATING_NO = r.RATING_NO 
--  and 
--group by a.ACCOUNT_ID;

--q4
select count(*) from movie m, CATEGORY c 
where m.MOVIE_REGISTER_NO = c.MOVIE_REGISTER_NO 
  and c.GENRE_NAME = 'Romance' 
  and (MOVIE_START_YEAR between to_date('2020-01-01', 'yyyy-mm-dd') and to_date('2020-12-31', 'yyyy-mm-dd'));

--q5
select count(*) from movie 
where (MOVIE_START_YEAR between to_date('2018-10-07', 'yyyy-mm-dd') and to_date('2020-10-07', 'yyyy-mm-dd'));

--q6
select avg(r.RATING_SCORE) from movie m, RATING r, write_rate w 
where m.MOVIE_REGISTER_NO = w.MOVIE_REGISTER_NO and w.RATING_NO = r.RATING_NO
  and (MOVIE_START_YEAR between to_date('2016-10-07', 'yyyy-mm-dd') and to_date('2020-10-07', 'yyyy-mm-dd'));

--q7
select count(*) from movie where MOVIE_RUNTIME >= 100 ;

--q8
select * 
from (
  select m.MOVIE_TITLE 
  from movie m, genre g, category c, write_rate w, rating r 
  where m.MOVIE_REGISTER_NO = c.MOVIE_REGISTER_NO and c.GENRE_NAME = g.GENRE_NAME 
    and m.MOVIE_REGISTER_NO = w.MOVIE_REGISTER_NO and w.RATING_NO = r.RATING_NO
    and (g.GENRE_NAME = 'Action' or g.GENRE_NAME = 'Comedy') 
    order by r.RATING_SCORE desc
) AS inner1 
limit 1;

--q9
select COUNT(*) from (
  select m.movie_type, e.episode_no, count(*) as num_episode 
  from movie m, episode e 
  where m.movie_register_no = e.movie_register_no and m.movie_type = 'TV Series' 
  group by m.movie_type, e.episode_no
  having count(*) >= 10
) AS inner2;