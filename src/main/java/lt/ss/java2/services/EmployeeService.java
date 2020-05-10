package lt.ss.java2.services;

import lt.ss.java2.model.Employee;
import lt.ss.java2.model.Salary;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class EmployeeService {

    /**
     * Grazinti employee puslapi
     *
     * @param pageNo   puslapio numeris (numeruojame nuo 0)
     * @param pageSize puslapio dydis
     * @return
     */
    public static List<Employee> loadEmployees(int pageNo, int pageSize) {
        try (
                Connection conn = DBService.getConnectionFromCP();  // 1 zingsnis
//              Galimas ir toks priskirimas su String.format
//               PreparedStatement statement = conn.prepareStatement(
//               String.format("SELECT * FROM employees LIMIT %s OFFSET %s", pageSize, pageSize * pageNo )
                PreparedStatement statement = conn.prepareStatement("SELECT * FROM employees LIMIT ? OFFSET ?");
        ) {
            statement.setInt(1, pageSize);
            statement.setInt(2, pageSize * pageNo);
            try (ResultSet resultSet = statement.executeQuery()) { // 2 zingsnis
                List<Employee> employees = new ArrayList<>();
                while (resultSet.next()) {
                    employees.add(EmployeeMap.fromResultSet(resultSet));   // 3 zingsnis
                }
                for (Employee epm : employees) {
                    epm.setSalaries(getEmployeeSalary(conn, epm.getEmpNo()));
                }

                return employees;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // Patare geriau susigrazinti tuscia lista nei Null
        return Collections.emptyList();

        // SELECT * FROM employees  LIMIT 5 OFFSET 10
        // SELECT * FROM employees  LIMIT 10,5

        // SELECT * FROM employees  LIMIT ? OFFSET ?
        // 1? <= 10
        // 2? <= 5


    }

    private static List<Salary> getEmployeeSalary(Connection conn, Integer empNo) {
    // pavyko perduoti prisijungima...
        try (
                PreparedStatement statement = conn.prepareStatement(
                        "SELECT * FROM salaries WHERE emp_no = ?");
        ) {
            statement.setInt(1, empNo.intValue());
            try (ResultSet resultSet = statement.executeQuery()) { // 2 zingsnis
                List<Salary> salaries = new ArrayList<>();
                while (resultSet.next()) {
                    salaries.add(SalaryMap.fromResultSet(resultSet));   // 3 zingsnis
                }
                return salaries;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return Collections.emptyList();
    }
}
