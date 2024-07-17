package com.finalproject.storemanagementproject.services;

import com.finalproject.storemanagementproject.models.AnalyticsReport;
import com.finalproject.storemanagementproject.models.Order;
import com.finalproject.storemanagementproject.models.Payment;
import com.finalproject.storemanagementproject.models.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class AnalyticsService {
    @Autowired
    private PaymentService paymentService;

    @Autowired
    private OrderService orderService;

    public AnalyticsReport getReportByTimeLine(String timeline, Instant startDate, Instant endDate) {
        Instant date;
        List<Payment> paymentsAtTime;
        List<Order> orders;
        int totalOrders;
        int totalProducts;

        Instant now = Instant.now(Clock.offset(Clock.systemUTC(), Duration.ofHours(+7))).truncatedTo(ChronoUnit.DAYS);

        switch (timeline.toLowerCase()) {
            case "yesterday":
                Instant yesterdayStart = now.minus(1, ChronoUnit.DAYS);
                Instant yesterdayEnd = now;

                paymentsAtTime = paymentService.getPaymentByBetweenDate(yesterdayStart, yesterdayEnd);
                orders = orderService.getOrdersByTimeAndStatus(yesterdayStart, yesterdayEnd, null);
                break;
            case "last7days":
                Instant sevenDaysAgo = now.minus(6, ChronoUnit.DAYS);

                paymentsAtTime = paymentService.getPaymentByBetweenDate(sevenDaysAgo, now.plus(1, ChronoUnit.DAYS));
                orders = orderService.getOrdersByTimeAndStatus(sevenDaysAgo, now.plus(1, ChronoUnit.DAYS), null);
                break;
            case "thismonth":
                Instant startOfMonth = now.atZone(ZoneOffset.UTC).withDayOfMonth(1).toInstant();

                paymentsAtTime = paymentService.getPaymentsInCurrentMonth(startOfMonth.atZone(ZoneOffset.UTC).toLocalDate());
                orders = orderService.getOrdersByTimeAndStatus(startOfMonth, now.plus(1, ChronoUnit.DAYS), null);
                break;
            case "custom":
                if (startDate == null || endDate == null) {
                    return null;
                }

                startDate.truncatedTo(ChronoUnit.DAYS);
                endDate.truncatedTo(ChronoUnit.DAYS).plus(1, ChronoUnit.DAYS);

                paymentsAtTime = paymentService.getPaymentByBetweenDate(startDate, endDate);
                orders = orderService.getOrdersByTimeAndStatus(startDate, endDate, null);
                break;
            default:
                paymentsAtTime = paymentService.getPaymentByBetweenDate(now, now.plus(1, ChronoUnit.DAYS));
                orders = orderService.getOrdersByTimeAndStatus(now, now.plus(1, ChronoUnit.DAYS), null);
                break;
        }

        totalOrders = (orders != null) ? orders.size() : 0;
        totalProducts = calculateTotalProducts(orders);

        AnalyticsReport report = new AnalyticsReport();

        double totalAmountReceived = calculateTotalAmount(paymentsAtTime, Status.COMPLETED);

        report.setTotalAmountReceived(totalAmountReceived);
        report.setNumberOfOrders(totalOrders);
        report.setNumberOfProducts(totalProducts);
        report.setOrders(orders);

        return report;
    }

    private int calculateTotalProducts(List<Order> orders) {
        return (orders != null)
                ? orders.stream().filter(order -> order.getOrderProducts() != null)
                .mapToInt(order -> order.getOrderProducts().size()).sum()
                : 0;
    }

    private double calculateTotalAmount(List<Payment> payments, Status targetStatus) {
        final Status finalTargetStatus = (targetStatus != null) ? targetStatus : Status.COMPLETED;

        return payments.stream().filter(p -> p.getStatus() == finalTargetStatus).mapToDouble(Payment::getAmount).sum();
    }

    public double calculateProfitForPeriod(String period) {
        Instant startDate;
        Instant endDate = Instant.now();

        switch (period.toLowerCase()) {
            case "yesterday":
                startDate = endDate.minus(1, ChronoUnit.DAYS);
                break;
            case "7days":
                startDate = endDate.minus(7, ChronoUnit.DAYS);
                break;
            case "thismonth":
                startDate = LocalDate.now().withDayOfMonth(1).atStartOfDay().toInstant(ZoneOffset.UTC);
                break;
            default:
                String[] dateRange = period.split("-");
                startDate = LocalDate.parse(dateRange[0], DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                        .atStartOfDay()
                        .toInstant(ZoneOffset.UTC);
                endDate = LocalDate.parse(dateRange[1], DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                        .plusDays(1)
                        .atStartOfDay()
                        .toInstant(ZoneOffset.UTC);
                break;
        }

        return calculateProfit(startDate, endDate);
    }

    public double calculateProfit(Instant startDate, Instant endDate) {
        List<Payment> completedPayments = paymentService.getPaymentByStatusAtDate(Status.COMPLETED, startDate, endDate);
        double totalRevenue = completedPayments.stream().mapToDouble(Payment::getAmount).sum();

        List<Order> orders = orderService.getOrdersByTimeAndStatus(startDate, endDate, Status.COMPLETED);

        double totalCost = orders.stream()
                .flatMap(order -> order.getOrderProducts().stream())
                .mapToDouble(orderProduct -> orderProduct.getImportPrice() * orderProduct.getQuantity())
                .sum();

        return totalRevenue - totalCost;
    }
}
