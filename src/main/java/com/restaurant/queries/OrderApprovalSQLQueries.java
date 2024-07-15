package com.restaurant.queries;

/**
 * Utility class containing SQL queries for the OrderApproval entity.
 */
public class OrderApprovalSQLQueries {	
	/**
     * SQL query to insert a new order approval into the database.
     */
	public static final String INSERT_APPROVAL = "INSERT INTO order_approvals (order_detail_id) "
			+ "VALUES (?)";
	
	/**
     * SQL query to retrieve an order approval by its ID from the database.
     */
    public static final String GET_APPROVAL_BY_ID = "SELECT oa.id, oa.order_detail_id, "
    		+ "od.total_amount, "
    		+ "os.id as status_id, os.status_name "
            + "FROM order_approvals oa "
            + "INNER JOIN order_details od ON oa.order_detail_id = od.id "
            + "INNER JOIN order_status os ON od.order_status_id = os.id "
            + "WHERE oa.id = ?";
    
    /**
     * SQL query to retrieve all order approvals from the database.
     */
    public static final String GET_ALL_APPROVALS = "SELECT oa.id, oa.order_detail_id, "
    		+ "od.total_amount, "
    		+ "os.id as status_id, os.status_name "
            + "FROM order_approvals oa "
            + "INNER JOIN order_details od ON oa.order_detail_id = od.id "
            + "INNER JOIN order_status os ON od.order_status_id = os.id";
    
    /**
     * SQL query to update an existing order approval in the database.
     */
    public static final String UPDATE_APPROVAL = "UPDATE order_approvals SET order_detail_id = ? "
    		+ "WHERE id = ?";
    
    /**
     * SQL query to delete an order approval by its ID from the database.
     */
    public static final String DELETE_APPROVAL = "DELETE FROM order_approvals "
    		+ "WHERE id = ?";
    
    private OrderApprovalSQLQueries() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
