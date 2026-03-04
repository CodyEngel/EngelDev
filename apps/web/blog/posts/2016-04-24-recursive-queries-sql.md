---
title: Recursive Queries in SQL
date: 2016-04-24
description: How to write recursive queries in SQL.
tags:
  - software engineering
  - sql
---

Recursion can be a tricky thing even with a modern programming language. It's down right confusing when you're trying to
write a recursive query for a stored procedure. This past week I found myself in such a situation. I needed to get all
of child nodes given a parent node while also returning the parent node. Since this was for categories I could have a
top level node result in several levels of child nodes, in other words, recursion was the only solution.

```sql
DECLARE @TopID_in int -- The top level we want to resolve children for
SET @TopID_in = 100 -- ID of parent node, change this value
;WITH HierarchyCTE(ID, ParentID) AS
(
    -- BLOCK ONE
    SELECT Id, ParentId
    FROM Categories
    WHERE Id = @TopID_in
    -- END BLOCK ONE
    UNION ALL
    -- BLOCK TWO
    SELECT Categories.Id, Categories.ParentId
    FROM Categories
    INNER JOIN HierarchyCTE
    ON Categories.ParentId = HierarchyCTE.Id
    -- END BLOCK TWO
)
SELECT *
FROM HierarchyCTE
```

The first select statement, block one, is known as our anchor. This selects our parent node's Id along with it's ParentId.
Our second select statement, block two, is the recursive portion of the query. The first query on block two will be relative
to the anchor result (block one). This produces a result set that we'll call r1 and it is then joined to categories.
Subsequent queries of this block will reference R(n-1) until no more results are found. Then finally we just select all 
of the results from our HierarchyCTE result set.

I've saved this code as a [gist on GitHub](https://gist.github.com/CodyEngel/fb4ad57db2d4e1535442) so feel free to
bookmark or download it.