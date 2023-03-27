### public | act_evt_log                 | table | alfresco
Table "public.act_evt_log"

```shell




Column     |            Type             | Collation | Nullable |                   Default                    | Storage  | Compression | Stats target | Description
---------------+-----------------------------+-----------+----------+----------------------------------------------+----------+-------------+--------------+-------------
 log_nr_       | integer                     |           | not null | nextval('act_evt_log_log_nr__seq'::regclass) | plain    |             |              |
 type_         | character varying(64)       |           |          |                                              | extended |             |              |
 proc_def_id_  | character varying(64)       |           |          |                                              | extended |             |              |
 proc_inst_id_ | character varying(64)       |           |          |                                              | extended |             |              |
 execution_id_ | character varying(64)       |           |          |                                              | extended |             |              |
 task_id_      | character varying(64)       |           |          |                                              | extended |             |              |
 time_stamp_   | timestamp without time zone |           | not null |                                              | plain    |             |              |
 user_id_      | character varying(255)      |           |          |                                              | extended |             |              |
 data_         | bytea                       |           |          |                                              | extended |             |              |
 lock_owner_   | character varying(255)      |           |          |                                              | extended |             |              |
 lock_time_    | timestamp without time zone |           |          |                                              | plain    |             |              |
 is_processed_ | smallint                    |           |          | 0                                            | plain    |             |              |
Indexes:
    "act_evt_log_pkey" PRIMARY KEY, btree (log_nr_)

```

--  public | act_ge_bytearray            | table | alfresco

```shell
Table "public.act_ge_bytearray"
Column     |          Type          | Collation | Nullable | Default | Storage  | Compression | Stats target | Description
----------------+------------------------+-----------+----------+---------+----------+-------------+--------------+-------------
id_            | character varying(64)  |           | not null |         | extended |             |              |
rev_           | integer                |           |          |         | plain    |             |              |
name_          | character varying(255) |           |          |         | extended |             |              |
deployment_id_ | character varying(64)  |           |          |         | extended |             |              |
bytes_         | bytea                  |           |          |         | extended |             |              |
generated_     | boolean                |           |          |         | plain    |             |              |
Indexes:
"act_ge_bytearray_pkey" PRIMARY KEY, btree (id_)
"act_idx_bytear_depl" btree (deployment_id_)
Foreign-key constraints:
"act_fk_bytearr_depl" FOREIGN KEY (deployment_id_) REFERENCES act_re_deployment(id_)
Referenced by:
TABLE "act_procdef_info" CONSTRAINT "act_fk_info_json_ba" FOREIGN KEY (info_json_id_) REFERENCES act_ge_bytearray(id_)
TABLE "act_ru_job" CONSTRAINT "act_fk_job_exception" FOREIGN KEY (exception_stack_id_) REFERENCES act_ge_bytearray(id_)
TABLE "act_re_model" CONSTRAINT "act_fk_model_source" FOREIGN KEY (editor_source_value_id_) REFERENCES act_ge_bytearray(id_)
TABLE "act_re_model" CONSTRAINT "act_fk_model_source_extra" FOREIGN KEY (editor_source_extra_value_id_) REFERENCES act_ge_bytearray(id_)
TABLE "act_ru_variable" CONSTRAINT "act_fk_var_bytearray" FOREIGN KEY (bytearray_id_) REFERENCES act_ge_bytearray(id_)

```


--  public | act_ge_property             | table | alfresco

```shell
                                             Table "public.act_ge_property"
Column |          Type          | Collation | Nullable | Default | Storage  | Compression | Stats target | Description
--------+------------------------+-----------+----------+---------+----------+-------------+--------------+-------------
name_  | character varying(64)  |           | not null |         | extended |             |              |
value_ | character varying(300) |           |          |         | extended |             |              |
rev_   | integer                |           |          |         | plain    |             |              |
Indexes:
"act_ge_property_pkey" PRIMARY KEY, btree (name_)
Access method: heap

```



--  public | act_hi_actinst              | table | alfresco

```shell
Table "public.act_hi_actinst"
Column       |            Type             | Collation | Nullable |        Default        | Storage  | Compression | Stats target | Description
--------------------+-----------------------------+-----------+----------+-----------------------+----------+-------------+--------------+-------------
id_                | character varying(64)       |           | not null |                       | extended |             |              |
proc_def_id_       | character varying(64)       |           | not null |                       | extended |             |              |
proc_inst_id_      | character varying(64)       |           | not null |                       | extended |             |              |
execution_id_      | character varying(64)       |           | not null |                       | extended |             |              |
act_id_            | character varying(255)      |           | not null |                       | extended |             |              |
task_id_           | character varying(64)       |           |          |                       | extended |             |              |
call_proc_inst_id_ | character varying(64)       |           |          |                       | extended |             |              |
act_name_          | character varying(255)      |           |          |                       | extended |             |              |
act_type_          | character varying(255)      |           | not null |                       | extended |             |              |
assignee_          | character varying(255)      |           |          |                       | extended |             |              |
start_time_        | timestamp without time zone |           | not null |                       | plain    |             |              |
end_time_          | timestamp without time zone |           |          |                       | plain    |             |              |
duration_          | bigint                      |           |          |                       | plain    |             |              |
tenant_id_         | character varying(255)      |           |          | ''::character varying | extended |             |              |
Indexes:
"act_hi_actinst_pkey" PRIMARY KEY, btree (id_)
"act_idx_hi_act_inst_end" btree (end_time_)
"act_idx_hi_act_inst_exec" btree (execution_id_, act_id_)
"act_idx_hi_act_inst_procinst" btree (proc_inst_id_, act_id_)
"act_idx_hi_act_inst_start" btree (start_time_)

```


--  public | act_hi_attachment           | table | alfresco

```shell
Table "public.act_hi_attachment"
Column     |            Type             | Collation | Nullable | Default | Storage  | Compression | Stats target | Description
---------------+-----------------------------+-----------+----------+---------+----------+-------------+--------------+-------------
id_           | character varying(64)       |           | not null |         | extended |             |              |
rev_          | integer                     |           |          |         | plain    |             |              |
user_id_      | character varying(255)      |           |          |         | extended |             |              |
name_         | character varying(255)      |           |          |         | extended |             |              |
description_  | character varying(4000)     |           |          |         | extended |             |              |
type_         | character varying(255)      |           |          |         | extended |             |              |
task_id_      | character varying(64)       |           |          |         | extended |             |              |
proc_inst_id_ | character varying(64)       |           |          |         | extended |             |              |
url_          | character varying(4000)     |           |          |         | extended |             |              |
content_id_   | character varying(64)       |           |          |         | extended |             |              |
time_         | timestamp without time zone |           |          |         | plain    |             |              |
Indexes:
"act_hi_attachment_pkey" PRIMARY KEY, btree (id_)

```




--  public | act_hi_comment              | table | alfresco

```shell
Table "public.act_hi_comment"
Column     |            Type             | Collation | Nullable | Default | Storage  | Compression | Stats target | Description
---------------+-----------------------------+-----------+----------+---------+----------+-------------+--------------+-------------
id_           | character varying(64)       |           | not null |         | extended |             |              |
type_         | character varying(255)      |           |          |         | extended |             |              |
time_         | timestamp without time zone |           | not null |         | plain    |             |              |
user_id_      | character varying(255)      |           |          |         | extended |             |              |
task_id_      | character varying(64)       |           |          |         | extended |             |              |
proc_inst_id_ | character varying(64)       |           |          |         | extended |             |              |
action_       | character varying(255)      |           |          |         | extended |             |              |
message_      | character varying(4000)     |           |          |         | extended |             |              |
full_msg_     | bytea                       |           |          |         | extended |             |              |
Indexes:
"act_hi_comment_pkey" PRIMARY KEY, btree (id_)

```


--  public | act_hi_detail               | table | alfresco


```shell
Table "public.act_hi_detail"
Column     |            Type             | Collation | Nullable | Default | Storage  | Compression | Stats target | Description
---------------+-----------------------------+-----------+----------+---------+----------+-------------+--------------+-------------
id_           | character varying(64)       |           | not null |         | extended |             |              |
type_         | character varying(255)      |           | not null |         | extended |             |              |
proc_inst_id_ | character varying(64)       |           |          |         | extended |             |              |
execution_id_ | character varying(64)       |           |          |         | extended |             |              |
task_id_      | character varying(64)       |           |          |         | extended |             |              |
act_inst_id_  | character varying(64)       |           |          |         | extended |             |              |
name_         | character varying(255)      |           | not null |         | extended |             |              |
var_type_     | character varying(64)       |           |          |         | extended |             |              |
rev_          | integer                     |           |          |         | plain    |             |              |
time_         | timestamp without time zone |           | not null |         | plain    |             |              |
bytearray_id_ | character varying(64)       |           |          |         | extended |             |              |
double_       | double precision            |           |          |         | plain    |             |              |
long_         | bigint                      |           |          |         | plain    |             |              |
text_         | character varying(4000)     |           |          |         | extended |             |              |
text2_        | character varying(4000)     |           |          |         | extended |             |              |
Indexes:
"act_hi_detail_pkey" PRIMARY KEY, btree (id_)
"act_idx_hi_detail_act_inst" btree (act_inst_id_)
"act_idx_hi_detail_name" btree (name_)
"act_idx_hi_detail_proc_inst" btree (proc_inst_id_)
"act_idx_hi_detail_task_id" btree (task_id_)
"act_idx_hi_detail_time" btree (time_)

```

--  public | act_hi_identitylink         | table | alfresco


```shell
                                              Table "public.act_hi_identitylink"
    Column     |          Type          | Collation | Nullable | Default | Storage  | Compression | Stats target | Description
---------------+------------------------+-----------+----------+---------+----------+-------------+--------------+-------------
id_           | character varying(64)  |           | not null |         | extended |             |              |
group_id_     | character varying(255) |           |          |         | extended |             |              |
type_         | character varying(255) |           |          |         | extended |             |              |
user_id_      | character varying(255) |           |          |         | extended |             |              |
task_id_      | character varying(64)  |           |          |         | extended |             |              |
proc_inst_id_ | character varying(64)  |           |          |         | extended |             |              |
Indexes:
"act_hi_identitylink_pkey" PRIMARY KEY, btree (id_)
"act_idx_hi_ident_lnk_procinst" btree (proc_inst_id_)
"act_idx_hi_ident_lnk_task" btree (task_id_)
"act_idx_hi_ident_lnk_user" btree (user_id_)
```

--  public | act_hi_procinst             | table | alfresco

```shell

                                                                Table "public.act_hi_procinst"
           Column           |            Type             | Collation | Nullable |        Default        | Storage  | Compression | Stats target | Descr
iption
----------------------------+-----------------------------+-----------+----------+-----------------------+----------+-------------+--------------+------
-------
id_                        | character varying(64)       |           | not null |                       | extended |             |              |
proc_inst_id_              | character varying(64)       |           | not null |                       | extended |             |              |
business_key_              | character varying(255)      |           |          |                       | extended |             |              |
proc_def_id_               | character varying(64)       |           | not null |                       | extended |             |              |
start_time_                | timestamp without time zone |           | not null |                       | plain    |             |              |
end_time_                  | timestamp without time zone |           |          |                       | plain    |             |              |
duration_                  | bigint                      |           |          |                       | plain    |             |              |
start_user_id_             | character varying(255)      |           |          |                       | extended |             |              |
start_act_id_              | character varying(255)      |           |          |                       | extended |             |              |
end_act_id_                | character varying(255)      |           |          |                       | extended |             |              |
super_process_instance_id_ | character varying(64)       |           |          |                       | extended |             |              |
delete_reason_             | character varying(4000)     |           |          |                       | extended |             |              |
tenant_id_                 | character varying(255)      |           |          | ''::character varying | extended |             |              |
name_                      | character varying(255)      |           |          |                       | extended |             |              |
Indexes:
"act_hi_procinst_pkey" PRIMARY KEY, btree (id_)
"act_hi_procinst_proc_inst_id__key" UNIQUE CONSTRAINT, btree (proc_inst_id_)
"act_idx_hi_pro_i_buskey" btree (business_key_)
"act_idx_hi_pro_inst_end" btree (end_time_)
```


