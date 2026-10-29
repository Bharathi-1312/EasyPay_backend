package com.springboot.EasyPay;

import com.springboot.EasyPay.Dto.request.PayrollComponentDto;
import com.springboot.EasyPay.Dto.response.PayrollComponentRespDto;
import com.springboot.EasyPay.enums.PayrollStatus;
import com.springboot.EasyPay.mapper.PayrollComponentMapper;
import com.springboot.EasyPay.model.Payroll;
import com.springboot.EasyPay.model.PayrollComponent;
import com.springboot.EasyPay.repository.PayrollRepository;
import com.springboot.EasyPay.repository.PayrollComponentRepository;
import com.springboot.EasyPay.service.PayrollComponentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PayrollComponentServiceTest {

    @InjectMocks
    private PayrollComponentService payrollComponentService;

    @Mock
    private PayrollRepository payrollRepository;

    @Mock
    private PayrollComponentRepository componentRepository;

    @Test
    public void addComponentTest() {
        Payroll payroll = new Payroll();
        payroll.setStatus(PayrollStatus.DRAFT);

        PayrollComponentDto dto = mock(PayrollComponentDto.class);
        PayrollComponent component = new PayrollComponent();
        PayrollComponent saved = new PayrollComponent();
        PayrollComponentRespDto response =
                mock(PayrollComponentRespDto.class);

        when(payrollRepository.findById(1L))
                .thenReturn(Optional.of(payroll));
        when(dto.amountPercentage()).thenReturn(BigDecimal.TEN);
        when(componentRepository.save(any(PayrollComponent.class)))
                .thenReturn(saved);

        try (MockedStatic<PayrollComponentMapper> mapper =
                     mockStatic(PayrollComponentMapper.class)) {

            mapper.when(() ->
                            PayrollComponentMapper.convertDtoToEntity(dto))
                    .thenReturn(component);

            mapper.when(() ->
                            PayrollComponentMapper.convertEntityToDto(saved))
                    .thenReturn(response);

            PayrollComponentRespDto result =
                    payrollComponentService.addComponent(1L, dto);

            assertSame(response, result);
            assertSame(payroll, component.getPayroll());
            verify(componentRepository).save(component);
        }
    }

    @Test
    public void addComponentTest_PayrollNotFound() {
        when(payrollRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> payrollComponentService.addComponent(
                        99L, mock(PayrollComponentDto.class)));

        verify(componentRepository, never())
                .save(any(PayrollComponent.class));
    }

    @Test
    public void addComponentTest_NotDraft() {
        Payroll payroll = new Payroll();
        payroll.setStatus(PayrollStatus.PROCESSED);

        when(payrollRepository.findById(1L))
                .thenReturn(Optional.of(payroll));

        assertThrows(IllegalStateException.class,
                () -> payrollComponentService.addComponent(
                        1L, mock(PayrollComponentDto.class)));

        verify(componentRepository, never())
                .save(any(PayrollComponent.class));
    }

    @Test
    public void addComponentTest_NegativePercentage() {
        Payroll payroll = new Payroll();
        payroll.setStatus(PayrollStatus.DRAFT);

        PayrollComponentDto dto = mock(PayrollComponentDto.class);

        when(payrollRepository.findById(1L))
                .thenReturn(Optional.of(payroll));
        when(dto.amountPercentage())
                .thenReturn(new BigDecimal("-5"));

        assertThrows(IllegalArgumentException.class,
                () -> payrollComponentService.addComponent(1L, dto));

        verify(componentRepository, never())
                .save(any(PayrollComponent.class));
    }

    @Test
    public void getComponentsTest() {
        when(payrollRepository.existsById(1L)).thenReturn(true);

        PayrollComponent component = new PayrollComponent();
        PayrollComponentRespDto response =
                mock(PayrollComponentRespDto.class);

        when(componentRepository.findByPayroll_Id(1L))
                .thenReturn(List.of(component));

        try (MockedStatic<PayrollComponentMapper> mapper =
                     mockStatic(PayrollComponentMapper.class)) {

            mapper.when(() ->
                            PayrollComponentMapper.convertEntityToDto(component))
                    .thenReturn(response);

            List<PayrollComponentRespDto> result =
                    payrollComponentService.getComponents(1L);

            assertEquals(1, result.size());
            assertSame(response, result.get(0));
        }
    }

    @Test
    public void getComponentsTest_PayrollNotFound() {
        when(payrollRepository.existsById(99L)).thenReturn(false);

        assertThrows(RuntimeException.class,
                () -> payrollComponentService.getComponents(99L));

        verify(componentRepository, never()).findByPayroll_Id(99L);
    }

    @Test
    public void deleteComponentTest() {
        Payroll payroll = new Payroll();
        payroll.setStatus(PayrollStatus.DRAFT);

        PayrollComponent component = new PayrollComponent();
        component.setPayroll(payroll);

        when(componentRepository.findById(1L))
                .thenReturn(Optional.of(component));

        payrollComponentService.deleteComponent(1L);

        verify(componentRepository).delete(component);
    }

    @Test
    public void deleteComponentTest_NotFound() {
        when(componentRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> payrollComponentService.deleteComponent(99L));

        verify(componentRepository, never())
                .delete(any(PayrollComponent.class));
    }

    @Test
    public void deleteComponentTest_NotDraft() {
        Payroll payroll = new Payroll();
        payroll.setStatus(PayrollStatus.PAID);

        PayrollComponent component = new PayrollComponent();
        component.setPayroll(payroll);

        when(componentRepository.findById(1L))
                .thenReturn(Optional.of(component));

        assertThrows(IllegalStateException.class,
                () -> payrollComponentService.deleteComponent(1L));

        verify(componentRepository, never())
                .delete(any(PayrollComponent.class));
    }
}