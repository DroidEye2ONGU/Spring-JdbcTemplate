package JDBC;

public class Manager {
    private int m_id;
    private String m_managerName;
    private String m_managerPassword;

    public Manager() {
    }

    public Manager(String m_managerName, String m_managerPassword) {
        this.m_managerName = m_managerName;
        this.m_managerPassword = m_managerPassword;
    }

    public int getM_id() {
        return m_id;
    }

    public void setM_id(int m_id) {
        this.m_id = m_id;
    }

    public String getM_managerName() {
        return m_managerName;
    }

    public void setM_managerName(String m_managerName) {
        this.m_managerName = m_managerName;
    }

    public String getM_managerPassword() {
        return m_managerPassword;
    }

    public void setM_managerPassword(String m_managerPassword) {
        this.m_managerPassword = m_managerPassword;
    }

    @Override
    public String toString() {
        return "Manager{" +
                "m_id=" + m_id +
                ", m_managerName='" + m_managerName + '\'' +
                ", m_managerPassword='" + m_managerPassword + '\'' +
                '}';
    }
}