--  public | act_hi_taskinst             | table | alfresco

```shell

                                                           Table "public.act_hi_taskinst"
     Column      |            Type             | Collation | Nullable |        Default        | Storage  | Compression | Stats target | Description
-----------------+-----------------------------+-----------+----------+-----------------------+----------+-------------+--------------+-------------
id_             | character varying(64)       |           | not null |                       | extended |             |              |
proc_def_id_    | character varying(64)       |           |          |                       | extended |             |              |
task_def_key_   | character varying(255)      |           |          |                       | extended |             |              |
proc_inst_id_   | character varying(64)       |           |          |                       | extended |             |              |
execution_id_   | character varying(64)       |           |          |                       | extended |             |              |
name_           | character varying(255)      |           |          |                       | extended |             |              |
parent_task_id_ | character varying(64)       |           |          |                       | extended |             |              |
description_    | character varying(4000)     |           |          |                       | extended |             |              |
owner_          | character varying(255)      |           |          |                       | extended |             |              |
assignee_       | character varying(255)      |           |          |                       | extended |             |              |
start_time_     | timestamp without time zone |           | not null |                       | plain    |             |              |
claim_time_     | timestamp without time zone |           |          |                       | plain    |             |              |
end_time_       | timestamp without time zone |           |          |                       | plain    |             |              |
duration_       | bigint                      |           |          |                       | plain    |             |              |
delete_reason_  | character varying(4000)     |           |          |                       | extended |             |              |
priority_       | integer                     |           |          |                       | plain    |             |              |
due_date_       | timestamp without time zone |           |          |                       | plain    |             |              |
form_key_       | character varying(255)      |           |          |                       | extended |             |              |
category_       | character varying(255)      |           |          |                       | extended |             |              |
tenant_id_      | character varying(255)      |           |          | ''::character varying | extended |             |              |
Indexes:
"act_hi_taskinst_pkey" PRIMARY KEY, btree (id_)
"act_idx_hi_task_inst_procinst" btree (proc_inst_id_)
```


--  public | act_hi_varinst              | table | alfresco

```shell
                                                      Table "public.act_hi_varinst"
       Column       |            Type             | Collation | Nullable | Default | Storage  | Compression | Stats target | Description
--------------------+-----------------------------+-----------+----------+---------+----------+-------------+--------------+-------------
id_                | character varying(64)       |           | not null |         | extended |             |              |
proc_inst_id_      | character varying(64)       |           |          |         | extended |             |              |
execution_id_      | character varying(64)       |           |          |         | extended |             |              |
task_id_           | character varying(64)       |           |          |         | extended |             |              |
name_              | character varying(255)      |           | not null |         | extended |             |              |
var_type_          | character varying(100)      |           |          |         | extended |             |              |
rev_               | integer                     |           |          |         | plain    |             |              |
bytearray_id_      | character varying(64)       |           |          |         | extended |             |              |
double_            | double precision            |           |          |         | plain    |             |              |
long_              | bigint                      |           |          |         | plain    |             |              |
text_              | character varying(4000)     |           |          |         | extended |             |              |
text2_             | character varying(4000)     |           |          |         | extended |             |              |
create_time_       | timestamp without time zone |           |          |         | plain    |             |              |
last_updated_time_ | timestamp without time zone |           |          |         | plain    |             |              |
Indexes:
"act_hi_varinst_pkey" PRIMARY KEY, btree (id_)
"act_idx_hi_procvar_name_type" btree (name_, var_type_)
"act_idx_hi_procvar_proc_inst" btree (proc_inst_id_)
"act_idx_hi_procvar_task_id" btree (task_id_)
```


--  public | act_id_group                | table | alfresco

```shell

                                              Table "public.act_id_group"
Column |          Type          | Collation | Nullable | Default | Storage  | Compression | Stats target | Description
--------+------------------------+-----------+----------+---------+----------+-------------+--------------+-------------
id_    | character varying(64)  |           | not null |         | extended |             |              |
rev_   | integer                |           |          |         | plain    |             |              |
name_  | character varying(255) |           |          |         | extended |             |              |
type_  | character varying(255) |           |          |         | extended |             |              |
Indexes:
"act_id_group_pkey" PRIMARY KEY, btree (id_)
Referenced by:
TABLE "act_id_membership" CONSTRAINT "act_fk_memb_group" FOREIGN KEY (group_id_) REFERENCES act_id_group(id_)

```


--  public | act_id_info                 | table | alfresco

```shell

                                                 Table "public.act_id_info"
Column   |          Type          | Collation | Nullable | Default | Storage  | Compression | Stats target | Description
------------+------------------------+-----------+----------+---------+----------+-------------+--------------+-------------
id_        | character varying(64)  |           | not null |         | extended |             |              |
rev_       | integer                |           |          |         | plain    |             |              |
user_id_   | character varying(64)  |           |          |         | extended |             |              |
type_      | character varying(64)  |           |          |         | extended |             |              |
key_       | character varying(255) |           |          |         | extended |             |              |
value_     | character varying(255) |           |          |         | extended |             |              |
password_  | bytea                  |           |          |         | extended |             |              |
parent_id_ | character varying(255) |           |          |         | extended |             |              |
Indexes:
"act_id_info_pkey" PRIMARY KEY, btree (id_)
```


--  public | act_id_membership           | table | alfresco

```shell
                                             Table "public.act_id_membership"
Column   |         Type          | Collation | Nullable | Default | Storage  | Compression | Stats target | Description
-----------+-----------------------+-----------+----------+---------+----------+-------------+--------------+-------------
user_id_  | character varying(64) |           | not null |         | extended |             |              |
group_id_ | character varying(64) |           | not null |         | extended |             |              |
Indexes:
"act_id_membership_pkey" PRIMARY KEY, btree (user_id_, group_id_)
"act_idx_memb_group" btree (group_id_)
"act_idx_memb_user" btree (user_id_)
Foreign-key constraints:
"act_fk_memb_group" FOREIGN KEY (group_id_) REFERENCES act_id_group(id_)
"act_fk_memb_user" FOREIGN KEY (user_id_) REFERENCES act_id_user(id_)
```

--  public | act_id_user                 | table | alfresco
```shell
                                                 Table "public.act_id_user"
Column    |          Type          | Collation | Nullable | Default | Storage  | Compression | Stats target | Description
-------------+------------------------+-----------+----------+---------+----------+-------------+--------------+-------------
id_         | character varying(64)  |           | not null |         | extended |             |              |
rev_        | integer                |           |          |         | plain    |             |              |
first_      | character varying(255) |           |          |         | extended |             |              |
last_       | character varying(255) |           |          |         | extended |             |              |
email_      | character varying(255) |           |          |         | extended |             |              |
pwd_        | character varying(255) |           |          |         | extended |             |              |
picture_id_ | character varying(64)  |           |          |         | extended |             |              |
Indexes:
"act_id_user_pkey" PRIMARY KEY, btree (id_)
Referenced by:
TABLE "act_id_membership" CONSTRAINT "act_fk_memb_user" FOREIGN KEY (user_id_) REFERENCES act_id_user(id_)
```

--  public | act_procdef_info            | table | alfresco

```shell

                                               Table "public.act_procdef_info"
    Column     |         Type          | Collation | Nullable | Default | Storage  | Compression | Stats target | Description
---------------+-----------------------+-----------+----------+---------+----------+-------------+--------------+-------------
id_           | character varying(64) |           | not null |         | extended |             |              |
proc_def_id_  | character varying(64) |           | not null |         | extended |             |              |
rev_          | integer               |           |          |         | plain    |             |              |
info_json_id_ | character varying(64) |           |          |         | extended |             |              |
Indexes:
"act_procdef_info_pkey" PRIMARY KEY, btree (id_)
"act_idx_procdef_info_json" btree (info_json_id_)
"act_idx_procdef_info_proc" btree (proc_def_id_)
"act_uniq_info_procdef" UNIQUE CONSTRAINT, btree (proc_def_id_)
Foreign-key constraints:
"act_fk_info_json_ba" FOREIGN KEY (info_json_id_) REFERENCES act_ge_bytearray(id_)
"act_fk_info_procdef" FOREIGN KEY (proc_def_id_) REFERENCES act_re_procdef(id_)
```

--  public | act_re_deployment           | table | alfresco

```shell
                                                        Table "public.act_re_deployment"
    Column    |            Type             | Collation | Nullable |        Default        | Storage  | Compression | Stats target | Description
--------------+-----------------------------+-----------+----------+-----------------------+----------+-------------+--------------+-------------
id_          | character varying(64)       |           | not null |                       | extended |             |              |
name_        | character varying(255)      |           |          |                       | extended |             |              |
category_    | character varying(255)      |           |          |                       | extended |             |              |
tenant_id_   | character varying(255)      |           |          | ''::character varying | extended |             |              |
deploy_time_ | timestamp without time zone |           |          |                       | plain    |             |              |
Indexes:
"act_re_deployment_pkey" PRIMARY KEY, btree (id_)
Referenced by:
TABLE "act_ge_bytearray" CONSTRAINT "act_fk_bytearr_depl" FOREIGN KEY (deployment_id_) REFERENCES act_re_deployment(id_)
TABLE "act_re_model" CONSTRAINT "act_fk_model_deployment" FOREIGN KEY (deployment_id_) REFERENCES act_re_deployment(id_)
```

--  public | act_re_model                | table | alfresco

```shell
                                                                   Table "public.act_re_model"
            Column             |            Type             | Collation | Nullable |        Default        | Storage  | Compression | Stats target | De
scription
-------------------------------+-----------------------------+-----------+----------+-----------------------+----------+-------------+--------------+---
----------
id_                           | character varying(64)       |           | not null |                       | extended |             |              |
rev_                          | integer                     |           |          |                       | plain    |             |              |
name_                         | character varying(255)      |           |          |                       | extended |             |              |
key_                          | character varying(255)      |           |          |                       | extended |             |              |
category_                     | character varying(255)      |           |          |                       | extended |             |              |
create_time_                  | timestamp without time zone |           |          |                       | plain    |             |              |
last_update_time_             | timestamp without time zone |           |          |                       | plain    |             |              |
version_                      | integer                     |           |          |                       | plain    |             |              |
meta_info_                    | character varying(4000)     |           |          |                       | extended |             |              |
deployment_id_                | character varying(64)       |           |          |                       | extended |             |              |
editor_source_value_id_       | character varying(64)       |           |          |                       | extended |             |              |
editor_source_extra_value_id_ | character varying(64)       |           |          |                       | extended |             |              |
tenant_id_                    | character varying(255)      |           |          | ''::character varying | extended |             |              |
Indexes:
"act_re_model_pkey" PRIMARY KEY, btree (id_)
"act_idx_model_deployment" btree (deployment_id_)
"act_idx_model_source" btree (editor_source_value_id_)
"act_idx_model_source_extra" btree (editor_source_extra_value_id_)
Foreign-key constraints:
"act_fk_model_deployment" FOREIGN KEY (deployment_id_) REFERENCES act_re_deployment(id_)
"act_fk_model_source" FOREIGN KEY (editor_source_value_id_) REFERENCES act_ge_bytearray(id_)
"act_fk_model_source_extra" FOREIGN KEY (editor_source_extra_value_id_) REFERENCES act_ge_bytearray(id_)
```

