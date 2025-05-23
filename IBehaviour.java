public interface IBehaviour {
    void onInit();
    void onEnabled();
    void onDisabled();
    void onDestroy();
    void onUpdate();
    void onCollision(GameObject other);
}
