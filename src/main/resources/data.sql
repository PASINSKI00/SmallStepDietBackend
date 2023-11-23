
CREATE OR REPLACE FUNCTION user_delete_function() RETURNS TRIGGER AS '
    BEGIN
        DELETE FROM user_monitoring WHERE app_user_id = OLD.id_user;
        RETURN OLD;
    END;
' LANGUAGE plpgsql;

CREATE TRIGGER user_delete_trigger BEFORE DELETE ON app_user
    FOR EACH ROW
EXECUTE FUNCTION user_delete_function();