--  public | act_re_procdef              | table | alfresco
```shell
                                                             Table "public.act_re_procdef"
         Column          |          Type           | Collation | Nullable |        Default        | Storage  | Compression | Stats target | Description
-------------------------+-------------------------+-----------+----------+-----------------------+----------+-------------+--------------+-------------
id_                     | character varying(64)   |           | not null |                       | extended |             |              |
rev_                    | integer                 |           |          |                       | plain    |             |              |
category_               | character varying(255)  |           |          |                       | extended |             |              |
name_                   | character varying(255)  |           |          |                       | extended |             |              |
key_                    | character varying(255)  |           | not null |                       | extended |             |              |
version_                | integer                 |           | not null |                       | plain    |             |              |
deployment_id_          | character varying(64)   |           |          |                       | extended |             |              |
resource_name_          | character varying(4000) |           |          |                       | extended |             |              |
dgrm_resource_name_     | character varying(4000) |           |          |                       | extended |             |              |
description_            | character varying(4000) |           |          |                       | extended |             |              |
has_start_form_key_     | boolean                 |           |          |                       | plain    |             |              |
has_graphical_notation_ | boolean                 |           |          |                       | plain    |             |              |
suspension_state_       | integer                 |           |          |                       | plain    |             |              |
tenant_id_              | character varying(255)  |           |          | ''::character varying | extended |             |              |
Indexes:
"act_re_procdef_pkey" PRIMARY KEY, btree (id_)
"act_uniq_procdef" UNIQUE CONSTRAINT, btree (key_, version_, tenant_id_)
Referenced by:
TABLE "act_ru_identitylink" CONSTRAINT "act_fk_athrz_procedef" FOREIGN KEY (proc_def_id_) REFERENCES act_re_procdef(id_)
TABLE "act_ru_execution" CONSTRAINT "act_fk_exe_procdef" FOREIGN KEY (proc_def_id_) REFERENCES act_re_procdef(id_)
TABLE "act_procdef_info" CONSTRAINT "act_fk_info_procdef" FOREIGN KEY (proc_def_id_) REFERENCES act_re_procdef(id_)
TABLE "act_ru_task" CONSTRAINT "act_fk_task_procdef" FOREIGN KEY (proc_def_id_) REFERENCES act_re_procdef(id_)
```


--  public | act_ru_event_subscr         | table | alfresco

```shell
                                                        Table "public.act_ru_event_subscr"
     Column     |            Type             | Collation | Nullable |        Default        | Storage  | Compression | Stats target | Description
----------------+-----------------------------+-----------+----------+-----------------------+----------+-------------+--------------+-------------
id_            | character varying(64)       |           | not null |                       | extended |             |              |
rev_           | integer                     |           |          |                       | plain    |             |              |
event_type_    | character varying(255)      |           | not null |                       | extended |             |              |
event_name_    | character varying(255)      |           |          |                       | extended |             |              |
execution_id_  | character varying(64)       |           |          |                       | extended |             |              |
proc_inst_id_  | character varying(64)       |           |          |                       | extended |             |              |
activity_id_   | character varying(64)       |           |          |                       | extended |             |              |
configuration_ | character varying(255)      |           |          |                       | extended |             |              |
created_       | timestamp without time zone |           | not null |                       | plain    |             |              |
proc_def_id_   | character varying(64)       |           |          |                       | extended |             |              |
tenant_id_     | character varying(255)      |           |          | ''::character varying | extended |             |              |
Indexes:
"act_ru_event_subscr_pkey" PRIMARY KEY, btree (id_)
"act_idx_event_subscr" btree (execution_id_)
"act_idx_event_subscr_config_" btree (configuration_)
Foreign-key constraints:
"act_fk_event_exec" FOREIGN KEY (execution_id_) REFERENCES act_ru_execution(id_)
```

--  public | act_ru_execution            | table | alfresco

```shell
                                                           Table "public.act_ru_execution"
      Column       |            Type             | Collation | Nullable |        Default        | Storage  | Compression | Stats target | Description
-------------------+-----------------------------+-----------+----------+-----------------------+----------+-------------+--------------+-------------
id_               | character varying(64)       |           | not null |                       | extended |             |              |
rev_              | integer                     |           |          |                       | plain    |             |              |
proc_inst_id_     | character varying(64)       |           |          |                       | extended |             |              |
business_key_     | character varying(255)      |           |          |                       | extended |             |              |
parent_id_        | character varying(64)       |           |          |                       | extended |             |              |
proc_def_id_      | character varying(64)       |           |          |                       | extended |             |              |
super_exec_       | character varying(64)       |           |          |                       | extended |             |              |
act_id_           | character varying(255)      |           |          |                       | extended |             |              |
is_active_        | boolean                     |           |          |                       | plain    |             |              |
is_concurrent_    | boolean                     |           |          |                       | plain    |             |              |
is_scope_         | boolean                     |           |          |                       | plain    |             |              |
is_event_scope_   | boolean                     |           |          |                       | plain    |             |              |
suspension_state_ | integer                     |           |          |                       | plain    |             |              |
cached_ent_state_ | integer                     |           |          |                       | plain    |             |              |
tenant_id_        | character varying(255)      |           |          | ''::character varying | extended |             |              |
name_             | character varying(255)      |           |          |                       | extended |             |              |
lock_time_        | timestamp without time zone |           |          |                       | plain    |             |              |
Indexes:
"act_ru_execution_pkey" PRIMARY KEY, btree (id_)
"act_idx_exe_parent" btree (parent_id_)
"act_idx_exe_procdef" btree (proc_def_id_)
"act_idx_exe_procinst" btree (proc_inst_id_)
"act_idx_exe_super" btree (super_exec_)
"act_idx_exec_buskey" btree (business_key_)
Foreign-key constraints:
"act_fk_exe_parent" FOREIGN KEY (parent_id_) REFERENCES act_ru_execution(id_)
"act_fk_exe_procdef" FOREIGN KEY (proc_def_id_) REFERENCES act_re_procdef(id_)
"act_fk_exe_procinst" FOREIGN KEY (proc_inst_id_) REFERENCES act_ru_execution(id_)
"act_fk_exe_super" FOREIGN KEY (super_exec_) REFERENCES act_ru_execution(id_)
Referenced by:
TABLE "act_ru_event_subscr" CONSTRAINT "act_fk_event_exec" FOREIGN KEY (execution_id_) REFERENCES act_ru_execution(id_)
TABLE "act_ru_execution" CONSTRAINT "act_fk_exe_parent" FOREIGN KEY (parent_id_) REFERENCES act_ru_execution(id_)
TABLE "act_ru_execution" CONSTRAINT "act_fk_exe_procinst" FOREIGN KEY (proc_inst_id_) REFERENCES act_ru_execution(id_)
TABLE "act_ru_execution" CONSTRAINT "act_fk_exe_super" FOREIGN KEY (super_exec_) REFERENCES act_ru_execution(id_)
TABLE "act_ru_identitylink" CONSTRAINT "act_fk_idl_procinst" FOREIGN KEY (proc_inst_id_) REFERENCES act_ru_execution(id_)
TABLE "act_ru_task" CONSTRAINT "act_fk_task_exe" FOREIGN KEY (execution_id_) REFERENCES act_ru_execution(id_)
TABLE "act_ru_task" CONSTRAINT "act_fk_task_procinst" FOREIGN KEY (proc_inst_id_) REFERENCES act_ru_execution(id_)
TABLE "act_ru_variable" CONSTRAINT "act_fk_var_exe" FOREIGN KEY (execution_id_) REFERENCES act_ru_execution(id_)
TABLE "act_ru_variable" CONSTRAINT "act_fk_var_procinst" FOREIGN KEY (proc_inst_id_) REFERENCES act_ru_execution(id_)
```


--  public | act_ru_identitylink         | table | alfresco

```shell
                                              Table "public.act_ru_identitylink"
    Column     |          Type          | Collation | Nullable | Default | Storage  | Compression | Stats target | Description
---------------+------------------------+-----------+----------+---------+----------+-------------+--------------+-------------
id_           | character varying(64)  |           | not null |         | extended |             |              |
rev_          | integer                |           |          |         | plain    |             |              |
group_id_     | character varying(255) |           |          |         | extended |             |              |
type_         | character varying(255) |           |          |         | extended |             |              |
user_id_      | character varying(255) |           |          |         | extended |             |              |
task_id_      | character varying(64)  |           |          |         | extended |             |              |
proc_inst_id_ | character varying(64)  |           |          |         | extended |             |              |
proc_def_id_  | character varying(64)  |           |          |         | extended |             |              |
Indexes:
"act_ru_identitylink_pkey" PRIMARY KEY, btree (id_)
"act_idx_athrz_procedef" btree (proc_def_id_)
"act_idx_ident_lnk_group" btree (group_id_)
"act_idx_ident_lnk_user" btree (user_id_)
"act_idx_idl_procinst" btree (proc_inst_id_)
"act_idx_tskass_task" btree (task_id_)
Foreign-key constraints:
"act_fk_athrz_procedef" FOREIGN KEY (proc_def_id_) REFERENCES act_re_procdef(id_)
"act_fk_idl_procinst" FOREIGN KEY (proc_inst_id_) REFERENCES act_ru_execution(id_)
"act_fk_tskass_task" FOREIGN KEY (task_id_) REFERENCES act_ru_task(id_)
```

--  public | act_ru_job                  | table | alfresco

```shell

                                                                Table "public.act_ru_job"
        Column        |            Type             | Collation | Nullable |        Default        | Storage  | Compression | Stats target | Description

----------------------+-----------------------------+-----------+----------+-----------------------+----------+-------------+--------------+------------
-
id_                  | character varying(64)       |           | not null |                       | extended |             |              |
rev_                 | integer                     |           |          |                       | plain    |             |              |
type_                | character varying(255)      |           | not null |                       | extended |             |              |
lock_exp_time_       | timestamp without time zone |           |          |                       | plain    |             |              |
lock_owner_          | character varying(255)      |           |          |                       | extended |             |              |
exclusive_           | boolean                     |           |          |                       | plain    |             |              |
execution_id_        | character varying(64)       |           |          |                       | extended |             |              |
process_instance_id_ | character varying(64)       |           |          |                       | extended |             |              |
proc_def_id_         | character varying(64)       |           |          |                       | extended |             |              |
retries_             | integer                     |           |          |                       | plain    |             |              |
exception_stack_id_  | character varying(64)       |           |          |                       | extended |             |              |
exception_msg_       | character varying(4000)     |           |          |                       | extended |             |              |
duedate_             | timestamp without time zone |           |          |                       | plain    |             |              |
repeat_              | character varying(255)      |           |          |                       | extended |             |              |
handler_type_        | character varying(255)      |           |          |                       | extended |             |              |
handler_cfg_         | character varying(4000)     |           |          |                       | extended |             |              |
tenant_id_           | character varying(255)      |           |          | ''::character varying | extended |             |              |
Indexes:
"act_ru_job_pkey" PRIMARY KEY, btree (id_)
"act_idx_job_exception" btree (exception_stack_id_)
Foreign-key constraints:
"act_fk_job_exception" FOREIGN KEY (exception_stack_id_) REFERENCES act_ge_bytearray(id_)
```

--  public | act_ru_task                 | table | alfresco

```shell
                                                              Table "public.act_ru_task"
      Column       |            Type             | Collation | Nullable |        Default        | Storage  | Compression | Stats target | Description
-------------------+-----------------------------+-----------+----------+-----------------------+----------+-------------+--------------+-------------
id_               | character varying(64)       |           | not null |                       | extended |             |              |
rev_              | integer                     |           |          |                       | plain    |             |              |
execution_id_     | character varying(64)       |           |          |                       | extended |             |              |
proc_inst_id_     | character varying(64)       |           |          |                       | extended |             |              |
proc_def_id_      | character varying(64)       |           |          |                       | extended |             |              |
name_             | character varying(255)      |           |          |                       | extended |             |              |
parent_task_id_   | character varying(64)       |           |          |                       | extended |             |              |
description_      | character varying(4000)     |           |          |                       | extended |             |              |
task_def_key_     | character varying(255)      |           |          |                       | extended |             |              |
owner_            | character varying(255)      |           |          |                       | extended |             |              |
assignee_         | character varying(255)      |           |          |                       | extended |             |              |
delegation_       | character varying(64)       |           |          |                       | extended |             |              |
priority_         | integer                     |           |          |                       | plain    |             |              |
create_time_      | timestamp without time zone |           |          |                       | plain    |             |              |
due_date_         | timestamp without time zone |           |          |                       | plain    |             |              |
category_         | character varying(255)      |           |          |                       | extended |             |              |
suspension_state_ | integer                     |           |          |                       | plain    |             |              |
tenant_id_        | character varying(255)      |           |          | ''::character varying | extended |             |              |
form_key_         | character varying(255)      |           |          |                       | extended |             |              |
Indexes:
"act_ru_task_pkey" PRIMARY KEY, btree (id_)
"act_idx_task_create" btree (create_time_)
"act_idx_task_exec" btree (execution_id_)
"act_idx_task_procdef" btree (proc_def_id_)
"act_idx_task_procinst" btree (proc_inst_id_)
Foreign-key constraints:
"act_fk_task_exe" FOREIGN KEY (execution_id_) REFERENCES act_ru_execution(id_)
"act_fk_task_procdef" FOREIGN KEY (proc_def_id_) REFERENCES act_re_procdef(id_)
"act_fk_task_procinst" FOREIGN KEY (proc_inst_id_) REFERENCES act_ru_execution(id_)
Referenced by:
TABLE "act_ru_identitylink" CONSTRAINT "act_fk_tskass_task" FOREIGN KEY (task_id_) REFERENCES act_ru_task(id_)
```


