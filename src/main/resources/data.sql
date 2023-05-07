CREATE OR REPLACE FUNCTION user_insert_function() RETURNS TRIGGER AS '
BEGIN
    INSERT INTO user_monitoring(app_user_id, created_on) VALUES (NEW.id_user, NOW());
    RETURN NEW;
END;
' LANGUAGE plpgsql;

CREATE TRIGGER user_insert_trigger AFTER INSERT ON app_user
    FOR EACH ROW
    EXECUTE FUNCTION user_insert_function();



CREATE OR REPLACE FUNCTION user_delete_function() RETURNS TRIGGER AS '
    BEGIN
        DELETE FROM user_monitoring WHERE app_user_id = OLD.id_user;
        RETURN OLD;
    END;
' LANGUAGE plpgsql;

CREATE TRIGGER user_delete_trigger BEFORE DELETE ON app_user
    FOR EACH ROW
EXECUTE FUNCTION user_delete_function();