--  public | act_ru_variable             | table | alfresco

```shell
                                                 Table "public.act_ru_variable"
    Column     |          Type           | Collation | Nullable | Default | Storage  | Compression | Stats target | Description
---------------+-------------------------+-----------+----------+---------+----------+-------------+--------------+-------------
id_           | character varying(64)   |           | not null |         | extended |             |              |
rev_          | integer                 |           |          |         | plain    |             |              |
type_         | character varying(255)  |           | not null |         | extended |             |              |
name_         | character varying(255)  |           | not null |         | extended |             |              |
execution_id_ | character varying(64)   |           |          |         | extended |             |              |
proc_inst_id_ | character varying(64)   |           |          |         | extended |             |              |
task_id_      | character varying(64)   |           |          |         | extended |             |              |
bytearray_id_ | character varying(64)   |           |          |         | extended |             |              |
double_       | double precision        |           |          |         | plain    |             |              |
long_         | bigint                  |           |          |         | plain    |             |              |
text_         | character varying(4000) |           |          |         | extended |             |              |
text2_        | character varying(4000) |           |          |         | extended |             |              |
Indexes:
"act_ru_variable_pkey" PRIMARY KEY, btree (id_)
"act_idx_var_bytearray" btree (bytearray_id_)
"act_idx_var_exe" btree (execution_id_)
"act_idx_var_procinst" btree (proc_inst_id_)
"act_idx_variable_task_id" btree (task_id_)
Foreign-key constraints:
"act_fk_var_bytearray" FOREIGN KEY (bytearray_id_) REFERENCES act_ge_bytearray(id_)
"act_fk_var_exe" FOREIGN KEY (execution_id_) REFERENCES act_ru_execution(id_)
"act_fk_var_procinst" FOREIGN KEY (proc_inst_id_) REFERENCES act_ru_execution(id_)
```

--  public | alf_access_control_entry    | table | alfresco
```shell
                                    Table "public.alf_access_control_entry"
    Column     |  Type   | Collation | Nullable | Default | Storage | Compression | Stats target | Description
---------------+---------+-----------+----------+---------+---------+-------------+--------------+-------------
id            | bigint  |           | not null |         | plain   |             |              |
version       | bigint  |           | not null |         | plain   |             |              |
permission_id | bigint  |           | not null |         | plain   |             |              |
authority_id  | bigint  |           | not null |         | plain   |             |              |
allowed       | boolean |           | not null |         | plain   |             |              |
applies       | integer |           | not null |         | plain   |             |              |
context_id    | bigint  |           |          |         | plain   |             |              |
Indexes:
"alf_access_control_entry_pkey" PRIMARY KEY, btree (id)
"fk_alf_ace_auth" btree (authority_id)
"fk_alf_ace_ctx" btree (context_id)
"fk_alf_ace_perm" btree (permission_id)
"permission_id" UNIQUE, btree (permission_id, authority_id, allowed, applies)
Foreign-key constraints:
"fk_alf_ace_auth" FOREIGN KEY (authority_id) REFERENCES alf_authority(id)
"fk_alf_ace_ctx" FOREIGN KEY (context_id) REFERENCES alf_ace_context(id)
"fk_alf_ace_perm" FOREIGN KEY (permission_id) REFERENCES alf_permission(id)
Referenced by:
TABLE "alf_acl_member" CONSTRAINT "fk_alf_aclm_ace" FOREIGN KEY (ace_id) REFERENCES alf_access_control_entry(id)
```

--  public | alf_access_control_list     | table | alfresco
```shell
Table "public.alf_access_control_list"
Column      |         Type          | Collation | Nullable | Default | Storage  | Compression | Stats target | Description
------------------+-----------------------+-----------+----------+---------+----------+-------------+--------------+-------------
id               | bigint                |           | not null |         | plain    |             |              |
version          | bigint                |           | not null |         | plain    |             |              |
acl_id           | character varying(36) |           | not null |         | extended |             |              |
latest           | boolean               |           | not null |         | plain    |             |              |
acl_version      | bigint                |           | not null |         | plain    |             |              |
inherits         | boolean               |           | not null |         | plain    |             |              |
inherits_from    | bigint                |           |          |         | plain    |             |              |
type             | integer               |           | not null |         | plain    |             |              |
inherited_acl    | bigint                |           |          |         | plain    |             |              |
is_versioned     | boolean               |           | not null |         | plain    |             |              |
requires_version | boolean               |           | not null |         | plain    |             |              |
acl_change_set   | bigint                |           |          |         | plain    |             |              |
Indexes:
"alf_access_control_list_pkey" PRIMARY KEY, btree (id)
"acl_id" UNIQUE, btree (acl_id, latest, acl_version)
"fk_alf_acl_acs" btree (acl_change_set)
"idx_alf_acl_acs" btree (acl_change_set, id)
"idx_alf_acl_inh" btree (inherits, inherits_from)
Foreign-key constraints:
"fk_alf_acl_acs" FOREIGN KEY (acl_change_set) REFERENCES alf_acl_change_set(id)
Referenced by:
TABLE "alf_acl_member" CONSTRAINT "fk_alf_aclm_acl" FOREIGN KEY (acl_id) REFERENCES alf_access_control_list(id)
TABLE "alf_node" CONSTRAINT "fk_alf_node_acl" FOREIGN KEY (acl_id) REFERENCES alf_access_control_list(id)
```


--  public | alf_ace_context             | table | alfresco

```shell
                                                  Table "public.alf_ace_context"
      Column      |          Type           | Collation | Nullable | Default | Storage  | Compression | Stats target | Description
------------------+-------------------------+-----------+----------+---------+----------+-------------+--------------+-------------
id               | bigint                  |           | not null |         | plain    |             |              |
version          | bigint                  |           | not null |         | plain    |             |              |
class_context    | character varying(1024) |           |          |         | extended |             |              |
property_context | character varying(1024) |           |          |         | extended |             |              |
kvp_context      | character varying(1024) |           |          |         | extended |             |              |
Indexes:
"alf_ace_context_pkey" PRIMARY KEY, btree (id)
Referenced by:
TABLE "alf_access_control_entry" CONSTRAINT "fk_alf_ace_ctx" FOREIGN KEY (context_id) REFERENCES alf_ace_context(id)
```

--  public | alf_acl_change_set          | table | alfresco

```shell

                                       Table "public.alf_acl_change_set"
     Column     |  Type  | Collation | Nullable | Default | Storage | Compression | Stats target | Description
----------------+--------+-----------+----------+---------+---------+-------------+--------------+-------------
id             | bigint |           | not null |         | plain   |             |              |
commit_time_ms | bigint |           |          |         | plain   |             |              |
Indexes:
"alf_acl_change_set_pkey" PRIMARY KEY, btree (id)
"idx_alf_acs_ctms" btree (commit_time_ms, id)
Referenced by:
TABLE "alf_access_control_list" CONSTRAINT "fk_alf_acl_acs" FOREIGN KEY (acl_change_set) REFERENCES alf_acl_change_set(id)
```


--  public | alf_acl_member              | table | alfresco

```shell
                                      Table "public.alf_acl_member"
Column  |  Type   | Collation | Nullable | Default | Storage | Compression | Stats target | Description
---------+---------+-----------+----------+---------+---------+-------------+--------------+-------------
id      | bigint  |           | not null |         | plain   |             |              |
version | bigint  |           | not null |         | plain   |             |              |
acl_id  | bigint  |           | not null |         | plain   |             |              |
ace_id  | bigint  |           | not null |         | plain   |             |              |
pos     | integer |           | not null |         | plain   |             |              |
Indexes:
"alf_acl_member_pkey" PRIMARY KEY, btree (id)
"aclm_acl_id" UNIQUE, btree (acl_id, ace_id, pos)
"fk_alf_aclm_ace" btree (ace_id)
"fk_alf_aclm_acl" btree (acl_id)
Foreign-key constraints:
"fk_alf_aclm_ace" FOREIGN KEY (ace_id) REFERENCES alf_access_control_entry(id)
"fk_alf_aclm_acl" FOREIGN KEY (acl_id) REFERENCES alf_access_control_list(id)
```

--  public | alf_activity_feed           | table | alfresco

```shell

                                                   Table "public.alf_activity_feed"
      Column      |            Type             | Collation | Nullable | Default | Storage  | Compression | Stats target | Description
------------------+-----------------------------+-----------+----------+---------+----------+-------------+--------------+-------------
id               | bigint                      |           | not null |         | plain    |             |              |
post_id          | bigint                      |           |          |         | plain    |             |              |
post_date        | timestamp without time zone |           | not null |         | plain    |             |              |
activity_summary | character varying(1024)     |           |          |         | extended |             |              |
feed_user_id     | character varying(255)      |           |          |         | extended |             |              |
activity_type    | character varying(255)      |           | not null |         | extended |             |              |
site_network     | character varying(255)      |           |          |         | extended |             |              |
app_tool         | character varying(36)       |           |          |         | extended |             |              |
post_user_id     | character varying(255)      |           | not null |         | extended |             |              |
feed_date        | timestamp without time zone |           | not null |         | plain    |             |              |
Indexes:
"alf_activity_feed_pkey" PRIMARY KEY, btree (id)
"feed_feeduserid_idx" btree (feed_user_id)
"feed_postdate_idx" btree (post_date)
"feed_postuserid_idx" btree (post_user_id)
"feed_sitenetwork_idx" btree (site_network)

```

--  public | alf_activity_feed_control   | table | alfresco

```shell
                                              Table "public.alf_activity_feed_control"
    Column     |            Type             | Collation | Nullable | Default | Storage  | Compression | Stats target | Description
---------------+-----------------------------+-----------+----------+---------+----------+-------------+--------------+-------------
id            | bigint                      |           | not null |         | plain    |             |              |
feed_user_id  | character varying(255)      |           | not null |         | extended |             |              |
site_network  | character varying(255)      |           |          |         | extended |             |              |
app_tool      | character varying(36)       |           |          |         | extended |             |              |
last_modified | timestamp without time zone |           | not null |         | plain    |             |              |
Indexes:
"alf_activity_feed_control_pkey" PRIMARY KEY, btree (id)
"feedctrl_feeduserid_idx" btree (feed_user_id)
```

--  public | alf_activity_post           | table | alfresco
```shell
                                                  Table "public.alf_activity_post"
    Column     |            Type             | Collation | Nullable | Default | Storage  | Compression | Stats target | Description
---------------+-----------------------------+-----------+----------+---------+----------+-------------+--------------+-------------
sequence_id   | bigint                      |           | not null |         | plain    |             |              |
post_date     | timestamp without time zone |           | not null |         | plain    |             |              |
status        | character varying(10)       |           | not null |         | extended |             |              |
activity_data | character varying(1024)     |           | not null |         | extended |             |              |
post_user_id  | character varying(255)      |           | not null |         | extended |             |              |
job_task_node | integer                     |           | not null |         | plain    |             |              |
site_network  | character varying(255)      |           |          |         | extended |             |              |
app_tool      | character varying(36)       |           |          |         | extended |             |              |
activity_type | character varying(255)      |           | not null |         | extended |             |              |
last_modified | timestamp without time zone |           | not null |         | plain    |             |              |
Indexes:
"alf_activity_post_pkey" PRIMARY KEY, btree (sequence_id)
"post_jobtasknode_idx" btree (job_task_node)
"post_status_idx" btree (status)
```
--  public | alf_applied_patch           | table | alfresco

```shell

                                                    Table "public.alf_applied_patch"
      Column       |            Type             | Collation | Nullable | Default | Storage  | Compression | Stats target | Description
-------------------+-----------------------------+-----------+----------+---------+----------+-------------+--------------+-------------
id                | character varying(64)       |           | not null |         | extended |             |              |
description       | character varying(1024)     |           |          |         | extended |             |              |
fixes_from_schema | integer                     |           |          |         | plain    |             |              |
fixes_to_schema   | integer                     |           |          |         | plain    |             |              |
applied_to_schema | integer                     |           |          |         | plain    |             |              |
target_schema     | integer                     |           |          |         | plain    |             |              |
applied_on_date   | timestamp without time zone |           |          |         | plain    |             |              |
applied_to_server | character varying(64)       |           |          |         | extended |             |              |
was_executed      | boolean                     |           |          |         | plain    |             |              |
succeeded         | boolean                     |           |          |         | plain    |             |              |
report            | character varying(1024)     |           |          |         | extended |             |              |
Indexes:
"alf_applied_patch_pkey" PRIMARY KEY, btree (id)
```
 

--  public | alf_audit_app               | table | alfresco

```shell

                                           Table "public.alf_audit_app"
      Column       |  Type   | Collation | Nullable | Default | Storage | Compression | Stats target | Description
-------------------+---------+-----------+----------+---------+---------+-------------+--------------+-------------
id                | bigint  |           | not null |         | plain   |             |              |
version           | integer |           | not null |         | plain   |             |              |
app_name_id       | bigint  |           | not null |         | plain   |             |              |
audit_model_id    | bigint  |           | not null |         | plain   |             |              |
disabled_paths_id | bigint  |           | not null |         | plain   |             |              |
Indexes:
"alf_audit_app_pkey" PRIMARY KEY, btree (id)
"fk_alf_aud_app_dis" btree (disabled_paths_id)
"fk_alf_aud_app_mod" btree (audit_model_id)
"idx_alf_aud_app_an" UNIQUE CONSTRAINT, btree (app_name_id)
Foreign-key constraints:
"fk_alf_aud_app_an" FOREIGN KEY (app_name_id) REFERENCES alf_prop_value(id)
"fk_alf_aud_app_dis" FOREIGN KEY (disabled_paths_id) REFERENCES alf_prop_root(id)
"fk_alf_aud_app_mod" FOREIGN KEY (audit_model_id) REFERENCES alf_audit_model(id) ON DELETE CASCADE
Referenced by:
TABLE "alf_audit_entry" CONSTRAINT "fk_alf_aud_ent_app" FOREIGN KEY (audit_app_id) REFERENCES alf_audit_app(id) ON DELETE CASCADE
```

--  public | alf_audit_entry             | table | alfresco
```shell
Table "public.alf_audit_entry"
Column      |  Type  | Collation | Nullable | Default | Storage | Compression | Stats target | Description
-----------------+--------+-----------+----------+---------+---------+-------------+--------------+-------------
id              | bigint |           | not null |         | plain   |             |              |
audit_app_id    | bigint |           | not null |         | plain   |             |              |
audit_time      | bigint |           | not null |         | plain   |             |              |
audit_user_id   | bigint |           |          |         | plain   |             |              |
audit_values_id | bigint |           |          |         | plain   |             |              |
Indexes:
"alf_audit_entry_pkey" PRIMARY KEY, btree (id)
"fk_alf_aud_ent_app" btree (audit_app_id)
"fk_alf_aud_ent_pro" btree (audit_values_id)
"fk_alf_aud_ent_use" btree (audit_user_id)
"idx_alf_aud_ent_tm" btree (audit_time)
Foreign-key constraints:
"fk_alf_aud_ent_app" FOREIGN KEY (audit_app_id) REFERENCES alf_audit_app(id) ON DELETE CASCADE
"fk_alf_aud_ent_pro" FOREIGN KEY (audit_values_id) REFERENCES alf_prop_root(id)
"fk_alf_aud_ent_use" FOREIGN KEY (audit_user_id) REFERENCES alf_prop_value(id)
```

--  public | alf_audit_model             | table | alfresco

```shell
                                         Table "public.alf_audit_model"
     Column      |  Type  | Collation | Nullable | Default | Storage | Compression | Stats target | Description
-----------------+--------+-----------+----------+---------+---------+-------------+--------------+-------------
id              | bigint |           | not null |         | plain   |             |              |
content_data_id | bigint |           | not null |         | plain   |             |              |
content_crc     | bigint |           | not null |         | plain   |             |              |
Indexes:
"alf_audit_model_pkey" PRIMARY KEY, btree (id)
"fk_alf_aud_mod_cd" btree (content_data_id)
"idx_alf_aud_mod_cr" UNIQUE, btree (content_crc)
Foreign-key constraints:
"fk_alf_aud_mod_cd" FOREIGN KEY (content_data_id) REFERENCES alf_content_data(id)
Referenced by:
TABLE "alf_audit_app" CONSTRAINT "fk_alf_aud_app_mod" FOREIGN KEY (audit_model_id) REFERENCES alf_audit_model(id) ON DELETE CASCADE
```

--  public | alf_auth_status             | table | alfresco

```shell
                                               Table "public.alf_auth_status"
Column   |          Type          | Collation | Nullable | Default | Storage  | Compression | Stats target | Description
------------+------------------------+-----------+----------+---------+----------+-------------+--------------+-------------
id         | bigint                 |           | not null |         | plain    |             |              |
username   | character varying(100) |           | not null |         | extended |             |              |
deleted    | boolean                |           | not null |         | plain    |             |              |
authorized | boolean                |           | not null |         | plain    |             |              |
checksum   | bytea                  |           | not null |         | extended |             |              |
authaction | character varying(10)  |           | not null |         | extended |             |              |
Indexes:
"alf_auth_status_pkey" PRIMARY KEY, btree (id)
"idx_alf_auth_action" btree (authaction)
"idx_alf_auth_deleted" btree (deleted)
"idx_alf_auth_usr_stat" UNIQUE, btree (username, authorized)
```

--  public | alf_authority               | table | alfresco

```shell
                                               Table "public.alf_authority"
Column   |          Type          | Collation | Nullable | Default | Storage  | Compression | Stats target | Description
-----------+------------------------+-----------+----------+---------+----------+-------------+--------------+-------------
id        | bigint                 |           | not null |         | plain    |             |              |
version   | bigint                 |           | not null |         | plain    |             |              |
authority | character varying(100) |           |          |         | extended |             |              |
crc       | bigint                 |           |          |         | plain    |             |              |
Indexes:
"alf_authority_pkey" PRIMARY KEY, btree (id)
"authority" UNIQUE, btree (authority, crc)
"idx_alf_auth_aut" btree (authority)
Referenced by:
TABLE "alf_access_control_entry" CONSTRAINT "fk_alf_ace_auth" FOREIGN KEY (authority_id) REFERENCES alf_authority(id)
TABLE "alf_authority_alias" CONSTRAINT "fk_alf_autha_ali" FOREIGN KEY (alias_id) REFERENCES alf_authority(id)
TABLE "alf_authority_alias" CONSTRAINT "fk_alf_autha_aut" FOREIGN KEY (auth_id) REFERENCES alf_authority(id)
```

--  public | alf_authority_alias         | table | alfresco

```shell
                                   Table "public.alf_authority_alias"
Column  |  Type  | Collation | Nullable | Default | Storage | Compression | Stats target | Description
----------+--------+-----------+----------+---------+---------+-------------+--------------+-------------
id       | bigint |           | not null |         | plain   |             |              |
version  | bigint |           | not null |         | plain   |             |              |
auth_id  | bigint |           | not null |         | plain   |             |              |
alias_id | bigint |           | not null |         | plain   |             |              |
Indexes:
"alf_authority_alias_pkey" PRIMARY KEY, btree (id)
"auth_id" UNIQUE, btree (auth_id, alias_id)
"fk_alf_autha_ali" btree (alias_id)
"fk_alf_autha_aut" btree (auth_id)
Foreign-key constraints:
"fk_alf_autha_ali" FOREIGN KEY (alias_id) REFERENCES alf_authority(id)
"fk_alf_autha_aut" FOREIGN KEY (auth_id) REFERENCES alf_authority(id)

```

--  public | alf_child_assoc             | table | alfresco

```shell
                                                   Table "public.alf_child_assoc"
       Column        |          Type          | Collation | Nullable | Default | Storage  | Compression | Stats target | Description
---------------------+------------------------+-----------+----------+---------+----------+-------------+--------------+-------------
id                  | bigint                 |           | not null |         | plain    |             |              |
version             | bigint                 |           | not null |         | plain    |             |              |
parent_node_id      | bigint                 |           | not null |         | plain    |             |              |
type_qname_id       | bigint                 |           | not null |         | plain    |             |              |
child_node_name_crc | bigint                 |           | not null |         | plain    |             |              |
child_node_name     | character varying(50)  |           | not null |         | extended |             |              |
child_node_id       | bigint                 |           | not null |         | plain    |             |              |
qname_ns_id         | bigint                 |           | not null |         | plain    |             |              |
qname_localname     | character varying(255) |           | not null |         | extended |             |              |
qname_crc           | bigint                 |           | not null |         | plain    |             |              |
is_primary          | boolean                |           |          |         | plain    |             |              |
assoc_index         | integer                |           |          |         | plain    |             |              |
Indexes:
"alf_child_assoc_pkey" PRIMARY KEY, btree (id)
"fk_alf_cass_cnode" btree (child_node_id)
"fk_alf_cass_qnns" btree (qname_ns_id)
"fk_alf_cass_tqn" btree (type_qname_id)
"idx_alf_cass_pnode" btree (parent_node_id, assoc_index, id)
"idx_alf_cass_pri" btree (parent_node_id, is_primary, child_node_id)
"idx_alf_cass_qncrc" btree (qname_crc, type_qname_id, parent_node_id)
"parent_node_id" UNIQUE, btree (parent_node_id, type_qname_id, child_node_name_crc, child_node_name)
Foreign-key constraints:
"fk_alf_cass_cnode" FOREIGN KEY (child_node_id) REFERENCES alf_node(id)
"fk_alf_cass_pnode" FOREIGN KEY (parent_node_id) REFERENCES alf_node(id)
"fk_alf_cass_qnns" FOREIGN KEY (qname_ns_id) REFERENCES alf_namespace(id)
"fk_alf_cass_tqn" FOREIGN KEY (type_qname_id) REFERENCES alf_qname(id)

```

--  public | alf_content_data            | table | alfresco

```shell
                                          Table "public.alf_content_data"
       Column        |  Type  | Collation | Nullable | Default | Storage | Compression | Stats target | Description
---------------------+--------+-----------+----------+---------+---------+-------------+--------------+-------------
id                  | bigint |           | not null |         | plain   |             |              |
version             | bigint |           | not null |         | plain   |             |              |
content_url_id      | bigint |           |          |         | plain   |             |              |
content_mimetype_id | bigint |           |          |         | plain   |             |              |
content_encoding_id | bigint |           |          |         | plain   |             |              |
content_locale_id   | bigint |           |          |         | plain   |             |              |
Indexes:
"alf_content_data_pkey" PRIMARY KEY, btree (id)
"fk_alf_cont_enc" btree (content_encoding_id)
"fk_alf_cont_loc" btree (content_locale_id)
"fk_alf_cont_mim" btree (content_mimetype_id)
"fk_alf_cont_url" btree (content_url_id)
Foreign-key constraints:
"fk_alf_cont_enc" FOREIGN KEY (content_encoding_id) REFERENCES alf_encoding(id)
"fk_alf_cont_loc" FOREIGN KEY (content_locale_id) REFERENCES alf_locale(id)
"fk_alf_cont_mim" FOREIGN KEY (content_mimetype_id) REFERENCES alf_mimetype(id)
"fk_alf_cont_url" FOREIGN KEY (content_url_id) REFERENCES alf_content_url(id)
Referenced by:
TABLE "alf_audit_model" CONSTRAINT "fk_alf_aud_mod_cd" FOREIGN KEY (content_data_id) REFERENCES alf_content_data(id)
```

--  public | alf_content_url             | table | alfresco

```shell
                                                  Table "public.alf_content_url"
      Column       |          Type          | Collation | Nullable | Default | Storage  | Compression | Stats target | Description
-------------------+------------------------+-----------+----------+---------+----------+-------------+--------------+-------------
id                | bigint                 |           | not null |         | plain    |             |              |
content_url       | character varying(255) |           | not null |         | extended |             |              |
content_url_short | character varying(12)  |           | not null |         | extended |             |              |
content_url_crc   | bigint                 |           | not null |         | plain    |             |              |
content_size      | bigint                 |           | not null |         | plain    |             |              |
orphan_time       | bigint                 |           |          |         | plain    |             |              |
Indexes:
"alf_content_url_pkey" PRIMARY KEY, btree (id)
"idx_alf_conturl_cr" UNIQUE, btree (content_url_short, content_url_crc)
"idx_alf_conturl_ot" btree (orphan_time)
"idx_alf_conturl_sz" btree (content_size, id)
Referenced by:
TABLE "alf_content_url_encryption" CONSTRAINT "fk_alf_cont_enc_url" FOREIGN KEY (content_url_id) REFERENCES alf_content_url(id) ON DELETE CASCADE
TABLE "alf_content_data" CONSTRAINT "fk_alf_cont_url" FOREIGN KEY (content_url_id) REFERENCES alf_content_url(id)
```

--  public | alf_content_url_encryption  | table | alfresco

```shell
                                              Table "public.alf_content_url_encryption"
        Column         |         Type          | Collation | Nullable | Default | Storage  | Compression | Stats target | Description
-----------------------+-----------------------+-----------+----------+---------+----------+-------------+--------------+-------------
id                    | bigint                |           | not null |         | plain    |             |              |
content_url_id        | bigint                |           | not null |         | plain    |             |              |
algorithm             | character varying(10) |           | not null |         | extended |             |              |
key_size              | integer               |           | not null |         | plain    |             |              |
encrypted_key         | bytea                 |           | not null |         | extended |             |              |
master_keystore_id    | character varying(20) |           | not null |         | extended |             |              |
master_key_alias      | character varying(15) |           | not null |         | extended |             |              |
unencrypted_file_size | bigint                |           |          |         | plain    |             |              |
Indexes:
"alf_content_url_encryption_pkey" PRIMARY KEY, btree (id)
"idx_alf_cont_enc_mka" btree (master_key_alias)
"idx_alf_cont_enc_url" UNIQUE, btree (content_url_id)
Foreign-key constraints:
"fk_alf_cont_enc_url" FOREIGN KEY (content_url_id) REFERENCES alf_content_url(id) ON DELETE CASCADE
```

--  public | alf_encoding                | table | alfresco

```shell
                                                 Table "public.alf_encoding"
    Column    |          Type          | Collation | Nullable | Default | Storage  | Compression | Stats target | Description
--------------+------------------------+-----------+----------+---------+----------+-------------+--------------+-------------
id           | bigint                 |           | not null |         | plain    |             |              |
version      | bigint                 |           | not null |         | plain    |             |              |
encoding_str | character varying(100) |           | not null |         | extended |             |              |
Indexes:
"alf_encoding_pkey" PRIMARY KEY, btree (id)
"alf_encoding_encoding_str_key" UNIQUE CONSTRAINT, btree (encoding_str)
Referenced by:
TABLE "alf_content_data" CONSTRAINT "fk_alf_cont_enc" FOREIGN KEY (content_encoding_id) REFERENCES alf_encoding(id)
```

--  public | alf_locale                  | table | alfresco

```shell

                                                 Table "public.alf_locale"
Column   |         Type          | Collation | Nullable | Default | Storage  | Compression | Stats target | Description
------------+-----------------------+-----------+----------+---------+----------+-------------+--------------+-------------
id         | bigint                |           | not null |         | plain    |             |              |
version    | bigint                |           | not null |         | plain    |             |              |
locale_str | character varying(20) |           | not null |         | extended |             |              |
Indexes:
"alf_locale_pkey" PRIMARY KEY, btree (id)
"locale_str" UNIQUE, btree (locale_str)
Referenced by:
TABLE "alf_content_data" CONSTRAINT "fk_alf_cont_loc" FOREIGN KEY (content_locale_id) REFERENCES alf_locale(id)
TABLE "alf_node" CONSTRAINT "fk_alf_node_loc" FOREIGN KEY (locale_id) REFERENCES alf_locale(id)
TABLE "alf_node_properties" CONSTRAINT "fk_alf_nprop_loc" FOREIGN KEY (locale_id) REFERENCES alf_locale(id)
```

--  public | alf_lock                    | table | alfresco

```shell
Table "public.alf_lock"
Column       |         Type          | Collation | Nullable | Default | Storage  | Compression | Stats target | Description
--------------------+-----------------------+-----------+----------+---------+----------+-------------+--------------+-------------
id                 | bigint                |           | not null |         | plain    |             |              |
version            | bigint                |           | not null |         | plain    |             |              |
shared_resource_id | bigint                |           | not null |         | plain    |             |              |
excl_resource_id   | bigint                |           | not null |         | plain    |             |              |
lock_token         | character varying(36) |           | not null |         | extended |             |              |
start_time         | bigint                |           | not null |         | plain    |             |              |
expiry_time        | bigint                |           | not null |         | plain    |             |              |
Indexes:
"alf_lock_pkey" PRIMARY KEY, btree (id)
"fk_alf_lock_excl" btree (excl_resource_id)
"idx_alf_lock_key" UNIQUE, btree (shared_resource_id, excl_resource_id)
Foreign-key constraints:
"fk_alf_lock_excl" FOREIGN KEY (excl_resource_id) REFERENCES alf_lock_resource(id)
"fk_alf_lock_shared" FOREIGN KEY (shared_resource_id) REFERENCES alf_lock_resource(id)
```

--  public | alf_lock_resource           | table | alfresco

```shell
Table "public.alf_lock_resource"
Column      |          Type          | Collation | Nullable | Default | Storage  | Compression | Stats target | Description
-----------------+------------------------+-----------+----------+---------+----------+-------------+--------------+-------------
id              | bigint                 |           | not null |         | plain    |             |              |
version         | bigint                 |           | not null |         | plain    |             |              |
qname_ns_id     | bigint                 |           | not null |         | plain    |             |              |
qname_localname | character varying(255) |           | not null |         | extended |             |              |
Indexes:
"alf_lock_resource_pkey" PRIMARY KEY, btree (id)
"idx_alf_lockr_key" UNIQUE, btree (qname_ns_id, qname_localname)
Foreign-key constraints:
"fk_alf_lockr_ns" FOREIGN KEY (qname_ns_id) REFERENCES alf_namespace(id)
Referenced by:
TABLE "alf_lock" CONSTRAINT "fk_alf_lock_excl" FOREIGN KEY (excl_resource_id) REFERENCES alf_lock_resource(id)
TABLE "alf_lock" CONSTRAINT "fk_alf_lock_shared" FOREIGN KEY (shared_resource_id) REFERENCES alf_lock_resource(id)
```

--  public | alf_mimetype                | table | alfresco

```shell
                                                 Table "public.alf_mimetype"
    Column    |          Type          | Collation | Nullable | Default | Storage  | Compression | Stats target | Description
--------------+------------------------+-----------+----------+---------+----------+-------------+--------------+-------------
id           | bigint                 |           | not null |         | plain    |             |              |
version      | bigint                 |           | not null |         | plain    |             |              |
mimetype_str | character varying(100) |           | not null |         | extended |             |              |
Indexes:
"alf_mimetype_pkey" PRIMARY KEY, btree (id)
"alf_mimetype_mimetype_str_key" UNIQUE CONSTRAINT, btree (mimetype_str)
Referenced by:
TABLE "alf_content_data" CONSTRAINT "fk_alf_cont_mim" FOREIGN KEY (content_mimetype_id) REFERENCES alf_mimetype(id)
```

--  public | alf_namespace               | table | alfresco

```shell
Table "public.alf_namespace"
Column  |          Type          | Collation | Nullable | Default | Storage  | Compression | Stats target | Description
---------+------------------------+-----------+----------+---------+----------+-------------+--------------+-------------
id      | bigint                 |           | not null |         | plain    |             |              |
version | bigint                 |           | not null |         | plain    |             |              |
uri     | character varying(100) |           | not null |         | extended |             |              |
Indexes:
"alf_namespace_pkey" PRIMARY KEY, btree (id)
"uri" UNIQUE, btree (uri)
Referenced by:
TABLE "alf_child_assoc" CONSTRAINT "fk_alf_cass_qnns" FOREIGN KEY (qname_ns_id) REFERENCES alf_namespace(id)
TABLE "alf_lock_resource" CONSTRAINT "fk_alf_lockr_ns" FOREIGN KEY (qname_ns_id) REFERENCES alf_namespace(id)
TABLE "alf_qname" CONSTRAINT "fk_alf_qname_ns" FOREIGN KEY (ns_id) REFERENCES alf_namespace(id)
Access method: heap
```

--  public | alf_node                    | table | alfresco

```shell
                                                    Table "public.alf_node"
     Column     |          Type          | Collation | Nullable | Default | Storage  | Compression | Stats target | Description
----------------+------------------------+-----------+----------+---------+----------+-------------+--------------+-------------
id             | bigint                 |           | not null |         | plain    |             |              |
version        | bigint                 |           | not null |         | plain    |             |              |
store_id       | bigint                 |           | not null |         | plain    |             |              |
uuid           | character varying(36)  |           | not null |         | extended |             |              |
transaction_id | bigint                 |           | not null |         | plain    |             |              |
type_qname_id  | bigint                 |           | not null |         | plain    |             |              |
locale_id      | bigint                 |           | not null |         | plain    |             |              |
acl_id         | bigint                 |           |          |         | plain    |             |              |
audit_creator  | character varying(255) |           |          |         | extended |             |              |
audit_created  | character varying(30)  |           |          |         | extended |             |              |
audit_modifier | character varying(255) |           |          |         | extended |             |              |
audit_modified | character varying(30)  |           |          |         | extended |             |              |
audit_accessed | character varying(30)  |           |          |         | extended |             |              |
Indexes:
"alf_node_pkey" PRIMARY KEY, btree (id)
"fk_alf_node_acl" btree (acl_id)
"fk_alf_node_loc" btree (locale_id)
"fk_alf_node_store" btree (store_id)
"idx_alf_node_cor" btree (audit_creator, store_id, type_qname_id, id)
"idx_alf_node_crd" btree (audit_created, store_id, type_qname_id, id)
"idx_alf_node_mdq" btree (store_id, type_qname_id, id)
"idx_alf_node_mod" btree (audit_modified, store_id, type_qname_id, id)
"idx_alf_node_mor" btree (audit_modifier, store_id, type_qname_id, id)
"idx_alf_node_tqn" btree (type_qname_id, store_id, id)
"idx_alf_node_txn" btree (transaction_id)
"idx_alf_node_txn_type" btree (transaction_id, type_qname_id)
"idx_alf_node_ver" btree (version)
"store_id" UNIQUE, btree (store_id, uuid)
Foreign-key constraints:
"fk_alf_node_acl" FOREIGN KEY (acl_id) REFERENCES alf_access_control_list(id)
"fk_alf_node_loc" FOREIGN KEY (locale_id) REFERENCES alf_locale(id)
"fk_alf_node_store" FOREIGN KEY (store_id) REFERENCES alf_store(id)
"fk_alf_node_tqn" FOREIGN KEY (type_qname_id) REFERENCES alf_qname(id)
"fk_alf_node_txn" FOREIGN KEY (transaction_id) REFERENCES alf_transaction(id)
Referenced by:
TABLE "alf_child_assoc" CONSTRAINT "fk_alf_cass_cnode" FOREIGN KEY (child_node_id) REFERENCES alf_node(id)
TABLE "alf_child_assoc" CONSTRAINT "fk_alf_cass_pnode" FOREIGN KEY (parent_node_id) REFERENCES alf_node(id)
TABLE "alf_node_aspects" CONSTRAINT "fk_alf_nasp_n" FOREIGN KEY (node_id) REFERENCES alf_node(id)
TABLE "alf_node_assoc" CONSTRAINT "fk_alf_nass_snode" FOREIGN KEY (source_node_id) REFERENCES alf_node(id)
TABLE "alf_node_assoc" CONSTRAINT "fk_alf_nass_tnode" FOREIGN KEY (target_node_id) REFERENCES alf_node(id)
TABLE "alf_node_properties" CONSTRAINT "fk_alf_nprop_n" FOREIGN KEY (node_id) REFERENCES alf_node(id)
TABLE "alf_store" CONSTRAINT "fk_alf_store_root" FOREIGN KEY (root_node_id) REFERENCES alf_node(id)
TABLE "alf_subscriptions" CONSTRAINT "fk_alf_sub_node" FOREIGN KEY (node_id) REFERENCES alf_node(id) ON DELETE CASCADE
TABLE "alf_subscriptions" CONSTRAINT "fk_alf_sub_user" FOREIGN KEY (user_node_id) REFERENCES alf_node(id) ON DELETE CASCADE
TABLE "alf_usage_delta" CONSTRAINT "fk_alf_usaged_n" FOREIGN KEY (node_id) REFERENCES alf_node(id)
```

--  public | alf_node_aspects            | table | alfresco

```shell
                                     Table "public.alf_node_aspects"
Column  |  Type  | Collation | Nullable | Default | Storage | Compression | Stats target | Description
----------+--------+-----------+----------+---------+---------+-------------+--------------+-------------
node_id  | bigint |           | not null |         | plain   |             |              |
qname_id | bigint |           | not null |         | plain   |             |              |
Indexes:
"alf_node_aspects_pkey" PRIMARY KEY, btree (node_id, qname_id)
"fk_alf_nasp_n" btree (node_id)
"fk_alf_nasp_qn" btree (qname_id)
Foreign-key constraints:
"fk_alf_nasp_n" FOREIGN KEY (node_id) REFERENCES alf_node(id)
"fk_alf_nasp_qn" FOREIGN KEY (qname_id) REFERENCES alf_qname(id)
```

--  public | alf_node_assoc              | table | alfresco

```shell
                                         Table "public.alf_node_assoc"
     Column     |  Type  | Collation | Nullable | Default | Storage | Compression | Stats target | Description
----------------+--------+-----------+----------+---------+---------+-------------+--------------+-------------
id             | bigint |           | not null |         | plain   |             |              |
version        | bigint |           | not null |         | plain   |             |              |
source_node_id | bigint |           | not null |         | plain   |             |              |
target_node_id | bigint |           | not null |         | plain   |             |              |
type_qname_id  | bigint |           | not null |         | plain   |             |              |
assoc_index    | bigint |           | not null |         | plain   |             |              |
Indexes:
"alf_node_assoc_pkey" PRIMARY KEY, btree (id)
"fk_alf_nass_snode" btree (source_node_id, type_qname_id, assoc_index)
"fk_alf_nass_tnode" btree (target_node_id, type_qname_id)
"fk_alf_nass_tqn" btree (type_qname_id)
"source_node_id" UNIQUE, btree (source_node_id, target_node_id, type_qname_id)
Foreign-key constraints:
"fk_alf_nass_snode" FOREIGN KEY (source_node_id) REFERENCES alf_node(id)
"fk_alf_nass_tnode" FOREIGN KEY (target_node_id) REFERENCES alf_node(id)
"fk_alf_nass_tqn" FOREIGN KEY (type_qname_id) REFERENCES alf_qname(id)
```

--  public | alf_node_properties         | table | alfresco

```shell
                                                 Table "public.alf_node_properties"
       Column       |          Type           | Collation | Nullable | Default | Storage  | Compression | Stats target | Description
--------------------+-------------------------+-----------+----------+---------+----------+-------------+--------------+-------------
node_id            | bigint                  |           | not null |         | plain    |             |              |
actual_type_n      | integer                 |           | not null |         | plain    |             |              |
persisted_type_n   | integer                 |           | not null |         | plain    |             |              |
boolean_value      | boolean                 |           |          |         | plain    |             |              |
long_value         | bigint                  |           |          |         | plain    |             |              |
float_value        | real                    |           |          |         | plain    |             |              |
double_value       | double precision        |           |          |         | plain    |             |              |
string_value       | character varying(1024) |           |          |         | extended |             |              |
serializable_value | bytea                   |           |          |         | extended |             |              |
qname_id           | bigint                  |           | not null |         | plain    |             |              |
list_index         | integer                 |           | not null |         | plain    |             |              |
locale_id          | bigint                  |           | not null |         | plain    |             |              |
Indexes:
"alf_node_properties_pkey" PRIMARY KEY, btree (node_id, qname_id, list_index, locale_id)
"fk_alf_nprop_loc" btree (locale_id)
"fk_alf_nprop_n" btree (node_id)
"fk_alf_nprop_qn" btree (qname_id)
"idx_alf_nprop_b" btree (qname_id, boolean_value, node_id)
"idx_alf_nprop_d" btree (qname_id, double_value, node_id)
"idx_alf_nprop_f" btree (qname_id, float_value, node_id)
"idx_alf_nprop_l" btree (qname_id, long_value, node_id)
"idx_alf_nprop_s" btree (qname_id, string_value, node_id)
Foreign-key constraints:
"fk_alf_nprop_loc" FOREIGN KEY (locale_id) REFERENCES alf_locale(id)
"fk_alf_nprop_n" FOREIGN KEY (node_id) REFERENCES alf_node(id)
"fk_alf_nprop_qn" FOREIGN KEY (qname_id) REFERENCES alf_qname(id)
```

--  public | alf_permission              | table | alfresco

```shell
Table "public.alf_permission"
Column     |          Type          | Collation | Nullable | Default | Storage  | Compression | Stats target | Description
---------------+------------------------+-----------+----------+---------+----------+-------------+--------------+-------------
id            | bigint                 |           | not null |         | plain    |             |              |
version       | bigint                 |           | not null |         | plain    |             |              |
type_qname_id | bigint                 |           | not null |         | plain    |             |              |
name          | character varying(100) |           | not null |         | extended |             |              |
Indexes:
"alf_permission_pkey" PRIMARY KEY, btree (id)
"fk_alf_perm_tqn" btree (type_qname_id)
"type_qname_id" UNIQUE, btree (type_qname_id, name)
Foreign-key constraints:
"fk_alf_perm_tqn" FOREIGN KEY (type_qname_id) REFERENCES alf_qname(id)
Referenced by:
TABLE "alf_access_control_entry" CONSTRAINT "fk_alf_ace_perm" FOREIGN KEY (permission_id) REFERENCES alf_permission(id)
```

--  public | alf_prop_class              | table | alfresco
```shell
Table "public.alf_prop_class"
Column         |          Type          | Collation | Nullable | Default | Storage  | Compression | Stats target | Description
-----------------------+------------------------+-----------+----------+---------+----------+-------------+--------------+-------------
id                    | bigint                 |           | not null |         | plain    |             |              |
java_class_name       | character varying(255) |           | not null |         | extended |             |              |
java_class_name_short | character varying(32)  |           | not null |         | extended |             |              |
java_class_name_crc   | bigint                 |           | not null |         | plain    |             |              |
Indexes:
"alf_prop_class_pkey" PRIMARY KEY, btree (id)
"idx_alf_propc_clas" btree (java_class_name)
"idx_alf_propc_crc" UNIQUE, btree (java_class_name_crc, java_class_name_short)
```

--  public | alf_prop_date_value         | table | alfresco
```shell
Table "public.alf_prop_date_value"
Column      |   Type   | Collation | Nullable | Default | Storage | Compression | Stats target | Description
-----------------+----------+-----------+----------+---------+---------+-------------+--------------+-------------
date_value      | bigint   |           | not null |         | plain   |             |              |
full_year       | integer  |           | not null |         | plain   |             |              |
half_of_year    | smallint |           | not null |         | plain   |             |              |
quarter_of_year | smallint |           | not null |         | plain   |             |              |
month_of_year   | smallint |           | not null |         | plain   |             |              |
week_of_year    | smallint |           | not null |         | plain   |             |              |
week_of_month   | smallint |           | not null |         | plain   |             |              |
day_of_year     | integer  |           | not null |         | plain   |             |              |
day_of_month    | smallint |           | not null |         | plain   |             |              |
day_of_week     | smallint |           | not null |         | plain   |             |              |
Indexes:
"alf_prop_date_value_pkey" PRIMARY KEY, btree (date_value)
"idx_alf_propdt_dt" btree (full_year, month_of_year, day_of_month)
```

--  public | alf_prop_double_value       | table | alfresco

```shell
                                         Table "public.alf_prop_double_value"
    Column    |       Type       | Collation | Nullable | Default | Storage | Compression | Stats target | Description
--------------+------------------+-----------+----------+---------+---------+-------------+--------------+-------------
id           | bigint           |           | not null |         | plain   |             |              |
double_value | double precision |           | not null |         | plain   |             |              |
Indexes:
"alf_prop_double_value_pkey" PRIMARY KEY, btree (id)
"idx_alf_propd_val" UNIQUE, btree (double_value)
```

--  public | alf_prop_link               | table | alfresco
```shell
                                         Table "public.alf_prop_link"
    Column     |  Type  | Collation | Nullable | Default | Storage | Compression | Stats target | Description
---------------+--------+-----------+----------+---------+---------+-------------+--------------+-------------
root_prop_id  | bigint |           | not null |         | plain   |             |              |
prop_index    | bigint |           | not null |         | plain   |             |              |
contained_in  | bigint |           | not null |         | plain   |             |              |
key_prop_id   | bigint |           | not null |         | plain   |             |              |
value_prop_id | bigint |           | not null |         | plain   |             |              |
Indexes:
"alf_prop_link_pkey" PRIMARY KEY, btree (root_prop_id, contained_in, prop_index)
"fk_alf_propln_key" btree (key_prop_id)
"fk_alf_propln_val" btree (value_prop_id)
"idx_alf_propln_for" btree (root_prop_id, key_prop_id, value_prop_id)
Foreign-key constraints:
"fk_alf_propln_key" FOREIGN KEY (key_prop_id) REFERENCES alf_prop_value(id) ON DELETE CASCADE
"fk_alf_propln_root" FOREIGN KEY (root_prop_id) REFERENCES alf_prop_root(id) ON DELETE CASCADE
"fk_alf_propln_val" FOREIGN KEY (value_prop_id) REFERENCES alf_prop_value(id) ON DELETE CASCADE
```

--  public | alf_prop_root               | table | alfresco
```shell
                                      Table "public.alf_prop_root"
Column  |  Type   | Collation | Nullable | Default | Storage | Compression | Stats target | Description
---------+---------+-----------+----------+---------+---------+-------------+--------------+-------------
id      | bigint  |           | not null |         | plain   |             |              |
version | integer |           | not null |         | plain   |             |              |
Indexes:
"alf_prop_root_pkey" PRIMARY KEY, btree (id)
Referenced by:
TABLE "alf_audit_app" CONSTRAINT "fk_alf_aud_app_dis" FOREIGN KEY (disabled_paths_id) REFERENCES alf_prop_root(id)
TABLE "alf_audit_entry" CONSTRAINT "fk_alf_aud_ent_pro" FOREIGN KEY (audit_values_id) REFERENCES alf_prop_root(id)
TABLE "alf_prop_link" CONSTRAINT "fk_alf_propln_root" FOREIGN KEY (root_prop_id) REFERENCES alf_prop_root(id) ON DELETE CASCADE
TABLE "alf_prop_unique_ctx" CONSTRAINT "fk_alf_propuctx_p1" FOREIGN KEY (prop1_id) REFERENCES alf_prop_root(id)
```

--  public | alf_prop_serializable_value | table | alfresco

```shell
                                     Table "public.alf_prop_serializable_value"
       Column       |  Type  | Collation | Nullable | Default | Storage  | Compression | Stats target | Description
--------------------+--------+-----------+----------+---------+----------+-------------+--------------+-------------
id                 | bigint |           | not null |         | plain    |             |              |
serializable_value | bytea  |           | not null |         | extended |             |              |
Indexes:
"alf_prop_serializable_value_pkey" PRIMARY KEY, btree (id)

```

--  public | alf_prop_string_value       | table | alfresco

```shell
                                              Table "public.alf_prop_string_value"
      Column      |          Type           | Collation | Nullable | Default | Storage  | Compression | Stats target | Description
------------------+-------------------------+-----------+----------+---------+----------+-------------+--------------+-------------
id               | bigint                  |           | not null |         | plain    |             |              |
string_value     | character varying(1024) |           | not null |         | extended |             |              |
string_end_lower | character varying(16)   |           | not null |         | extended |             |              |
string_crc       | bigint                  |           | not null |         | plain    |             |              |
Indexes:
"alf_prop_string_value_pkey" PRIMARY KEY, btree (id)
"idx_alf_props_crc" UNIQUE, btree (string_end_lower, string_crc)
"idx_alf_props_str" btree (string_value)
```

--  public | alf_prop_unique_ctx         | table | alfresco
```shell
                                       Table "public.alf_prop_unique_ctx"
     Column     |  Type   | Collation | Nullable | Default | Storage | Compression | Stats target | Description
----------------+---------+-----------+----------+---------+---------+-------------+--------------+-------------
id             | bigint  |           | not null |         | plain   |             |              |
version        | integer |           | not null |         | plain   |             |              |
value1_prop_id | bigint  |           | not null |         | plain   |             |              |
value2_prop_id | bigint  |           | not null |         | plain   |             |              |
value3_prop_id | bigint  |           | not null |         | plain   |             |              |
prop1_id       | bigint  |           |          |         | plain   |             |              |
Indexes:
"alf_prop_unique_ctx_pkey" PRIMARY KEY, btree (id)
"fk_alf_propuctx_p1" btree (prop1_id)
"fk_alf_propuctx_v2" btree (value2_prop_id)
"fk_alf_propuctx_v3" btree (value3_prop_id)
"idx_alf_propuctx" UNIQUE, btree (value1_prop_id, value2_prop_id, value3_prop_id)
Foreign-key constraints:
"fk_alf_propuctx_p1" FOREIGN KEY (prop1_id) REFERENCES alf_prop_root(id)
"fk_alf_propuctx_v1" FOREIGN KEY (value1_prop_id) REFERENCES alf_prop_value(id) ON DELETE CASCADE
"fk_alf_propuctx_v2" FOREIGN KEY (value2_prop_id) REFERENCES alf_prop_value(id) ON DELETE CASCADE
"fk_alf_propuctx_v3" FOREIGN KEY (value3_prop_id) REFERENCES alf_prop_value(id) ON DELETE CASCADE
```

--  public | alf_prop_value              | table | alfresco
```shell
                                          Table "public.alf_prop_value"
     Column     |   Type   | Collation | Nullable | Default | Storage | Compression | Stats target | Description
----------------+----------+-----------+----------+---------+---------+-------------+--------------+-------------
id             | bigint   |           | not null |         | plain   |             |              |
actual_type_id | bigint   |           | not null |         | plain   |             |              |
persisted_type | smallint |           | not null |         | plain   |             |              |
long_value     | bigint   |           | not null |         | plain   |             |              |
Indexes:
"alf_prop_value_pkey" PRIMARY KEY, btree (id)
"idx_alf_propv_act" UNIQUE, btree (actual_type_id, long_value)
"idx_alf_propv_per" btree (persisted_type, long_value)
Referenced by:
TABLE "alf_audit_app" CONSTRAINT "fk_alf_aud_app_an" FOREIGN KEY (app_name_id) REFERENCES alf_prop_value(id)
TABLE "alf_audit_entry" CONSTRAINT "fk_alf_aud_ent_use" FOREIGN KEY (audit_user_id) REFERENCES alf_prop_value(id)
TABLE "alf_prop_link" CONSTRAINT "fk_alf_propln_key" FOREIGN KEY (key_prop_id) REFERENCES alf_prop_value(id) ON DELETE CASCADE
TABLE "alf_prop_link" CONSTRAINT "fk_alf_propln_val" FOREIGN KEY (value_prop_id) REFERENCES alf_prop_value(id) ON DELETE CASCADE
TABLE "alf_prop_unique_ctx" CONSTRAINT "fk_alf_propuctx_v1" FOREIGN KEY (value1_prop_id) REFERENCES alf_prop_value(id) ON DELETE CASCADE
TABLE "alf_prop_unique_ctx" CONSTRAINT "fk_alf_propuctx_v2" FOREIGN KEY (value2_prop_id) REFERENCES alf_prop_value(id) ON DELETE CASCADE
TABLE "alf_prop_unique_ctx" CONSTRAINT "fk_alf_propuctx_v3" FOREIGN KEY (value3_prop_id) REFERENCES alf_prop_value(id) ON DELETE CASCADE
```

--  public | alf_qname                   | table | alfresco
```shell
Table "public.alf_qname"
Column   |          Type          | Collation | Nullable | Default | Storage  | Compression | Stats target | Description
------------+------------------------+-----------+----------+---------+----------+-------------+--------------+-------------
id         | bigint                 |           | not null |         | plain    |             |              |
version    | bigint                 |           | not null |         | plain    |             |              |
ns_id      | bigint                 |           | not null |         | plain    |             |              |
local_name | character varying(200) |           | not null |         | extended |             |              |
Indexes:
"alf_qname_pkey" PRIMARY KEY, btree (id)
"ns_id" UNIQUE, btree (ns_id, local_name)
Foreign-key constraints:
"fk_alf_qname_ns" FOREIGN KEY (ns_id) REFERENCES alf_namespace(id)
Referenced by:
TABLE "alf_child_assoc" CONSTRAINT "fk_alf_cass_tqn" FOREIGN KEY (type_qname_id) REFERENCES alf_qname(id)
TABLE "alf_node_aspects" CONSTRAINT "fk_alf_nasp_qn" FOREIGN KEY (qname_id) REFERENCES alf_qname(id)
TABLE "alf_node_assoc" CONSTRAINT "fk_alf_nass_tqn" FOREIGN KEY (type_qname_id) REFERENCES alf_qname(id)
TABLE "alf_node" CONSTRAINT "fk_alf_node_tqn" FOREIGN KEY (type_qname_id) REFERENCES alf_qname(id)
TABLE "alf_node_properties" CONSTRAINT "fk_alf_nprop_qn" FOREIGN KEY (qname_id) REFERENCES alf_qname(id)
TABLE "alf_permission" CONSTRAINT "fk_alf_perm_tqn" FOREIGN KEY (type_qname_id) REFERENCES alf_qname(id)
```

--  public | alf_store                   | table | alfresco
```shell
                                                   Table "public.alf_store"
    Column    |          Type          | Collation | Nullable | Default | Storage  | Compression | Stats target | Description
--------------+------------------------+-----------+----------+---------+----------+-------------+--------------+-------------
id           | bigint                 |           | not null |         | plain    |             |              |
version      | bigint                 |           | not null |         | plain    |             |              |
protocol     | character varying(50)  |           | not null |         | extended |             |              |
identifier   | character varying(100) |           | not null |         | extended |             |              |
root_node_id | bigint                 |           |          |         | plain    |             |              |
Indexes:
"alf_store_pkey" PRIMARY KEY, btree (id)
"fk_alf_store_root" btree (root_node_id)
"protocol" UNIQUE, btree (protocol, identifier)
Foreign-key constraints:
"fk_alf_store_root" FOREIGN KEY (root_node_id) REFERENCES alf_node(id)
Referenced by:
TABLE "alf_node" CONSTRAINT "fk_alf_node_store" FOREIGN KEY (store_id) REFERENCES alf_store(id)
```
--  public | alf_subscriptions           | table | alfresco

```shell

                                      Table "public.alf_subscriptions"
    Column    |  Type  | Collation | Nullable | Default | Storage | Compression | Stats target | Description
--------------+--------+-----------+----------+---------+---------+-------------+--------------+-------------
user_node_id | bigint |           | not null |         | plain   |             |              |
node_id      | bigint |           | not null |         | plain   |             |              |
Indexes:
"alf_subscriptions_pkey" PRIMARY KEY, btree (user_node_id, node_id)
"fk_alf_sub_node" btree (node_id)
Foreign-key constraints:
"fk_alf_sub_node" FOREIGN KEY (node_id) REFERENCES alf_node(id) ON DELETE CASCADE
"fk_alf_sub_user" FOREIGN KEY (user_node_id) REFERENCES alf_node(id) ON DELETE CASCADE
```

--  public | alf_tenant                  | table | alfresco
```shell
                                                   Table "public.alf_tenant"
    Column     |          Type          | Collation | Nullable | Default | Storage  | Compression | Stats target | Description
---------------+------------------------+-----------+----------+---------+----------+-------------+--------------+-------------
tenant_domain | character varying(75)  |           | not null |         | extended |             |              |
version       | bigint                 |           | not null |         | plain    |             |              |
enabled       | boolean                |           | not null |         | plain    |             |              |
tenant_name   | character varying(75)  |           |          |         | extended |             |              |
content_root  | character varying(255) |           |          |         | extended |             |              |
db_url        | character varying(255) |           |          |         | extended |             |              |
Indexes:
"alf_tenant_pkey" PRIMARY KEY, btree (tenant_domain)
```

--  public | alf_transaction             | table | alfresco

```shell
                                                Table "public.alf_transaction"
     Column     |         Type          | Collation | Nullable | Default | Storage  | Compression | Stats target | Description
----------------+-----------------------+-----------+----------+---------+----------+-------------+--------------+-------------
id             | bigint                |           | not null |         | plain    |             |              |
version        | bigint                |           | not null |         | plain    |             |              |
change_txn_id  | character varying(56) |           | not null |         | extended |             |              |
commit_time_ms | bigint                |           |          |         | plain    |             |              |
Indexes:
"alf_transaction_pkey" PRIMARY KEY, btree (id)
"idx_alf_txn_ctms" btree (commit_time_ms, id)
"idx_alf_txn_ctms_sc" btree (commit_time_ms)
"idx_alf_txn_id_ctms" btree (id, commit_time_ms)
Referenced by:
TABLE "alf_node" CONSTRAINT "fk_alf_node_txn" FOREIGN KEY (transaction_id) REFERENCES alf_transaction(id)
```

--  public | alf_usage_delta
```shell
                                      Table "public.alf_usage_delta"
Column   |  Type  | Collation | Nullable | Default | Storage | Compression | Stats target | Description
------------+--------+-----------+----------+---------+---------+-------------+--------------+-------------
id         | bigint |           | not null |         | plain   |             |              |
version    | bigint |           | not null |         | plain   |             |              |
node_id    | bigint |           | not null |         | plain   |             |              |
delta_size | bigint |           | not null |         | plain   |             |              |
Indexes:
"alf_usage_delta_pkey" PRIMARY KEY, btree (id)
"fk_alf_usaged_n" btree (node_id)
Foreign-key constraints:
"fk_alf_usaged_n" FOREIGN KEY (node_id) REFERENCES alf_node(id)
```